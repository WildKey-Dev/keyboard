package pt.lasige.cns.study.inputmethod.metrics.textentry.measures;

import java.util.ArrayList;

import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.AlignedPair;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.CharacterLevelResults;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryCondition;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryMSD;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryParticipantSession;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class ComputeCharacterLevel {

	static public TextEntryTrial computeTrialCharacterLevelResults(TextEntryTrial trial)
	{
		/*CharacterLevelResults results = trial.getTrialResults().getCharacterLevelResults();
		
		// compute msd
		TextEntryMSD msd = computeMSD(trial.getRequiredSentence(), trial.getTranscribedSentence());
		int[][] d = msd.getMatrix();
		
		// compute optimal alignments
		ArrayList<AlignedPair> alignments = new ArrayList<AlignedPair>();
		alignments = computeOptimalAlignments(trial.getRequiredSentence(), trial.getTranscribedSentence(), d, 
				trial.getRequiredSentence().length(), trial.getTranscribedSentence().length(), 
				"", "", alignments);
		results.setOptimalAlignments(alignments);
				
		results = ComputeErrors(results);*/
		
		return trial;
	}
	
	static protected TextEntryParticipantSession computeSessionCharacterLevelResults(TextEntryParticipantSession session)
	{
		/*CharacterLevelResults results = session.getSessionResults().getCharacterLevelResults();
		
		for(TextEntryTrial trial : session.getTrials())
		{
			// compute MSD
			TextEntryMSD msd = computeMSD(trial.getRequiredSentence(), trial.getTranscribedSentence());			
			int[][] d = msd.getMatrix();
						
			// compute optimal alignments
			ArrayList<AlignedPair> alignments = new ArrayList<AlignedPair>();
			alignments = computeOptimalAlignments(trial.getRequiredSentence(), trial.getTranscribedSentence(), d, 
					trial.getRequiredSentence().length(), trial.getTranscribedSentence().length(), 
					"", "", alignments);
						
			// add new alignments
			results.getOptimalAlignments().addAll(alignments);
		}
		
		results = ComputeErrors(results);*/
		
		return session;
	}
	
	static protected TextEntryCondition computeConditionCharacterLevelResults(TextEntryCondition condition)
	{
		/*CharacterLevelResults results = condition.getConditionResults().getCharacterLevelResults();
		
		for(TextEntryParticipantSession session : condition.getSessions())
		{
			for(TextEntryTrial trial : session.getTrials())
			{
				// compute MSD
				TextEntryMSD msd = computeMSD(trial.getRequiredSentence(), trial.getTranscribedSentence());			
				int[][] d = msd.getMatrix();
							
				// compute optimal alignments
				ArrayList<AlignedPair> alignments = new ArrayList<AlignedPair>();
				alignments = computeOptimalAlignments(trial.getRequiredSentence(), trial.getTranscribedSentence(), d, 
						trial.getRequiredSentence().length(), trial.getTranscribedSentence().length(), 
						"", "", alignments);
							
				// add new alignments
				results.getOptimalAlignments().addAll(alignments);
			}
		}
		results = ComputeErrors(results);*/
		return condition;
	}
	
	/*
	 * HELPER FUNCTIONS
	 */
	static protected char INSERTION_CHAR = '>';
	
	// TODO: review das, "das
	static public TextEntryMSD computeMSD(String requiredSentence, String transcribedSentence)
	{

		int n = requiredSentence.length();
		int m = transcribedSentence.length();
			
		int[][] d = new int[n + 1][m + 1];
		TextEntryMSD msd = new TextEntryMSD();
		msd.setMatrix(d);
			
		String[][] types = new String[n + 1][m + 1];
			
		// step 1
	    if (n == 0) { return msd; }

	    if (m == 0) { d[n][m] = n; msd.setMatrix(d); return msd; } //assume that sentence is all wrong

	    // step 2
	    for (int i = 0; i <= n; types[i][0] = String.valueOf(INSERTION_CHAR), d[i][0] = i++) { }

	    for (int j = 0; j <= m; types[0][j] = String.valueOf(INSERTION_CHAR), d[0][j] = j++) { }

	    // step 3
	    for (int i = 1; i <= n; i++)
	    {
	    	// step 4
	        for (int j = 1; j <= m; j++)
	        {
	        	// step 5
	            // if cost = 1 then it is an error
	            int cost = (transcribedSentence.charAt(j - 1) == requiredSentence.charAt(i - 1)) ? 0 : 1;

	            // step 6
	            if(cost == 0)
	            {
	              	d[i][j] = d[i - 1][j - 1];
	               	types[i][j] = "c";
	            }
	            else
	            {
	              	int omission = d[i - 1][j] + 1;
		            int insertion = d[i][j - 1] + 1;
		            int substitution = d[i - 1][j - 1] + 1;
		                
		            d[i][j] = Math.min( Math.min(omission, insertion), substitution);
	            }
	        }
	     }
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < d[i].length; j++) {
				sb.append(d[i][j]).append(" ");
			}
			sb = new StringBuilder();
		}

		// changed
	    msd.setCorrect(m - d[n][m]);
	    return msd;
	}
	
	static public float computeMWDErrRate(String required, String transcribed)
	{
		String[] requiredWords = required.split(" ");
		int n = requiredWords.length;
		String[] transcribedWords = transcribed.split(" "); 
		int m = transcribedWords.length;
		
		int[][] d = new int[n + 1][m + 1];
		
		TextEntryMSD msd = new TextEntryMSD();
		msd.setMatrix(d);
		
		// step 1
	    if (n == 0) { return m; }
	    if (m == 0) { return n; } //assume that sentence is all wrong
		
	    // step 2
	    for (int i = 0; i <= n; d[i][0] = i++);
	    for (int j = 0; j <= m; d[0][j] = j++);

	    // step 3
	    for (int i = 1; i <= n; i++)
	    {
	    	// step 4
	    	for (int j = 1; j <= m; j++)
	    	{
	    		// step 5
	    		// if cost = 1 then it is an error
	    		int cost = (transcribedWords[j - 1].equalsIgnoreCase(requiredWords[i - 1])) ? 0 : 1;

	    		// step 6
	    		if(cost == 0)
	    		{
	    			d[i][j] = d[i - 1][j - 1];
	    		}
	    		else
	    		{
	    			int omission = d[i - 1][j] + 1;
	    			int insertion = d[i][j - 1] + 1;
	    			int substitution = d[i - 1][j - 1] + 1;

	    			d[i][j] = Math.min( Math.min(omission, insertion), substitution);
	    		}
	    	}
	    }
	    
		return (float) d[n][m] / (float) Math.max(n, m);
	}
	
	final static protected int NCHARS = 27; // all letters + space
	
	// Compute optimal alignments [MacKenzie and Soukoreff, 2002]
	static protected ArrayList<AlignedPair> computeOptimalAlignments(String requiredSentence, String transcribedSentence, int[][] msdMatrix,
			int requiredSentenceLength, int transcribedSentenceLength, String auxRequiredSentence, String auxTranscribedSentence,
			ArrayList<AlignedPair> alignments)
	{
		if (requiredSentenceLength == 0 && transcribedSentenceLength == 0)
        { 
			//add new aligned pair
            AlignedPair align = new AlignedPair(auxRequiredSentence, auxTranscribedSentence);
            alignments.add(align);
            return alignments;
        }

        if (requiredSentenceLength > 0 && transcribedSentenceLength > 0)
        {
            // correct � no error
            if (msdMatrix[requiredSentenceLength][transcribedSentenceLength] == msdMatrix[requiredSentenceLength - 1][transcribedSentenceLength - 1] && 
            		requiredSentence.charAt(requiredSentenceLength - 1) == transcribedSentence.charAt(transcribedSentenceLength - 1))
            {
            	alignments = computeOptimalAlignments(requiredSentence, transcribedSentence, msdMatrix, requiredSentenceLength - 1,
                		transcribedSentenceLength - 1, requiredSentence.charAt(requiredSentenceLength - 1) + auxRequiredSentence,
                		transcribedSentence.charAt(transcribedSentenceLength - 1) + auxTranscribedSentence, alignments);
            }

            // substitution error
            if (msdMatrix[requiredSentenceLength][transcribedSentenceLength] == msdMatrix[requiredSentenceLength - 1][transcribedSentenceLength - 1] + 1)
            {
            	alignments = computeOptimalAlignments(requiredSentence, transcribedSentence, msdMatrix, requiredSentenceLength - 1,
            			transcribedSentenceLength - 1, requiredSentence.charAt(requiredSentenceLength - 1) + auxRequiredSentence, 
            			transcribedSentence.charAt(transcribedSentenceLength - 1) + auxTranscribedSentence, alignments);
            }
        }

        // deletion error
        if (requiredSentenceLength > 0 && 
        		msdMatrix[requiredSentenceLength][transcribedSentenceLength] == msdMatrix[requiredSentenceLength - 1][transcribedSentenceLength] + 1)
        {
        	alignments = computeOptimalAlignments(requiredSentence, transcribedSentence, msdMatrix, requiredSentenceLength - 1, 
            		transcribedSentenceLength, requiredSentence.charAt(requiredSentenceLength - 1) + auxRequiredSentence, String.valueOf(INSERTION_CHAR) + auxTranscribedSentence, 
            		alignments);
        }

        // insertion error
        if (transcribedSentenceLength > 0 && 
        		msdMatrix[requiredSentenceLength][transcribedSentenceLength] == msdMatrix[requiredSentenceLength][transcribedSentenceLength - 1] + 1)
        {
        	alignments = computeOptimalAlignments(requiredSentence, transcribedSentence, msdMatrix, requiredSentenceLength, 
        			transcribedSentenceLength - 1, String.valueOf(INSERTION_CHAR) + auxRequiredSentence,
        			transcribedSentence.charAt(transcribedSentenceLength - 1) + auxTranscribedSentence, alignments);
        }

		return alignments;
	}
		
	static protected CharacterLevelResults ComputeErrors(CharacterLevelResults results)
	{
		// compute character count (aka update confusion matrix and error count)
		results = computeCharacterCount(results);
			
		// compute character-level error rates
		results = computeCharacterLevelErrors(results);
	
		return results;
	}
	
	static protected CharacterLevelResults computeCharacterCount(CharacterLevelResults results)
	{
		ArrayList<AlignedPair> alignments = results.getOptimalAlignments();
		
		// character-level errors (per character): 0 - char count, 1 - insertions, 2 - substitutions, 3 - deletions, 4 - total errors per char
		float[][] characterLevelErrors = new float[NCHARS + 1][5]; // +1 stands for insertion character '-'
		
		// initialization
        for (int i = 0; i < NCHARS + 1; i++)
        	for(int j = 0; j < 5; j++)
        		characterLevelErrors[i][j] = 0;
        		
        // confusion matrix: NCHARS (all letters + space) + 1 (unrecognized character)
        float[][] confusionMatrix = new float[NCHARS + 1][NCHARS + 1];
        for (int i = 0; i < NCHARS + 1; i++)
            for (int j = 0; j < NCHARS + 1; j++)
                confusionMatrix[i][j] = 0;

        // go through all alignments
        for (AlignedPair alignment : alignments)
        {
            String required = alignment.getRequired();
            String transcribed = alignment.getTranscribed();

            for (int i = 0; i < required.length(); i++)
            {
                // update character count
                int pos = ((int)required.charAt(i)) - 96;
                if (pos >= 1 && pos < NCHARS)
                {
                	characterLevelErrors[pos][0]++;
                }
                else if (required.charAt(i) == INSERTION_CHAR)
                {
                	characterLevelErrors[NCHARS][0]++;
                }
                else if (required.charAt(i) == ' ')
                {
                	characterLevelErrors[0][0]++;
                    pos = 0;
                }
                else
                {  
                	// ignores all other characters
                    continue;
                }

                // update character-level error count
                if (required.charAt(i) == INSERTION_CHAR)
                {
                	// insertion
                	characterLevelErrors[NCHARS][1]++;
                }
                else if (transcribed.charAt(i) == INSERTION_CHAR)
                {
                	// omission
                	characterLevelErrors[pos][3]++;
                }
                else if (required.charAt(i) != transcribed.charAt(i))
                {
                	// substitution
                	characterLevelErrors[pos][2]++;

                    // update confusion matrix
                    if (transcribed.charAt(i) == ' ')
                    {
                    	// space
                        confusionMatrix[pos][0]++;
                    }
                    else
                    {
                        int TPos = ((int)transcribed.charAt(i)) - 96;
                        
                        if(TPos < 0 || TPos >= NCHARS)
                        {
                        	// unrecognized character
                        	confusionMatrix[pos][NCHARS]++;
                        }
                        else
                        {
                        	// valid character
                        	confusionMatrix[pos][TPos]++;
                        }
                    }
                }
                else
                { 
                	//P[i] == T[i] -> correct
                    confusionMatrix[pos][pos]++;
                }
            }
        }
        
        // return confusion matrix and character-level errors
        results.setConfusionMatrix(confusionMatrix);
        results.setCharacterLevelErrors(characterLevelErrors);
        return results;
	}
	
	static protected CharacterLevelResults computeCharacterLevelErrors(CharacterLevelResults results)
	{
		ArrayList<AlignedPair> alignments = results.getOptimalAlignments(); 
		float[][] characterLevelErrors = results.getCharacterLevelErrors();
		float[][] confusionMatrix = results.getConfusionMatrix();
		
		// total errors by error type
		float[] totalErrors = new float[5];
		
		// average errors by error type
		float[] averageErrors = new float[4];
		
        // update error probability columns (error types) with weight
        for (int i = 0; i < NCHARS + 1; i++)
        {
            if (characterLevelErrors[i][0] != 0)
            {
            	// go through all error types and total
                for (int j = 1; j < 4; j++)
                {
                	characterLevelErrors[i][j] = characterLevelErrors[i][j] / characterLevelErrors[i][0]; // error / char_count
                }
            }
        }

        // compute total char count
        for (int i = 0; i < NCHARS + 1; i++)
        {
        	characterLevelErrors[i][0] = characterLevelErrors[i][0] / alignments.size();
            totalErrors[0] += characterLevelErrors[i][0];
        }

        // compute total error rate per char
        for (int i = 0; i < NCHARS + 1; i++)
        {
            float totalChar = 0;
            if (characterLevelErrors[i][0] != 0)
            {
            	// get total error rate per char (sum of all error types)
                for (int j = 1; j < 4; j++)
                {
                    totalChar += characterLevelErrors[i][j];
                }
            }
            
            // update total error rate for each char
            characterLevelErrors[i][4] = totalChar;
        }

        // save absolute confusion matrix (can be useful for other analysis)
        results.setConfusionMatrixAbsolute(cloneArray(confusionMatrix));
        
        // update confusion matrix
        for(int l = 0; l < confusionMatrix.length; l++)
        {
        	// sum line
        	int totalline = 0;
        	for(int c = 0; c < confusionMatrix.length; c++)
        	{
        		totalline += confusionMatrix[l][c];
        	}
        	
        	// average
        	for(int c = 0; c < confusionMatrix.length; c++)
        	{
        		confusionMatrix[l][c] = totalline == 0 ? 0 : confusionMatrix[l][c] / totalline;
        	}
        }
        
        // update total error probability
        for (int j = 1; j < 5; j++) //NCOLUMNS
        {
            float totalCharErrors = 0;

            for (int i = 0; i < NCHARS + 1; i++)
            {
                totalCharErrors = totalCharErrors + (characterLevelErrors[i][j] * characterLevelErrors[i][0]);
            }

            totalErrors[j] = totalCharErrors;
        }

        //update average
        for (int i = 0; i < 4; i++)
        {
            averageErrors[i] = totalErrors[i + 1] / totalErrors[0];
        }
        
        // return character-level errors, total errors, average errors, confusion matrix
        results.setCharacterLevelErrors(characterLevelErrors);
        results.setConfusionMatrix(confusionMatrix);
        results.setTotalErrors(totalErrors);
        results.setAverageErrors(averageErrors);
        
        return results;
	}
	
	protected static float[][] cloneArray(float[][] source)
	{
		int length = source.length;
		float[][] target = new float[length][source[0].length];
		for (int i = 0; i < length; i++) {
			target[i] = source[i].clone();
	        //System.arraycopy(source[i], 0, target[i], 0, source[i].length);
	    }
		return target;
	}
}
