package pt.lasige.inputmethod.metrics.textentry.measures;

import android.util.Log;

import java.util.ArrayList;

import pt.lasige.inputmethod.metrics.textentry.datastructures.AlignedPair;
import pt.lasige.inputmethod.metrics.textentry.datastructures.ErrorRateResults;
import pt.lasige.inputmethod.metrics.textentry.datastructures.ISCharacterLevelResults;
import pt.lasige.inputmethod.metrics.textentry.datastructures.InputAction;
import pt.lasige.inputmethod.metrics.textentry.datastructures.StreamAlignment;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryCondition;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryMSD;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryParticipantSession;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial;
import pt.lasige.inputmethod.metrics.textentry.datastructures.ISCharacterLevelResults.ErrRateType;
import pt.lasige.inputmethod.metrics.textentry.datastructures.ISCharacterLevelResults.ErrType;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial.INPUT_ACTION_TYPE;

public class ComputeISCharacterLevel {
	static final private char INSERTION_CHAR = '¬';
	static final private char STREAM_ALIGN_CHAR = '_';
	static final private char UNRECOGNIZED_CHAR = 'ø';
	
	static public TextEntryCondition computeISCharacterLevelResults(TextEntryCondition condition)
	{
		/*for(TextEntryParticipantSession session : condition.getSessions())
			computeISCharacterLevelResults(session);*/
		
		computeErrors(condition);
		
		return condition;
	}
	
	static public TextEntryParticipantSession computeISCharacterLevelResults(TextEntryParticipantSession session)
	{		
		// go through all trials and compute errors
		/*for(TextEntryTrial trial : session.getTrials())
			computeISCharacterLevelResults(trial);*/
		
		// compute errors for all trials
		computeErrors(session);
		
		return session;
	}
		
	static public TextEntryTrial computeISCharacterLevelResults(TextEntryTrial trial)
	{
		if(!trial.getTrialResults().COMPUTE_ERRORS) return trial;
		
		String inputstream = trial.getInputStream();
		String required = trial.getRequiredSentence();
		String transcribed = trial.getTranscribedSentence();
		
		// 1 - flag the input stream
		String flagedIS = flagInputStream(inputstream);
		
		// 2 - compute MSD matrix
		TextEntryMSD msd = computeMSD(required, transcribed);
		
		// 3 - compute set of optimal alignments
		ArrayList<AlignedPair> alignments = computeOptimalAlignments(trial.getRequiredSentence(), trial.getTranscribedSentence(), msd.getMatrix(), 
				trial.getRequiredSentence().length(), trial.getTranscribedSentence().length(), 
				"", "", new ArrayList<AlignedPair>());
		
		// this condition is just for rare cases where transcribed is empty
		if(alignments.size() == 0)
		{
			String s = new String();
			for(int i = 0; i < required.length(); i++) s += INSERTION_CHAR; 
			alignments.add(new AlignedPair(required, s));
		}
		
		// 4 - stream align IS with P and T
		ArrayList<StreamAlignment> streamAlignments = computeStreamAlignments(inputstream, flagedIS, alignments);
		
		// 5 - assign position values to characters in the input stream
		streamAlignments = assignPositionValues(streamAlignments);
		
		// 6 - detected and classify errors
		ISCharacterLevelResults results = computeErrors(trial, streamAlignments);
		
		// set results
		results.setMSD(msd);
		results.setStreamAlignments(streamAlignments);
		trial.getTrialResults().setCharacterLevelResults(results);
		
		return trial;
	}	

	// flag input stream
	static protected String flagInputStream(String inputstream)
	{
		StringBuilder flagedIS = new StringBuilder(inputstream);
		
		// initialize array 
		for(int i = 0; i < inputstream.length(); i++) flagedIS.setCharAt(i, '0');;
		
		int count = 0;
		
		for(int i = inputstream.length() - 1; i >= 0; i--)
		{
			if(inputstream.charAt(i) == '<' && i > 0 && inputstream.charAt(i - 1) == '<')
			{
				// increment count when encounter a backspace
				count++;
				i--; // subtract i because backspace has 2 chars '<<'
			}
			else if(inputstream.charAt(i) == UNRECOGNIZED_CHAR)
			{
				continue; // do nothing 
			}
			else //if(isLetter(inputstream.charAt(i)))
			{
				// is letter
				if(count == 0) flagedIS.setCharAt(i, '1');
				else count = count - 1 < 0 ? 0 : count - 1 ;
			}
			/*else
			{
				// all other chars
				count = count - 1 < 0 ? 0 : count - 1 ;
			}*/
		}
		
		return flagedIS.toString();
	}
	
	static protected TextEntryMSD computeMSD(String P, String T)
	{
		TextEntryMSD msd = new TextEntryMSD();
		
		int[][] d = new int[P.length() + 1][T.length() + 1];
		msd.setMatrix(d);
		
		if (P.length() == 0) return msd;
	    if (T.length() == 0) { d[P.length()][T.length()] = P.length(); return msd; } //assume that sentence is all wrong
	    
		for(int i = 0; i <= P.length(); i++) d[i][0] = i;
		for(int j = 0; j <= T.length(); j++) d[0][j] = j;
		
		for(int i = 1; i <= P.length(); i++)
			for(int j = 1; j <= T.length(); j++)
			{
				int cost = (T.charAt(j - 1) == P.charAt(i - 1)) ? 0 : 1;
				
				int omission = d[i - 1][j] + 1;
	            int insertion = d[i][j - 1] + 1;
	            int substitution = d[i - 1][j - 1] + cost;
     
	            d[i][j] = Math.min(Math.min(omission, insertion), substitution);
			}
		
		return msd;
	}
	
	static protected ArrayList<AlignedPair> computeOptimalAlignments(String P, String T, int[][] d,
			int x, int y, String P_, String T_, ArrayList<AlignedPair> alignments)
	{
		if (x == 0 && y == 0)
        { 
			//add new aligned pair
            AlignedPair align = new AlignedPair(P_, T_);
            alignments.add(align);
            return alignments;
        }
		
		if (x > 0 && y > 0)
        {
            // correct - no error
            if (d[x][y] == d[x - 1][y - 1] && P.charAt(x - 1) == T.charAt(y - 1))
            	alignments = computeOptimalAlignments(P, T, d, x - 1, y - 1, P.charAt(x - 1) + P_,
                		T.charAt(y - 1) + T_, alignments);

            // substitution error
            if (d[x][y] == d[x - 1][y - 1] + 1)
            	alignments = computeOptimalAlignments(P, T, d, x - 1, y - 1, P.charAt(x - 1) + P_, 
            			T.charAt(y - 1) + T_, alignments);
        }
		
		// deletion error
        if (x > 0 && d[x][y] == d[x - 1][y] + 1)
        	alignments = computeOptimalAlignments(P, T, d, x - 1, y, 
        			P.charAt(x - 1) + P_, String.valueOf(INSERTION_CHAR) + T_, alignments);
        
        // insertion error
        if (y > 0 && d[x][y] == d[x][y - 1] + 1)
        	alignments = computeOptimalAlignments(P, T, d, x, y - 1, 
        			String.valueOf(INSERTION_CHAR) + P_, T.charAt(y - 1) + T_, alignments);
        
		return alignments;
	}
	
	static protected ArrayList<StreamAlignment> computeStreamAlignments(String inputstream, String flaggedIS,
			ArrayList<AlignedPair> alignments)
	{
		ArrayList<StreamAlignment> streamAlignments = new ArrayList<StreamAlignment>();
		
		for(AlignedPair pair : alignments)
		{
			StringBuilder IS_ = new StringBuilder(inputstream);
			StringBuilder flaggedIS_ = new StringBuilder(flaggedIS);
			StringBuilder P_ = new StringBuilder(pair.getRequired());
			StringBuilder T_ = new StringBuilder(pair.getTranscribed());
			
			for(int i = 0; i < Math.max(T_.length(), IS_.length()); i++)
			{
				if(i < T_.length() && T_.charAt(i) == INSERTION_CHAR)
				{
					if(i >= IS_.length())
					{
						IS_.append(STREAM_ALIGN_CHAR);
						flaggedIS_.append('0');
					}
					else
					{
						IS_.insert(i, STREAM_ALIGN_CHAR);
						flaggedIS_.insert(i, '0');
					}
				}
				else if(i < flaggedIS_.length() && flaggedIS_.charAt(i) == '0')
				{
					P_.insert(i, STREAM_ALIGN_CHAR);
					T_.insert(i, STREAM_ALIGN_CHAR);
				}
			}
			
			streamAlignments.add(new StreamAlignment(P_.toString(), T_.toString(), IS_.toString(), flaggedIS_.toString()));
		}
		
		return streamAlignments;
	}
	
	static protected ArrayList<StreamAlignment> assignPositionValues(ArrayList<StreamAlignment> streamalignments)
	{
		for(StreamAlignment alignment : streamalignments)
		{
			String IS = alignment.getInputstream();
			String flaggedIS = alignment.getFlaggedInputstream();
			int pos = 0;
			String positionValue = "";
			
			for(int i = 0; i < IS.length(); i++)
			{
				if(flaggedIS.charAt(i) == '1')
				{
					positionValue += pos;
					pos = 0;
				}
				else
				{
					if(IS.charAt(i) == '<' && (i + 1) < IS.length() && IS.charAt(i + 1) == '<' && pos > 0)
					{
						pos--;
						positionValue += pos;
						i++;
					}
					
					positionValue += pos;
					
					if(isLetter(IS.charAt(i)))
						pos++;
				}
			}
			
			alignment.setPositionValues(positionValue);
			
		}
		
		return streamalignments;
	}
	
	static protected ISCharacterLevelResults computeErrors(TextEntryCondition condition)
	{
		ISCharacterLevelResults results = condition.getConditionResults().getCharacterLevelResults();
		
		// sum all counts
		
		for(TextEntryParticipantSession session : condition.getSessions())
		{
			ArrayList<StreamAlignment> alignments = session.getSessionResults().getCharacterLevelResults().getStreamAlignments();
			results.getStreamAlignments().addAll(alignments);
			
			// character counts
			// presented
			float[] conditionPresented = results.getPresented();
			float[] sessionPresented = session.getSessionResults().getCharacterLevelResults().getPresented();
			results.setPresented(sumArrays(conditionPresented, sessionPresented));
			
			// transcribed
			float[] conditionTranscribed = results.getTranscribed();
			float[] sessionTranscribed = session.getSessionResults().getCharacterLevelResults().getTranscribed();
			results.setTranscribed(sumArrays(conditionTranscribed, sessionTranscribed));
			
			// entered
			float[] conditionEntered = results.getEntered();
			float[] sessionEntered = session.getSessionResults().getCharacterLevelResults().getEntered();
			results.setEntered(sumArrays(conditionEntered, sessionEntered));
			
			// intended
			float[] conditionIntended = results.getIntended();
			float[] sessionIntended = session.getSessionResults().getCharacterLevelResults().getIntended();
			results.setIntended(sumArrays(conditionIntended, sessionIntended));
						
			// correct
			float[] conditionCorrect = results.getCorrect();
			float[] sessionCorrect = session.getSessionResults().getCharacterLevelResults().getCorrect();
			results.setCorrect(sumArrays(conditionCorrect, sessionCorrect));
						
			// NRs
			float[] conditionNR = results.getNR();
			float[] sessionNR = session.getSessionResults().getCharacterLevelResults().getNR();
			results.setNR(sumArrays(conditionNR, sessionNR));
			
			// confusion matrix
			float[][] conditionMatrix = results.getConfusionMatrix();
			float[][] sessionMatrix = session.getSessionResults().getCharacterLevelResults().getConfusionMatrix();
			results.setConfusionMatrix(sumMatrices(conditionMatrix, sessionMatrix));
						
			// error rate counts
			ErrorRateResults[] conditionSubstitutions = results.getSubstitutions();
			ErrorRateResults[] sessionSubstitutions = session.getSessionResults().getCharacterLevelResults().getSubstitutions();
			
			ErrorRateResults[] conditionOmissions = results.getOmissions();
			ErrorRateResults[] sessionOmissions = session.getSessionResults().getCharacterLevelResults().getOmissions();
			
			ErrorRateResults[] conditionInsertions = results.getInsertions();
			ErrorRateResults[] sessionInsertions = session.getSessionResults().getCharacterLevelResults().getInsertions();
			
			ErrorRateResults[] conditionNoErrors = results.getNoErrors();
			ErrorRateResults[] sessionNoErrors = session.getSessionResults().getCharacterLevelResults().getNoErrors();
			
			for(int i = 0; i < conditionSubstitutions.length; i++)
			{
				// substitution error count
				conditionSubstitutions[i].addCorrCount(sessionSubstitutions[i].getCorrCount());
				conditionSubstitutions[i].addUncCount(sessionSubstitutions[i].getUncCount());
				conditionSubstitutions[i].addNonRecCount(sessionSubstitutions[i].getNRCount());
				
				// omission error count
				conditionOmissions[i].addCorrCount(sessionOmissions[i].getCorrCount());
				conditionOmissions[i].addUncCount(sessionOmissions[i].getUncCount());
				conditionOmissions[i].addNonRecCount(sessionOmissions[i].getNRCount());
				
				// insertion error count
				conditionInsertions[i].addCorrCount(sessionInsertions[i].getCorrCount());
				conditionInsertions[i].addUncCount(sessionInsertions[i].getUncCount());
				conditionInsertions[i].addNonRecCount(sessionInsertions[i].getNRCount());
				
				// noerror error count
				conditionNoErrors[i].addCorrCount(sessionNoErrors[i].getCorrCount());
				conditionNoErrors[i].addUncCount(sessionNoErrors[i].getUncCount());
				conditionNoErrors[i].addNonRecCount(sessionNoErrors[i].getNRCount());
			}
		}
		
		// compute error rates
		results.computeErrors();
		
		return results;
	}
	
	static protected ISCharacterLevelResults computeErrors(TextEntryParticipantSession session)
	{
		ISCharacterLevelResults results = session.getSessionResults().getCharacterLevelResults();
		
		// sum all counts
		for(TextEntryTrial trial : session.getTrials())
		{
			if(!trial.getTrialResults().COMPUTE_ERRORS) continue;
			
			ArrayList<StreamAlignment> alignments = trial.getTrialResults().getCharacterLevelResults().getStreamAlignments();
			results.getStreamAlignments().addAll(alignments);
			
			// character counts
			// presented
			float[] sessionPresented = results.getPresented();
			float[] trialPresented = trial.getTrialResults().getCharacterLevelResults().getPresented();
			results.setPresented(sumArrays(sessionPresented, trialPresented));
			
			// transcribed
			float[] sessionTranscribed = results.getTranscribed();
			float[] trialTranscribed = trial.getTrialResults().getCharacterLevelResults().getTranscribed();
			results.setTranscribed(sumArrays(sessionTranscribed, trialTranscribed));
			
			// entered
			float[] sessionEntered = results.getEntered();
			float[] trialEntered = trial.getTrialResults().getCharacterLevelResults().getEntered();
			results.setEntered(sumArrays(sessionEntered, trialEntered));
			
			// intended
			float[] sessionIntended = results.getIntended();
			float[] trialIntended = trial.getTrialResults().getCharacterLevelResults().getIntended();
			results.setIntended(sumArrays(sessionIntended, trialIntended));
			
			// correct
			float[] sessionCorrect = results.getCorrect();
			float[] trialCorrect = trial.getTrialResults().getCharacterLevelResults().getCorrect();
			results.setCorrect(sumArrays(sessionCorrect, trialCorrect));
			
			// NRs
			float[] sessionNR = results.getNR();
			float[] trialNR = trial.getTrialResults().getCharacterLevelResults().getNR();
			results.setNR(sumArrays(sessionNR, trialNR));
			
			// confusion matrix
			float[][] sessionMatrix = results.getConfusionMatrix();
			float[][] trialMatrix = trial.getTrialResults().getCharacterLevelResults().getConfusionMatrix();
			results.setConfusionMatrix(sumMatrices(sessionMatrix, trialMatrix));
			
			// errors count
			ErrorRateResults[] sessionSubstitutions = results.getSubstitutions();
			ErrorRateResults[] trialSubstitutions = trial.getTrialResults().getCharacterLevelResults().getSubstitutions();
			
			ErrorRateResults[] sessionOmissions = results.getOmissions();
			ErrorRateResults[] trialOmissions = trial.getTrialResults().getCharacterLevelResults().getOmissions();
			
			ErrorRateResults[] sessionInsertions = results.getInsertions();
			ErrorRateResults[] trialInsertions = trial.getTrialResults().getCharacterLevelResults().getInsertions();
			
			ErrorRateResults[] sessionNoErrors = results.getNoErrors();
			ErrorRateResults[] trialNoErrors = trial.getTrialResults().getCharacterLevelResults().getNoErrors();
			
			for(int i = 0; i < sessionSubstitutions.length; i++)
			{
				// substitution error count
				sessionSubstitutions[i].addCorrCount(trialSubstitutions[i].getCorrCount());
				sessionSubstitutions[i].addUncCount(trialSubstitutions[i].getUncCount());
				sessionSubstitutions[i].addNonRecCount(trialSubstitutions[i].getNRCount());
				
				// omission error count
				sessionOmissions[i].addCorrCount(trialOmissions[i].getCorrCount());
				sessionOmissions[i].addUncCount(trialOmissions[i].getUncCount());
				sessionOmissions[i].addNonRecCount(trialOmissions[i].getNRCount());
				
				// insertion error count
				sessionInsertions[i].addCorrCount(trialInsertions[i].getCorrCount());
				sessionInsertions[i].addUncCount(trialInsertions[i].getUncCount());
				sessionInsertions[i].addNonRecCount(trialInsertions[i].getNRCount());
				
				// noerror error count
				sessionNoErrors[i].addCorrCount(trialNoErrors[i].getCorrCount());
				sessionNoErrors[i].addUncCount(trialNoErrors[i].getUncCount());
				sessionNoErrors[i].addNonRecCount(trialNoErrors[i].getNRCount());
			}
		}
		
		// after having all counts we can compute the error rates
		results.computeErrors();
		
		return results;
	}
	
	static protected float[][] sumMatrices(float[][] a, float[][]b)
	{
		float[][] c = new float[a.length][a.length];
		
		if(a.length != b.length) return c;
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a.length; j++)
				c[i][j] = a[i][j] + b[i][j];
		
		return c;
	}
	
	static protected float[] sumArrays(float[] a, float[] b)
	{
		float[] c = new float[a.length];
		
		if(a.length != b.length) return c;
		for(int i = 0; i < a.length; i++)
			c[i] = a[i] + b[i];
		
		return c;
	}
	
	static protected ISCharacterLevelResults computeErrors(TextEntryTrial trial, ArrayList<StreamAlignment> streamAlignments)
	{
		// results
		ISCharacterLevelResults results = new ISCharacterLevelResults();
		
		for(StreamAlignment alignment : streamAlignments)
		{
			String P = alignment.getRequired();
			String T = alignment.getTranscribed();
			String IS = alignment.getInputstream();
			String flaggedIS = alignment.getFlaggedInputstream();
			String positionValues = alignment.getPositionValues();
			
			// get just the ENTER actions
			/*ArrayList<InputAction> IS_inputactions = getInputStreamActions(trial.getInputActions());
			
			// get just the transcribed actions
			ArrayList<InputAction> T_inputactions = getTranscribedActions(IS_inputactions);
			
			// align both input action lists
			IS_inputactions = alignInputActions(IS_inputactions, IS);
			T_inputactions = alignInputActions(T_inputactions, T);*/

			
			int a = 0;
			
			for(int b = 0; b < IS.length(); b++)
			{				
				if(T.charAt(b) == INSERTION_CHAR)
				{
					// add uncorrected-omissions (P[b])
					results.addCharacterError(ErrRateType.UNCORRECTED, ErrType.OMISSION, P.charAt(b), INSERTION_CHAR);
				}
				else if(flaggedIS.charAt(b) == '1' || b == IS.length() - 1)
				{
					ArrayList<Integer> M = new ArrayList<Integer>(); // corrected omissions
					ArrayList<Integer> I = new ArrayList<Integer>(); // corrected insertions
					
					for(int i = a; i < b - 1; i++) // iterate over substring between flags
					{
						Integer v = Integer.parseInt(String.valueOf(positionValues.charAt(i)));
						if(withinBounds(IS, i) && IS.charAt(i) == '<' && withinBounds(IS, i+1) && IS.charAt(i + 1) == '<')
						{
							if(M.contains(v)) M.remove(v);
							if(I.contains(v)) I.remove(v);
							i++;
						}
						else if(withinBounds(IS, i) && IS.charAt(i) != STREAM_ALIGN_CHAR)
						{
							ComputeISCharacterLevel iscl = new ComputeISCharacterLevel();
							
							int target = lookAhead(P, b, v + M.size() - I.size(), iscl.new IsLetterCondition());
							if(withinBounds(IS, i) && IS.charAt(i) == UNRECOGNIZED_CHAR)
							{ // IS[i] is an unrecognized char
								if(target >= P.length())
								{
									// add nonrec-insertion(ø)
									results.addCharacterError(ErrRateType.NONRECOGNITION, ErrType.INSERTION, 
											INSERTION_CHAR, UNRECOGNIZED_CHAR);
								}
								else
								{
									// add nonrec-substitution(P[target], ø)
									results.addCharacterError(ErrRateType.NONRECOGNITION, ErrType.SUBSTITUTION, 
											P.charAt(target), UNRECOGNIZED_CHAR);
								}
							}
							else // IS[i] is a letter
							{
								int nextP = lookAhead(P, target, 1, iscl.new IsLetterCondition());
								int prevP = lookBehind(P, target, 1, iscl.new IsLetterCondition());
								int nextIS = lookAhead(IS, i, 1, iscl.new IsNot(new char[]{UNRECOGNIZED_CHAR, STREAM_ALIGN_CHAR}));
								int prevIS = lookBehind(IS, i, 1, iscl.new IsNot(new char[]{STREAM_ALIGN_CHAR}));
								
								if(withinBounds(IS, i) && withinBounds(P, target) && IS.charAt(i) == P.charAt(target))
								{
									// add corrected-noerror(IS[i])
									results.addCharacterError(ErrRateType.CORRECTED, ErrType.NOERROR,
											IS.charAt(i), IS.charAt(i));
								}
								else if(target >= P.length() || 
										(withinBounds(IS, nextIS) && withinBounds(P, target) && IS.charAt(nextIS) == P.charAt(target)) ||
										(withinBounds(IS, prevIS) && withinBounds(IS, i) && IS.charAt(prevIS) == IS.charAt(i) && 
										withinBounds(P, prevP) && IS.charAt(prevIS) == P.charAt(prevP)))
								{
									// add corrected-insertion(IS[i])
									results.addCharacterError(ErrRateType.CORRECTED, ErrType.INSERTION,
											INSERTION_CHAR, IS.charAt(i));
									I.add(v); // track this corrected insertion
								}
								else if(withinBounds(IS, i) && withinBounds(P, nextP) && 
										IS.charAt(i) == P.charAt(nextP) && withinBounds(T, target) && isLetter(T.charAt(target)))
								{
									// add corrected-omission(P[target])
									results.addCharacterError(ErrRateType.CORRECTED, ErrType.OMISSION,
											P.charAt(target), INSERTION_CHAR);
									
									// add corrected-noerror(IS[i])
									results.addCharacterError(ErrRateType.CORRECTED, ErrType.NOERROR,
											IS.charAt(i), IS.charAt(i));
									
									M.add(v); // track this corrected omission
								}
								else
								{
									// add corrected-substitution(P[target], IS[i])
									results.addCharacterError(ErrRateType.CORRECTED, ErrType.SUBSTITUTION,
											P.charAt(target), IS.charAt(i));
								}
							}
						}
					} // end for - i from a to b - 1
					
					if(withinBounds(P, b) && P.charAt(b) == INSERTION_CHAR)
					{
						// add uncorrected-insertion(T[b])
						results.addCharacterError(ErrRateType.UNCORRECTED, ErrType.INSERTION,
								INSERTION_CHAR, T.charAt(b));
					}
					else if(withinBounds(P, b) && withinBounds(T, b) && P.charAt(b) != T.charAt(b))
					{
						// add uncorrected-substitution(P[b], T[b])
						results.addCharacterError(ErrRateType.UNCORRECTED, ErrType.SUBSTITUTION,
								P.charAt(b), T.charAt(b));
					}
					else if(withinBounds(P, b) && P.charAt(b) != STREAM_ALIGN_CHAR)
					{
						// add uncorrected-noerror(T[b])
						results.addCharacterError(ErrRateType.UNCORRECTED, ErrType.NOERROR,
								P.charAt(b), T.charAt(b));
					}
					else if(withinBounds(IS, b) && IS.charAt(b) == UNRECOGNIZED_CHAR)
					{
						// add nonrec-insertion(øßß)
						results.addCharacterError(ErrRateType.NONRECOGNITION, ErrType.INSERTION,
								INSERTION_CHAR, UNRECOGNIZED_CHAR);
					}
					a = b + 1;
				}
			}
			
			results.computePresented(trial.getRequiredSentence());
		}
		
		// update intended character for all input actions
		updateIntendedCharacters(trial.getInputActions());
		
		results.averageErrorRateCounts((float)streamAlignments.size());
		results.averageCharacterCounts((float)streamAlignments.size());
		results.computeErrors();
		
		return results;
	}
	
	static protected void updateIntendedCharacters(ArrayList<InputAction> actions)
	{
		for(int i = 0; i < actions.size(); i++)
		{
			InputAction action = actions.get(i);
			
			if(action.Type == INPUT_ACTION_TYPE.ENTER)
			{
				// when the character is backspace we need to assume it was the intended character
				// this is an assumption of the IS text-entry measures
				if(action.Character.equalsIgnoreCase("<<"))
				{
					action.Intended.add("<<");
				}
				
				// update all previous input actions
				for(int j = i - 1; j >= 0; j--)
				{
					if(actions.get(j).Type == INPUT_ACTION_TYPE.ENTER) break;
					actions.get(j).Character = action.Character;
					actions.get(j).Intended = action.Intended;
					
				}
			}
		}
	}
	
	// get the input action index that matches index i in inputstream (because delete char is actually 2 chars)
	static protected int getInputActionIndex(int index, String is)
	{
		int count = 0;
		for(int i = 0; i < index && i < is.length(); i++)
		{
			if(is.charAt(i) == '<' && i + 1 < index && i + 1 < is.length() && is.charAt(i + 1) == '<')
			{
				i++;
				count++;
			}
		}
		
		return index - count;
	}
	
	// adds STREAM ALIGN CHARS to input actions list
	static protected ArrayList<InputAction> alignInputActions(ArrayList<InputAction> actions, String s)
	{
		int s_index = 0;
		int actions_index = 0;
		
		for(; s_index < s.length(); s_index++, actions_index++)
		{
			if(s.charAt(s_index) == '<' && s_index + 1 < s.length() && s.charAt(s_index + 1) == '<')
			{
				// backspace, do nothing
				if(!(s.substring(s_index, s_index + 2)).equalsIgnoreCase(actions.get(actions_index).Character))
				{
					//System.out.println("Alignment error: different characters");
				}
				s_index++;
			}
			else if(s.charAt(s_index) == STREAM_ALIGN_CHAR)
			{
				actions.add(actions_index, new InputAction(INPUT_ACTION_TYPE.ENTER, String.valueOf(STREAM_ALIGN_CHAR), null));
			}
			else if(s.charAt(s_index) == INSERTION_CHAR)
			{
				actions.add(actions_index, new InputAction(INPUT_ACTION_TYPE.ENTER, String.valueOf(INSERTION_CHAR), null));
			}
			else if(!String.valueOf(s.charAt(s_index)).equalsIgnoreCase(actions.get(actions_index).Character))
			{
				// otherwise characters need to match
				//System.out.println("Alignment error: different characters");
			}
		}
		
		if(s.length() != actions.size() + (s_index - actions_index))
			System.out.println("Alignment error: different sizes");
		
		return actions;
	}
	
	// get enter actions
	static protected ArrayList<InputAction> getInputStreamActions(ArrayList<InputAction> actions)
	{
		if(actions == null) return null;
		ArrayList<InputAction> is_actions = new ArrayList<InputAction>();
		
		for(InputAction action : actions)
			if(action.Type == INPUT_ACTION_TYPE.ENTER)
				is_actions.add(action);
		
		return is_actions;
	}
	
	// get transcribed input actions
	static protected ArrayList<InputAction> getTranscribedActions(ArrayList<InputAction> inputstream_actions)
	{
		if(inputstream_actions == null) return null;
		ArrayList<InputAction> transcribed_actions = new ArrayList<InputAction>();
		
		// calculate transcribed actions for trial
		for(int i = 0; i < inputstream_actions.size(); i++)
		{
			if(!inputstream_actions.get(i).Character.equalsIgnoreCase("<<"))
			{
				transcribed_actions.add(inputstream_actions.get(i));
			}
			else
			{
				if(transcribed_actions.size() > 0)
					transcribed_actions.remove(transcribed_actions.size() - 1);
			}
		}
		
		return transcribed_actions;
	}
	
	static protected int lookAhead(String S, int start, int count, ConditionFunction f)
	{
		int index = start;
		
		while(index > 0 && index < S.length() && !f.calc(S.charAt(index))) 
			index++; // proceed until the condition is met
		
		while(count > 0 && index < S.length())
		{
			index++;
			if(index >= S.length()) break;
			else if (f.calc(S.charAt(index)))
				count--;
		}
		
		return index;
	}
	
	static protected int lookBehind(String S, int start, int count, ConditionFunction f)
	{
		int index = start;
		
		while(index > 0 && index < S.length() && !f.calc(S.charAt(index)))
			index--; // go back until the condition is met
		
		while(count > 0 && index >=0)
		{
			index--;
			if(index < 0) break;
			else if(f.calc(S.charAt(index)))
				count--;
		}
		
		return index;
	}
	
	static protected boolean isLetter(char c)
	{
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ' || c == '\n';
	}
	
	static protected boolean withinBounds(String s, int index)
	{
		return index >= 0 && index < s.length();
	}
	
	/**
	 * 
	 * HELPER CLASSES / INTERFACES
	 *
	 */
	
	public interface ConditionFunction{
		public boolean calc(char c);
	}
	
	public class IsLetterCondition implements ConditionFunction
	{
		public boolean calc(char c)
		{
			return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ';
		}
	}
	
	public class IsNot implements ConditionFunction
	{
		private char[] mChars;
		public IsNot(char[] chars)
		{
			mChars = chars;
		}
		
		public boolean calc(char c)
		{
			for(char ch : mChars)
			{
				if(ch == c) return false;
			}
			return true;
		}
	}
	
}
