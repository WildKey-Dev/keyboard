package pt.lasige.cns.study.inputmethod.metrics.textentry.measures;

import java.util.ArrayList;

import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.EfficiencyResults;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.InputAction;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryCondition;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryParticipantSession;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial.INPUT_ACTION_TYPE;

public class ComputeEfficiency {

	static private String DELETE_CHAR = "<<";
	
	static public TextEntryTrial computeTrialEfficiencyResults(TextEntryTrial trial)
	{
		if(!trial.getTrialResults().COMPUTE_TIME) return trial;
		
		EfficiencyResults results = trial.getTrialResults().getEfficiencyResults();
		results.setTime(computeTrialTime(trial));
		results.setCorrectionTime(computeCorrectionTime(trial));
		results.setWPM(computeWPM(trial));
		results.setCPS(computeCPS(trial));
		results.setInterKeyTime(computeInterKeyInterval(trial));
		
		return trial;
	}
	
	static public TextEntryParticipantSession computeSessionEfficencyResults(TextEntryParticipantSession session)
	{
		EfficiencyResults efficiencyResults = session.getSessionResults().getEfficiencyResults();
		
		// sum all values
		int counter = 0;
		for(TextEntryTrial trial : session.getTrials())
		{
			if(!trial.getTrialResults().COMPUTE_TIME) continue;
			
			EfficiencyResults trialResults = trial.getTrialResults().getEfficiencyResults();
			counter++;
			
			efficiencyResults.setTime(efficiencyResults.getTime() + trialResults.getTime());
			efficiencyResults.setCorrectionTime(efficiencyResults.getCorrectionTime() + trialResults.getCorrectionTime());
			efficiencyResults.setCPS(efficiencyResults.getCPS() + trialResults.getCPS());
			efficiencyResults.setWPM(efficiencyResults.getWPM() + trialResults.getWPM());
			efficiencyResults.setInterKeyTime(efficiencyResults.getInterKeyTime() + trialResults.getInterKeyTime());
		}
		
		// calculate averages
		int size = counter;//session.getTrials().size();
		efficiencyResults.setTime(efficiencyResults.getTime() / size);
		efficiencyResults.setCorrectionTime(efficiencyResults.getCorrectionTime() / size);
		efficiencyResults.setCPS(efficiencyResults.getCPS() / size);
		efficiencyResults.setWPM(efficiencyResults.getWPM() / size);
		efficiencyResults.setInterKeyTime(efficiencyResults.getInterKeyTime() / size);
		
		return session;
	}
	
	static public TextEntryCondition computeConditionEfficiencyResults(TextEntryCondition condition)
	{
		EfficiencyResults efficiencyResults = condition.getConditionResults().getEfficiencyResults();
		
		// sum all values
		int counter = 0;
		for(TextEntryParticipantSession session : condition.getSessions())
		{
			EfficiencyResults sessionResults = session.getSessionResults().getEfficiencyResults();
			if(session.getTrials().size() == 0) continue;
			
			efficiencyResults.setTime(efficiencyResults.getTime() + sessionResults.getTime());
			efficiencyResults.setCorrectionTime(efficiencyResults.getCorrectionTime() + sessionResults.getCorrectionTime());
			efficiencyResults.setCPS(efficiencyResults.getCPS() + sessionResults.getCPS());
			efficiencyResults.setWPM(efficiencyResults.getWPM() + sessionResults.getWPM());
			efficiencyResults.setInterKeyTime(efficiencyResults.getInterKeyTime() + sessionResults.getInterKeyTime());
			counter++;
		}
		
		// calculate averages
		int size = counter; //condition.getSessions().size();
		efficiencyResults.setTime(efficiencyResults.getTime() / size);
		efficiencyResults.setCorrectionTime(efficiencyResults.getCorrectionTime() / size);
		efficiencyResults.setCPS(efficiencyResults.getCPS() / size);
		efficiencyResults.setWPM(efficiencyResults.getWPM() / size);
		efficiencyResults.setInterKeyTime(efficiencyResults.getInterKeyTime() / size);
		
		return condition;
	}
	
	/*
	 * HELPER FUNCTIONS
	 */
		
	static protected float computeCorrectionTime(TextEntryTrial trial)
	{
		//float trialTime = computeTrialTime(trial);
		float correctionTime = Float.NaN;
		
		long startCorr = -1;
		int lengthCorr = -1;
		long lastTransc = -1;
		
		StringBuilder transcribed = new StringBuilder();
		
		for(InputAction action : trial.getInputActions())
		{
			if(action.Type == TextEntryTrial.INPUT_ACTION_TYPE.ENTER)
			{
				if(action.Character.equalsIgnoreCase(DELETE_CHAR))
				{
					if(startCorr == -1)
					{
						startCorr = action.Timestamp.getTime();
						lengthCorr = transcribed.length();
					}
					
					// delete char
					if(transcribed.length() > 0)
					{
						transcribed.deleteCharAt(transcribed.length() - 1);
					}
					lastTransc = startCorr;
				}
				else
				{
					// char entered
					transcribed.append(action.Character);
					
					lastTransc = action.Timestamp.getTime();
					if(startCorr != -1 && transcribed.length() == lengthCorr)
					{
						// correction ended
						correctionTime += (lastTransc - startCorr);
						
						startCorr = -1;
						lengthCorr = -1;
						lastTransc = -1;
					}
				}
			}
		}
		
		if(startCorr != -1)
		{
			// correction ended
			correctionTime += (lastTransc - startCorr);
		}
		
		float t = computeTrialTime(trial);
		
		return (correctionTime / 1000) / t;
	}
	
	static protected float computeTrialTime(TextEntryTrial trial)
	{
		if(trial == null) return 0;
		
		float t = 0;
		ArrayList<InputAction> actions = trial.getInputActions();
		InputAction firstChar = null;
		InputAction lastChar = null;
		
		if(actions == null) return 0;
		
		// get first character
		for(InputAction action : actions)
		{
			if(action.Type == TextEntryTrial.INPUT_ACTION_TYPE.ENTER)
			{
				firstChar = action;
				break;
			}
		}
		
		// get last character
		for(int i = actions.size() - 1; i >= 0; i--)
		{
			InputAction action = actions.get(i);
			if(action.Type == TextEntryTrial.INPUT_ACTION_TYPE.ENTER)
			{
				lastChar = action;
				break;
			}
			
		}
		
		if(firstChar == null || lastChar == null) return 0;
		
		// difference in milliseconds since the first character until the last!!
		long diff = lastChar.Timestamp.getTime() - firstChar.Timestamp.getTime();
		
		// to seconds
		t = diff / 1000;
		
		return t;
	}
	
	static protected float computeCPS(TextEntryTrial trial)
	{
		float cps = 0;
		
		float t = computeTrialTime(trial);
		if(t <= 0 || trial.getTranscribedSentence().length() <= 0) return 0;
		
		// (transcribed_text_length - 1) / trial_time_in_seconds
		cps = (trial.getTranscribedSentence().length() - 1) / t;
		
		return cps;
	}
	
	static protected float computeWPM(TextEntryTrial trial)
	{
		float wpm = 0;
		
		float t = computeTrialTime(trial);
		if(t == 0 || trial.getTranscribedSentence().length() <= 0) return 0;
		
		// (transcribed_text_length - 1) * (60 secs / trial_time_in_seconds) / 5 chars_per_word
		wpm = (trial.getTranscribedSentence().length() - 1) * (60 / t) / 5;
		
		return wpm;
	}
	
	static protected float computeInterKeyInterval(TextEntryTrial trial)
	{
		if(trial == null) return 0;
		
		float totalTime = 0;
		ArrayList<InputAction> actions = trial.getInputActions();
		
		int index = getNextEnterIndex(actions, 0);
		int count = 0;
		
		for(int i = index; i < actions.size(); i++)
		{
			InputAction current = actions.get(i);
			int nextIndex = getNextEnterIndex(actions, i + 1);
			if(nextIndex == -1) break;
			InputAction next = actions.get(nextIndex);
			
			totalTime += next.Timestamp.getTime() - current.Timestamp.getTime();
			i = nextIndex - 1;
			count ++;
		}
		
		float averageTime = count == 0 ? 0 : totalTime / count / 1000;
		return averageTime;
	}
	
	static protected int getNextEnterIndex(ArrayList<InputAction> actions, int index)
	{
		for(int i = index; i < actions.size(); i++)
		{
			if(actions.get(i).Type == INPUT_ACTION_TYPE.ENTER) return i;
		}
		
		return -1;
	}
}
