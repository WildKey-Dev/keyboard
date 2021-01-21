package pt.lasige.inputmethod.metrics.textedit.measures;

import java.util.ArrayList;

import pt.lasige.inputmethod.metrics.textedit.datastructures.EditAction;
import pt.lasige.inputmethod.metrics.textedit.datastructures.EfficiencyResults;
import pt.lasige.inputmethod.metrics.textedit.datastructures.EventsResults;
import pt.lasige.inputmethod.metrics.textedit.datastructures.TextEditCondition;
import pt.lasige.inputmethod.metrics.textedit.datastructures.TextEditParticipant;
import pt.lasige.inputmethod.metrics.textedit.datastructures.TextEditTrial;

public class ComputeEvents {
	
	static private String SELECAO_ATIVA = "SeleçãoAtiva";
	static private String CARET_RIGHT = "MovimentoCursorDireita";
	static private String CARET_LEFT = "MovimentoCursorEsquerda";
	
	static public TextEditTrial computeTrialEventsResults(TextEditTrial trial)
	{	
		EventsResults results = trial.getTrialResults().getEventsResults();
		
		// compute number events
		String is = trial.getInputStream();
		String separator = ";";
		String[] events = is.split(separator);
		results.setEvents(events.length);
		
		// compute number selections
		int counter = 0;
		for(int i = 0; i < events.length; i++) {
			if(events[i].equalsIgnoreCase(SELECAO_ATIVA)) {
				counter++;
			}
		}
		results.setSelections(counter);
		
		// compute number caret movements
		counter = 0;
		for(int i = 0; i < events.length; i++) {
			if(events[i].equalsIgnoreCase(CARET_RIGHT) || events[i].equalsIgnoreCase(CARET_LEFT)) {
				counter++;
			}
		}
		results.setCaretMovements(counter);
		
		// task-dependent
		int taskIndex = getTaskIndex(trial.getOriginalSentence());
		if(taskIndex == -1)	return trial;
		// add to array position
		trial.getTrialResults().getEventsResults().mEventsTasks[taskIndex]= trial.getTrialResults().getEventsResults().getEvents();
		
		return trial;
	}
	
	static public TextEditParticipant computeParticipantEventsResults(TextEditParticipant participant)
	{		
		EventsResults eventsResults = participant.getParticipantResults().getEventsResults();
		
		// sum all values
		for(TextEditTrial trial : participant.getTrials())
		{			
			EventsResults trialResults = trial.getTrialResults().getEventsResults();
			
			// events
			eventsResults.setEvents(eventsResults.getEvents() + trialResults.getEvents());
			
			// selections
			eventsResults.setSelections(eventsResults.getSelections() + trialResults.getSelections());
			
			// caret
			eventsResults.setCaretMovements(eventsResults.getCaretMovements() + trialResults.getCaretMovements());
			
			// get task
			int taskIndex = getTaskIndex(trial.getOriginalSentence());
			if(taskIndex == -1) continue;
			eventsResults.mEventsTasks[taskIndex] = trialResults.mEventsTasks[taskIndex];
		}
		
		// calculate averages
		int size = participant.getTrials().size();
		if(size == 0) return participant;
		eventsResults.setEvents(eventsResults.getEvents() / size);
		eventsResults.setSelections(eventsResults.getSelections() / size);
		eventsResults.setCaretMovements(eventsResults.getCaretMovements() / size);
		
		// average by type of task
		eventsResults.averageByTypeOfTask();
		
		return participant;
	}
	
	static public TextEditCondition computeConditionEventsResults(TextEditCondition condition)
	{
		EventsResults eventsResults = condition.getConditionResults().getEventsResults();
		
		// sum all values
		int counter = 0;
		for(TextEditParticipant participant : condition.getParticipants())
		{			
			EventsResults participantResults = participant.getParticipantResults().getEventsResults();
			if(participantResults.getEvents() == 0) continue;
			counter++;
			
			// events
			eventsResults.setEvents(eventsResults.getEvents() + participantResults.getEvents());
			
			// selections
			eventsResults.setSelections(eventsResults.getSelections() + participantResults.getSelections());
			
			// caret
			eventsResults.setCaretMovements(eventsResults.getCaretMovements() + participantResults.getCaretMovements());
		}
		
		// calculate averages
		int size = counter++;
		eventsResults.setEvents(eventsResults.getEvents() / size);
		eventsResults.setSelections(eventsResults.getSelections() / size);
		eventsResults.setCaretMovements(eventsResults.getCaretMovements() / size);
		
		// task-dependent
		for(int i = 0; i < eventsResults.mEventsTasks.length; i++)
		{
			counter=0;
			eventsResults.mEventsTasks[i] = 0;
			for(TextEditParticipant participant : condition.getParticipants())
			{
				EventsResults participantResults = participant.getParticipantResults().getEventsResults();
						
				if(!Float.isNaN(participantResults.mEventsTasks[i])) {
					eventsResults.mEventsTasks[i] += participantResults.mEventsTasks[i];
					counter++;
				}
			}
							
			if(counter > 0) eventsResults.mEventsTasks[i] = eventsResults.mEventsTasks[i] / counter;
			else eventsResults.mEventsTasks[i] = Float.NaN;
		}
						
		eventsResults.averageByTypeOfTask();
				
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
