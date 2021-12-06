package pt.lasige.cns.study.inputmethod.metrics.textentry.measures;

import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.SpellCheckerResults;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.SuggestionEntry;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryCondition;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryMSD;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryParticipantSession;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class ComputeSpellChecker {
	
	static protected TextEntryCondition computeConditionSpellCheckerResults(TextEntryCondition condition)
	{
		SpellCheckerResults spellcheckerResults = condition.getConditionResults().getSpellCheckerResults();
		
		// sum all values
		for(TextEntryParticipantSession session : condition.getSessions())
		{
			SpellCheckerResults sessionResults = session.getSessionResults().getSpellCheckerResults();
			
			spellcheckerResults.setCorrectButIncorrectlyFixed(spellcheckerResults.getCorrectButIncorrectlyFixed() + sessionResults.getCorrectButIncorrectlyFixed());
			spellcheckerResults.setCorrectEntries(spellcheckerResults.getCorrectEntries() + sessionResults.getCorrectEntries());
			spellcheckerResults.setCorrectNotFixed(spellcheckerResults.getCorrectNotFixed() + sessionResults.getCorrectNotFixed());
			float[] tempTopF = spellcheckerResults.getIncorrectButTopFix();
			float[] trialTopF = sessionResults.getIncorrectButTopFix();
			for(int i = 0; i < sessionResults.getIncorrectButTopFix().length; i++)
			{
				tempTopF[i] += trialTopF[i];
			}
			spellcheckerResults.setIncorrectButTopFix(tempTopF);
			spellcheckerResults.setIncorrectButSuggested(spellcheckerResults.getIncorrectButSuggested() + sessionResults.getIncorrectButSuggested());
			spellcheckerResults.setIncorrectEntries(spellcheckerResults.getIncorrectEntries() + sessionResults.getIncorrectEntries());
			spellcheckerResults.setIncorrectNotFixed(spellcheckerResults.getIncorrectNotFixed() + sessionResults.getIncorrectNotFixed());
			
			float[] wordDistance = spellcheckerResults.getWordDistance();
			float[] sessionWordDistance = sessionResults.getWordDistance();
			for(int i = 0; i < sessionResults.getWordDistance().length; i++)
			{
				wordDistance[i] += sessionWordDistance[i];
			}
			spellcheckerResults.setWordDistance(wordDistance);
		}
		
		return condition;
	}
	
	static protected TextEntryParticipantSession computeSessionSpellCheckerResults(TextEntryParticipantSession session)
	{
		SpellCheckerResults spellcheckerResults = session.getSessionResults().getSpellCheckerResults();
		
		// sum all values
		for(TextEntryTrial trial : session.getTrials())
		{
			if(!trial.getTrialResults().COMPUTE_ERRORS) continue;
			
			SpellCheckerResults trialResults = trial.getTrialResults().getSpellCheckerResults();
			
			spellcheckerResults.setCorrectButIncorrectlyFixed(spellcheckerResults.getCorrectButIncorrectlyFixed() + trialResults.getCorrectButIncorrectlyFixed());
			spellcheckerResults.setCorrectEntries(spellcheckerResults.getCorrectEntries() + trialResults.getCorrectEntries());
			spellcheckerResults.setCorrectNotFixed(spellcheckerResults.getCorrectNotFixed() + trialResults.getCorrectNotFixed());
			float[] tempTopF = spellcheckerResults.getIncorrectButTopFix();
			float[] trialTopF = trialResults.getIncorrectButTopFix();
			for(int i = 0; i < trialResults.getIncorrectButTopFix().length; i++)
			{
				tempTopF[i] += trialTopF[i];
			}
			spellcheckerResults.setIncorrectButTopFix(tempTopF);
			spellcheckerResults.setIncorrectButSuggested(spellcheckerResults.getIncorrectButSuggested() + trialResults.getIncorrectButSuggested());
			spellcheckerResults.setIncorrectEntries(spellcheckerResults.getIncorrectEntries() + trialResults.getIncorrectEntries());
			spellcheckerResults.setIncorrectNotFixed(spellcheckerResults.getIncorrectNotFixed() + trialResults.getIncorrectNotFixed());
			
			float[] wordDistance = spellcheckerResults.getWordDistance();
			float[] trialWordDistance = trialResults.getWordDistance();
			for(int i = 0; i < trialResults.getWordDistance().length; i++)
			{
				wordDistance[i] += trialWordDistance[i];
			}
			spellcheckerResults.setWordDistance(wordDistance);
		}
		
		return session;
	}
	
	static public TextEntryTrial computeTrialSpellCheckerResults(TextEntryTrial trial)
	{
		if(!trial.getTrialResults().COMPUTE_ERRORS) return trial;
		
		// spellchecker accuracy measures
		float countIncorrectEntries = 0;
		float countCorrectEntries = 0;
		// incorrect and fixed
		float countIFTopN[] = new float[SpellCheckerResults.N];
		// incorrect and suggested
		float countISuggested = 0;
		// incorrect not fixed
		float countINF = 0;
		// correct and not fixed
		float countCbCF = 0;
		// correct but incorrectly fixed
		float countCbIF = 0;
		
		//word edit distance
		float wordEditDistance[] = {0, 0, 0, 0, 0, 0}; // 1, 2, 3, 4, 5, 6+
		
		// for each transcribed text between white spaces
		for(SuggestionEntry se : trial.getSuggestions())
		{
			if(se.getRequiredWord().equalsIgnoreCase(se.getTranscribedWord()))
			{
				// correct
				countCorrectEntries++;
				if(se.getSuggestions().size() > 0 && se.getSuggestions().get(0).equalsIgnoreCase(se.getRequiredWord()))
				{
					// correct and not fixed
					countCbCF++;
				}
				else if(se.getSuggestions().size() > 0 && !se.getSuggestions().get(0).equalsIgnoreCase(se.getRequiredWord()))
				{
					// correct but incorrectly fixed
					countCbIF++;
				}
			}
			else
			{
				// incorrect
				countIncorrectEntries++;
				
				// incorrect, lets check whether it is on the suggestion list
				boolean corrected = false;
				int s = 0;
				// for each suggestion
				for(; s < se.getSuggestions().size(); s++)
				{
					// check whether it is equal to the required word
					String suggestion = se.getSuggestions().get(s);
					if(suggestion != null && suggestion.equalsIgnoreCase(se.getRequiredWord()))
					{
						// if so, add to all topN suggestions, where N >= s
						addToTopFix(countIFTopN, s);
						corrected = true;
						break;
					}
				}
				
				if(corrected) countISuggested++;
				else if(s >= SpellCheckerResults.N) countINF++;
				
				// compute word edit distance
				TextEntryMSD msd = ComputeCharacterLevel.computeMSD(se.getRequiredWord(), se.getTranscribedWord());
				int editDistance = msd.getMSD();
				
				if(editDistance == 0)
				{
					System.out.println("NOOOOO");
				}
				
				if(editDistance <= wordEditDistance.length)
				{
					wordEditDistance[editDistance - 1]++;
				}
				else
				{
					wordEditDistance[wordEditDistance.length - 1]++;
				}
			}
			
		}
		
		// update spellchecker results
		SpellCheckerResults spellcheckerresults = new SpellCheckerResults(countCorrectEntries, countIncorrectEntries, 
				countIFTopN, countISuggested, countINF, countCbCF, countCbIF, wordEditDistance);
		trial.getTrialResults().setSpellCheckerResults(spellcheckerresults);
				
		return trial;
	}
	
	static private void addToTopFix(float[] topN, int n)
	{
		if(n < 0) return;
		for(int i = n; i < topN.length; i++)
		{
			topN[i]++;
		}
	}

}
