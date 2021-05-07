package uk.openlab.inputmethod.metrics.textedit.measures;

import uk.openlab.inputmethod.metrics.textedit.datastructures.EffectivenessResults;
import uk.openlab.inputmethod.metrics.textedit.datastructures.TextEditCondition;
import uk.openlab.inputmethod.metrics.textedit.datastructures.TextEditParticipant;
import uk.openlab.inputmethod.metrics.textedit.datastructures.TextEditTrial;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryMSD;
import uk.openlab.inputmethod.metrics.textentry.measures.ComputeCharacterLevel;

public class ComputeEffectiveness {
	
	static public TextEditCondition computeConditionEffectivenessResults(TextEditCondition condition)
	{
		EffectivenessResults effectivenessResults = condition.getConditionResults().getEffectivenessResults();
		
		// sum all values
		int counter = 0;
		for(TextEditParticipant participant : condition.getParticipants())
		{
			//if(participant.getTrials().size() == 0) continue;
			
			EffectivenessResults participantresults = participant.getParticipantResults().getEffectivenessResults();
			effectivenessResults.setMSDTranscribed(effectivenessResults.getMSDTranscribed() + participantresults.getMSDTranscribed());
			effectivenessResults.setMSDOriginal(effectivenessResults.getMSDOriginal() + participantresults.getMSDOriginal());
			effectivenessResults.setMSDEdit(effectivenessResults.getMSDEdit() + participantresults.getMSDEdit());
			effectivenessResults.setCompletionRate(effectivenessResults.getCompletionRate() + participantresults.getCompletionRate());
			counter++;
		}
		
		// calculate averages
		int size = counter;
		effectivenessResults.setMSDTranscribed(effectivenessResults.getMSDTranscribed() / size);
		effectivenessResults.setMSDOriginal(effectivenessResults.getMSDOriginal() / size);
		effectivenessResults.setMSDEdit(effectivenessResults.getMSDEdit() / size);
		effectivenessResults.setCompletionRate(effectivenessResults.getCompletionRate() / size);
		
		// task-dependent
		for(int i = 0; i < effectivenessResults.mMSDTasks.length; i++)
		{
			for(TextEditParticipant participant : condition.getParticipants())
			{
				EffectivenessResults participantResults = participant.getParticipantResults().getEffectivenessResults();
				
				effectivenessResults.mCompletionTasks[i] += participantResults.mCompletionTasks[i];
				effectivenessResults.mMSDTasks[i] += participantResults.mMSDTasks[i];
			}
			
			effectivenessResults.mCompletionTasks[i] = effectivenessResults.mCompletionTasks[i] / condition.getParticipants().size();
			effectivenessResults.mMSDTasks[i] = effectivenessResults.mMSDTasks[i] / condition.getParticipants().size();
		}
		
		effectivenessResults.averageByTypeOfTask();
		
		return condition;
	}
	
	static public TextEditParticipant computeParticipantEffectivenessResults(TextEditParticipant participant)
	{
		EffectivenessResults effectivenessResults = participant.getParticipantResults().getEffectivenessResults();
		
		// set task performance
		for(TextEditTrial trial : participant.getTrials())
		{			
			EffectivenessResults trialResults = trial.getTrialResults().getEffectivenessResults();
			
			// get task
			int taskIndex = getTaskIndex(trial.getOriginalSentence());
			if(taskIndex == -1) {
				System.out.println("\t\t\t* unknown task");
				continue;
			}
			
			effectivenessResults.mCompletionTasks[taskIndex] = trialResults.mCompletionTasks[taskIndex];
			effectivenessResults.mMSDTasks[taskIndex] = trialResults.mMSDTasks[taskIndex];
		}
		
		// sum all values
		for(int i = 0; i < 7; i++) {
			float completionRate = effectivenessResults.mCompletionTasks[i];
			float msdRate = effectivenessResults.mMSDTasks[i];
			
			effectivenessResults.setMSDTranscribed(effectivenessResults.getMSDTranscribed() + msdRate);
			effectivenessResults.setCompletionRate(effectivenessResults.getCompletionRate() + completionRate);
		}
		
		// calculate averages
		int size = 7;
		effectivenessResults.setMSDTranscribed(effectivenessResults.getMSDTranscribed() / size);
		effectivenessResults.setCompletionRate(effectivenessResults.getCompletionRate() / size);
		
		// average by type of task
		effectivenessResults.averageByTypeOfTask();
		
		return participant;
	}
	
	static public TextEditTrial computeTrialEffectivenessResults(TextEditTrial trial)
	{		
		TextEntryMSD msdTranscribed = ComputeCharacterLevel.computeMSD(trial.getRequiredSentence(), trial.getTranscribedSentence());
		TextEntryMSD msdOriginal = ComputeCharacterLevel.computeMSD(trial.getRequiredSentence(), trial.getOriginalSentence());
		float msdEdit = Math.abs(msdOriginal.getMSD() - msdTranscribed.getMSD());
		float completionrate = (msdTranscribed.getMSD() == 0 ? 100 : 0);
		
		//float msdErrRate = (float) msdTranscribed.getMSD() / ((float) msdTranscribed.getCorrect() + (float) msdTranscribed.getMSD()) * 100;
		float msdRatio = (float)msdTranscribed.getMSD()/(float)msdOriginal.getMSD()*100;
		EffectivenessResults effectivenessresults = new EffectivenessResults(msdRatio, msdOriginal.getMSD(), msdEdit, completionrate);
		trial.getTrialResults().setEffectivenessResults(effectivenessresults);
		
		// task-dependent
		int taskIndex = getTaskIndex(trial.getOriginalSentence());
			
		if(taskIndex == -1) {
			System.out.println("\t\t\t* unknown task");
			return trial;
		}
		
		// add to array position
		trial.getTrialResults().getEffectivenessResults().mMSDTasks[taskIndex] = trial.getTrialResults().getEffectivenessResults().getMSDTranscribed();
		trial.getTrialResults().getEffectivenessResults().mCompletionTasks[taskIndex] = trial.getTrialResults().getEffectivenessResults().getCompletionRate();

		return trial;
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
