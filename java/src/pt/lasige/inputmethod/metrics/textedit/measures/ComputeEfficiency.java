package pt.lasige.inputmethod.metrics.textedit.measures;

import java.util.ArrayList;

import pt.lasige.inputmethod.metrics.textedit.datastructures.EditAction;
import pt.lasige.inputmethod.metrics.textedit.datastructures.EfficiencyResults;
import pt.lasige.inputmethod.metrics.textedit.datastructures.TextEditCondition;
import pt.lasige.inputmethod.metrics.textedit.datastructures.TextEditParticipant;
import pt.lasige.inputmethod.metrics.textedit.datastructures.TextEditTrial;

public class ComputeEfficiency {
	
	static public TextEditTrial computeTrialEfficiencyResults(TextEditTrial trial)
	{		
		EfficiencyResults results = trial.getTrialResults().getEfficiencyResults();
		results.setTime(computeTrialTime(trial));
		
		// task-dependent
		int taskIndex = getTaskIndex(trial.getOriginalSentence());
					
		if(taskIndex == -1)	return trial;
				
		// add to array position
		trial.getTrialResults().getEfficiencyResults().mTimeTasks[taskIndex]= trial.getTrialResults().getEfficiencyResults().getTime();
		
		return trial;
	}
	
	static public TextEditParticipant computeParticipantEfficencyResults(TextEditParticipant participant)
	{
		EfficiencyResults efficiencyResults = participant.getParticipantResults().getEfficiencyResults();
		
		// sum all values
		int counter = 0;
		for(TextEditTrial trial : participant.getTrials())
		{			
			EfficiencyResults trialResults = trial.getTrialResults().getEfficiencyResults();
			counter++;
			
			efficiencyResults.setTime(efficiencyResults.getTime() + trialResults.getTime());
			
			// get task
			int taskIndex = getTaskIndex(trial.getOriginalSentence());
			if(taskIndex == -1) continue;
			
			efficiencyResults.mTimeTasks[taskIndex] = trialResults.mTimeTasks[taskIndex];
		}
		
		// calculate averages
		int size = counter;//session.getTrials().size();
		efficiencyResults.setTime(efficiencyResults.getTime() / size);
		
		// average by type of task
		efficiencyResults.averageByTypeOfTask();
		
		return participant;
	}
	
	static public TextEditCondition computeConditionEfficiencyResults(TextEditCondition condition)
	{
		EfficiencyResults efficiencyResults = condition.getConditionResults().getEfficiencyResults();
		
		// sum all values
		int counter = 0;
		for(TextEditParticipant participant: condition.getParticipants())
		{
			EfficiencyResults sessionResults = participant.getParticipantResults().getEfficiencyResults();
			if(participant.getTrials().size() == 0) continue;
			
			efficiencyResults.setTime(efficiencyResults.getTime() + sessionResults.getTime());
			counter++;
		}
		
		// calculate averages
		int size = counter; //condition.getSessions().size();
		efficiencyResults.setTime(efficiencyResults.getTime() / size);
		
		// task-dependent
		for(int i = 0; i < efficiencyResults.mTimeTasks.length; i++)
		{
			counter=0;
			efficiencyResults.mTimeTasks[i] = 0;
			for(TextEditParticipant participant : condition.getParticipants())
			{
				EfficiencyResults participantResults = participant.getParticipantResults().getEfficiencyResults();
				
				if(!Float.isNaN(participantResults.mTimeTasks[i])) {
					efficiencyResults.mTimeTasks[i] += participantResults.mTimeTasks[i];
					counter++;
				}
			}
					
			if(counter > 0) efficiencyResults.mTimeTasks[i] = efficiencyResults.mTimeTasks[i] / counter;
			else efficiencyResults.mTimeTasks[i] = Float.NaN;
				
		}
				
		efficiencyResults.averageByTypeOfTask();
		
		return condition;
	}
	
	/*
	 * HELPER FUNCTIONS
	 */
	
	static protected float computeTrialTime(TextEditTrial trial)
	{
		if(trial == null) return 0;
		
		float t = 0;
		ArrayList<EditAction> actions = trial.getEditActions();
		EditAction firstChar = null;
		EditAction lastChar = null;
		
		if(actions == null || actions.size() < 2) return 0;
		
		// get first character
		firstChar = actions.get(0);
		
		// get last character
		lastChar = actions.get(actions.size() - 1);
		
		// difference in milliseconds since the first character until the last!!
		long diff = lastChar.Timestamp.getTime() - firstChar.Timestamp.getTime();
		
		// to seconds
		t = diff / 1000;
		
		return t;
	}
	
	static private int getTaskIndex(String sentence) {
		if(sentence.equalsIgnoreCase("um dois trêXs quatro cinco")) return 0;
		if(sentence.equalsIgnoreCase("um dois três quatro XXXXX cinco")) return 1;
		if(sentence.equalsIgnoreCase("um dois trêsquatro cinco")) return 2;
		if(sentence.equalsIgnoreCase("um três quatro cinco")) return 3;
		if(sentence.equalsIgnoreCase("um três dois quatro cinco")) return 4;
		if(sentence.equalsIgnoreCase("um um um um↵três três três três↵dois dois dois dois↵quatro quatro quatro quatro↵cinco cinco cinco cinco")) return 5;
		if(sentence.equalsIgnoreCase("Heróis do mar nobre pavo Nação valente X mortal Levantai hoje de novo o explendor de Espanha")) return 6;
		return -1;
	}
}
