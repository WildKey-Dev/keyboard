package uk.openlab.inputmethod.metrics.textentry.measures;

import java.util.ArrayList;
import java.util.List;

import uk.openlab.inputmethod.metrics.textentry.datastructures.InputAction;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryCondition;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryParticipantSession;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial.INPUT_ACTION_TYPE;
import uk.openlab.inputmethod.metrics.textentry.datastructures.WordCompletionResults;

public class ComputeWordCompletion {
	
	static public TextEntryTrial computeTrialWordCompletionResults(TextEntryTrial trial, int nsuggestions)
	{
		WordCompletionResults results = trial.getTrialResults().getWordCompletionResults();
		
		results.setTime(computeTrialTime(trial));
		results.setWPM(computeWPM(trial));
		
		results.setErrorRate(computeErrorRate(trial));
		results.setKSRUsed(computeKSRUsed(trial, 0));
		results.setKSROptimal(computeKSROptimal(trial));
		results.setKSROffered(computeKSROffered(trial, nsuggestions));
		results.setKSSRUserLen(computeKSRUsed(trial, 3));
		results.setNSelections(computeNSelections(trial));
		results.setShown(computeShown(trial, nsuggestions));
		results.setCorrect(computeCorrect(trial, nsuggestions));
		results.setUsed(computeUsed(trial, nsuggestions));
		
		return trial;
	}
	
	static public TextEntryParticipantSession computeSessionWordCompletionResults(TextEntryParticipantSession session)
	{
		WordCompletionResults sessionResults = session.getSessionResults().getWordCompletionResults();
		
		// sum all values
		for(TextEntryTrial trial : session.getTrials())
		{
			WordCompletionResults trialResults = trial.getTrialResults().getWordCompletionResults();
			
			sessionResults.setTime(sessionResults.getTime() + trialResults.getTime());
			sessionResults.setWPM(sessionResults.getWPM() + trialResults.getWPM());
			sessionResults.setErrorRate(sessionResults.getErrorRate() + trialResults.getErrorRate());
			sessionResults.setKSRUsed(sessionResults.getKSRUsed() + trialResults.getKSRUsed());
			sessionResults.setKSROptimal(sessionResults.getKSROptimal() + trialResults.getKSROptimal());
			sessionResults.setKSROffered(sessionResults.getKSROffered() + trialResults.getKSROffered());
			sessionResults.setKSSRUserLen(sessionResults.getKSSRUserLen() + trialResults.getKSSRUserLen());
			sessionResults.setNSelections(sessionResults.getNSelections() + trialResults.getNSelections());
			sessionResults.setShown(sessionResults.getShown() + trialResults.getShown());
			sessionResults.setCorrect(sessionResults.getCorrect() + trialResults.getCorrect());
			sessionResults.setUsed(sessionResults.getUsed() + trialResults.getUsed());
		}
		
		// calculate averages
		sessionResults.setTime(sessionResults.getTime() / session.getTrials().size());
		sessionResults.setWPM(sessionResults.getWPM() / session.getTrials().size());
		sessionResults.setErrorRate(sessionResults.getErrorRate() / session.getTrials().size());
		sessionResults.setKSRUsed(sessionResults.getKSRUsed() / session.getTrials().size());
		sessionResults.setKSROptimal(sessionResults.getKSROptimal() / session.getTrials().size());
		sessionResults.setKSROffered(sessionResults.getKSROffered() / session.getTrials().size());
		sessionResults.setKSSRUserLen(sessionResults.getKSSRUserLen() / session.getTrials().size());
		sessionResults.setShown(sessionResults.getShown() / session.getTrials().size());
		sessionResults.setCorrect(sessionResults.getCorrect() / session.getTrials().size());
		sessionResults.setUsed(sessionResults.getUsed() / session.getTrials().size());
		
		return session;
	}
	
	static public TextEntryCondition computeConditionWordCompletionResults(TextEntryCondition condition)
	{
		WordCompletionResults conditionResults = condition.getConditionResults().getWordCompletionResults();
		
		// sum
		for(TextEntryParticipantSession session: condition.getSessions())
		{
			WordCompletionResults sessionResults = session.getSessionResults().getWordCompletionResults();
			
			conditionResults.setTime(conditionResults.getTime() + sessionResults.getTime());
			conditionResults.setWPM(conditionResults.getWPM() + sessionResults.getWPM());
			conditionResults.setErrorRate(conditionResults.getErrorRate() + sessionResults.getErrorRate());
			conditionResults.setKSRUsed(conditionResults.getKSRUsed() + sessionResults.getKSRUsed());
			conditionResults.setKSROptimal(conditionResults.getKSROptimal() + sessionResults.getKSROptimal());
			conditionResults.setKSROffered(conditionResults.getKSROffered() + sessionResults.getKSROffered());
			conditionResults.setKSSRUserLen(conditionResults.getKSSRUserLen() + sessionResults.getKSSRUserLen());
			conditionResults.setNSelections(conditionResults.getNSelections() + sessionResults.getNSelections());
			conditionResults.setShown(conditionResults.getShown() + sessionResults.getShown());
			conditionResults.setCorrect(conditionResults.getCorrect() + sessionResults.getCorrect());
			conditionResults.setUsed(conditionResults.getUsed() + sessionResults.getUsed());
		}
		
		// calculate averages
		conditionResults.setTime(conditionResults.getTime() / condition.getSessions().size());
		conditionResults.setWPM(conditionResults.getWPM() / condition.getSessions().size());
		conditionResults.setErrorRate(conditionResults.getErrorRate() / condition.getSessions().size());
		conditionResults.setKSRUsed(conditionResults.getKSRUsed() / condition.getSessions().size());
		conditionResults.setKSROptimal(conditionResults.getKSROptimal() / condition.getSessions().size());
		conditionResults.setKSROffered(conditionResults.getKSROffered() / condition.getSessions().size());
		conditionResults.setKSSRUserLen(conditionResults.getKSSRUserLen() / condition.getSessions().size());
		conditionResults.setShown(conditionResults.getShown() / condition.getSessions().size());
		conditionResults.setCorrect(conditionResults.getCorrect() / condition.getSessions().size());
		conditionResults.setUsed(conditionResults.getUsed() / condition.getSessions().size());
		
		return condition;
	}
	
	/* -- HELPER FUNCTIONS -- */
	static protected float computeTrialTime(TextEntryTrial trial)
	{
		if(trial == null) return Float.NaN;
		
		float t = 0;
		ArrayList<InputAction> actions = trial.getInputActions();
		InputAction firstChar = null;
		InputAction lastChar = null;
		
		if(actions == null) return Float.NaN;
		
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
			if(action.valid && action.Timestamp != null && (action.Type == TextEntryTrial.INPUT_ACTION_TYPE.ENTER || 
					action.Type == TextEntryTrial.INPUT_ACTION_TYPE.WORD_COMPLETION))
			{
				lastChar = action;
				break;
			}
			
		}
		
		if(firstChar == null || lastChar == null) return Float.NaN;
		
		// difference in milliseconds since the first character until the last!!
		long diff = lastChar.Timestamp.getTime() - firstChar.Timestamp.getTime();
		
		// to seconds
		t = (float) (diff / 1000.0);
		
		return t;
	}
	
	static protected float computeWPM(TextEntryTrial trial)
	{
		float wpm = 0;
		
		float t = computeTrialTime(trial);
		if(t == 0 || trial.getTranscribedSentence().length() <= 0) return Float.NaN;
		
		// (transcribed_text_length - 1) * (60 secs / trial_time_in_seconds) / 5 chars_per_word
		wpm = (trial.getTranscribedSentence().length() - 1) * (60 / t) / 5;
		
		return wpm;
	}
	
	static protected float computeErrorRate(TextEntryTrial trial) {
		float errrate = 0;
		
		ArrayList<InputAction> actions = trial.getInputActions();
		float errors = 0;
		float enters = 0;
		
		for(InputAction action : actions)
		{
			if(!action.valid) errors++;
			if(action.valid && 
					(action.Type == INPUT_ACTION_TYPE.ENTER || action.Type == INPUT_ACTION_TYPE.WORD_COMPLETION))
			{
				enters++;
			}
		}
		
		errrate = errors / (enters + errors);
		return errrate;
	}

	static protected float computeNSelections(TextEntryTrial trial) {
		float n = 0;
		
		ArrayList<InputAction> actions = trial.getInputActions();
		for(InputAction action : actions) {
			if(action.valid && action.Type == INPUT_ACTION_TYPE.WORD_COMPLETION) n++;
		}
		
		return n;
	}
	
	static protected float computeShown(TextEntryTrial trial, int nsuggestions) {
		float shown = 0;
		float chars = 0;
		
		// for each word
		// split required
		String[] words = trial.getRequiredSentence().split(" ");
		
		// split input actions
		ArrayList<InputAction> actions = trial.getInputActions();
		ArrayList<List<InputAction>> word_actions = splitInputActions(words, actions);
		
		for(int i=0; i < words.length; i++) {
			float kmax = countValid(word_actions.get(i));
			float kshown = 0;
			
			if(nsuggestions > 0) // number of suggestions that are always read
				kshown = countSuggestions(word_actions.get(i));
			else // talkback
				kshown = countAutoComplete(word_actions.get(i));
			
			if(kshown > kmax)
				System.out.println("what?");
			
			shown += kshown;
			chars += kmax;
		}
		
		shown = (float) (shown / chars);
		
		return shown;
	}
	
	static protected float computeCorrect(TextEntryTrial trial, int nsuggestions) {
		float correct = 0;
		float chars = 0;
		
		// for each word
		// split required
		String[] words = trial.getRequiredSentence().split(" ");
		
		// split input actions
		ArrayList<InputAction> actions = trial.getInputActions();
		ArrayList<List<InputAction>> word_actions = splitInputActions(words, actions);
		
		for(int i=0; i < words.length; i++) {
			float kmax = countValid(word_actions.get(i));
			float kcorrect = 0;
			
			if(nsuggestions > 0) // number of suggestions that are always read
				kcorrect = countAllCorrectSuggestions(words[i], word_actions.get(i), nsuggestions);
			else // talkback
				kcorrect = countAllCorrectAutoComplete(words[i], word_actions.get(i));
			
			if(kcorrect > kmax)
				System.out.println("what?");
			
			correct += kcorrect;
			chars += kmax;
		}
		
		correct = (float) (correct / chars);
		
		return correct;
	}
	
	static protected float computeUsed(TextEntryTrial trial, int nsuggestions) {
		
		float nselections = computeNSelections(trial);
		float nwords = countValid(trial.getInputActions());
		
		float used = (float) (nselections / nwords);
		
		return used;
	}
	
	
	static protected float computeKSROptimal(TextEntryTrial trial) {
		float kssr = 0;
		float kmin_sum = 0;
		float kmax_sum = 0;
		
		// for each word
		// split required
		String[] words = trial.getRequiredSentence().split(" ");
		
		// split input actions
		ArrayList<InputAction> actions = trial.getInputActions();
		ArrayList<List<InputAction>> word_actions = splitInputActions(words, actions);
		
		for(int i=0; i < words.length; i++) {
			float kmax = words[i].length() + 1;
			float kmin = countMin(words[i], word_actions.get(i));
			
			if(kmin > kmax)
				System.out.println("what?");
			
			kmax_sum += kmax;
			kmin_sum += kmin;
		}
		
		kssr = (float) (1.0 - (kmin_sum / kmax_sum));
		return kssr;
	}
	
	static protected float computeKSROffered(TextEntryTrial trial, int nsuggestions) {
		// 0 suggestions means Talkback
		// > 0 means personalized interface
		float kssr = 0;
		float kmin_sum = 0;
		float kmax_sum = 0;
		
		// for each word
		// split required
		String[] words = trial.getRequiredSentence().split(" ");
		
		// split input actions
		ArrayList<InputAction> actions = trial.getInputActions();
		ArrayList<List<InputAction>> word_actions = splitInputActions(words, actions);
		
		for(int i=0; i < words.length; i++) {
			float kmax = words[i].length() + 1;
			float kmin;
			
			if(nsuggestions > 0)
				kmin = countCorrectSuggestions(words[i], word_actions.get(i), nsuggestions);
			else
				kmin = countCorrectAutoComplete(words[i], word_actions.get(i));
			
			if(kmin > kmax)
				System.out.println("what?");
			
			kmax_sum += kmax;
			kmin_sum += kmin;
		}
		
		kssr = (float) (1.0 - (kmin_sum / kmax_sum));
		return kssr;
	}
	
	static protected float computeKSRUsed(TextEntryTrial trial, int len) {
		float kssr = 0;
		float kmax_sum = 0;
		float kuser_sum = 0;
		
		// for each word
		// split required
		String[] words = trial.getRequiredSentence().split(" ");
		
		// split input actions
		ArrayList<InputAction> actions = trial.getInputActions();
		ArrayList<List<InputAction>> word_actions = splitInputActions(words, actions);
		
		for(int i=0; i < words.length; i++) {
			if(words[i].length() <= len) 
				continue;
			
			float kmax = words[i].length() + 1;
			float kuser = countValid(word_actions.get(i));
			
			if(i == words.length - 1) kuser++; // last word in sentence, we need to account for a space that was not entered
			
			if(wasWordCompletion(word_actions.get(i))) 
				kuser--;
			
			if(kuser > kmax) {
				System.out.println("Error: more keystrokes than possible!");
			}
			
			kmax_sum += kmax;
			kuser_sum += kuser;
		}
		
		kssr = (float) (1.0 - (kuser_sum / kmax_sum));
		return kssr;
	}
	
	static protected ArrayList<List<InputAction>> splitInputActions(String[] words, ArrayList<InputAction> actions) {
		ArrayList<List<InputAction>> word_actions = new ArrayList<List<InputAction>>(words.length);
		
		int startIndex = 0;
		for(int i = 0; i < actions.size(); i++) {
			
			InputAction action = actions.get(i);
						
			if(action.valid) {
				if(action.Type == INPUT_ACTION_TYPE.ENTER) {
					if(action.Character.equalsIgnoreCase(" ")) {
						//typed the whole word
						word_actions.add(actions.subList(startIndex, i + 1));
						startIndex = i + 1;
					}
				}
				else if(action.Type == INPUT_ACTION_TYPE.WORD_COMPLETION){
					// it is an auto-complete, next space still belongs to this word
					i++;
					while(i < actions.size()) {
						action = actions.get(i);
						if(action != null && action.valid == true && action.Type == INPUT_ACTION_TYPE.ENTER &&
								action.Character.equalsIgnoreCase(" ")) {
							word_actions.add(actions.subList(startIndex, i + 1));
							break;
						}
						i++;
					}
					if(i < actions.size()) startIndex = i + 1;
				}
			}	
		}
		
		if(startIndex <= actions.size())
			word_actions.add(actions.subList(startIndex, actions.size()));
		
		if(word_actions.size() != words.length)
		{
			System.out.println("Error: split does not match!");
		}
		
		return word_actions;
	}
	
	static protected int countValid(List<InputAction> actions) {
		int counter = 0;
		for(InputAction action : actions) {
			if(action.valid) counter++;
		}
		
		return counter;
	}
	
	static protected boolean wasWordCompletion(List<InputAction> actions) {
		for(InputAction action : actions) {
			if(action.valid && action.Type == INPUT_ACTION_TYPE.WORD_COMPLETION) return true; 
		}
		return false;
	}
	
	static protected float countMin(String word, List<InputAction> actions) {
		float min = 0;
		
		for(InputAction action : actions) {
			if(action.valid) {
				min++;
				if(action.Suggestions.size() > 0) {
					for(String suggestion : action.Suggestions) {
						if(suggestion.equalsIgnoreCase(word)) return min + 1;
					}
				}
			}
		}
		return word.length() + 1;
	}
	
	static protected float countCorrectSuggestions(String word, List<InputAction> actions, int nsuggestions) {
		float min = 0;
		
		for(InputAction action : actions) {
			if(action.valid) {
				min++;
				for(int i = 0; i < action.Suggestions.size() && i < nsuggestions; i++)
				{
					String suggestion = action.Suggestions.get(i);
					if(suggestion.equalsIgnoreCase(word)) 
						return min + 1;
				}
				
			}
		}
		return word.length() + 1;
	}
	
	static protected float countCorrectAutoComplete(String word, List<InputAction> actions) {
		float min = 0;
		
		for(InputAction action : actions) {
			if(action.valid) {
				min++;
				for(int i = 0; i < action.Suggestions.size() && i < 1; i++)
				{
					String suggestion = action.Suggestions.get(i);
					if(action.willAutoCorrect && suggestion.equalsIgnoreCase(word)) 
						return min + 1;
				}
				
			}
		}
		return word.length() + 1;
	}
	
	static protected int countShown(List<InputAction> actions) {
		int counter = 0;
		for(InputAction action : actions) {
			if(action.valid) {
				counter++;
				if(action.willAutoCorrect) return counter;
			}
		}
		return counter;
	}
	
	static protected int countAutoComplete(List<InputAction> actions) {
		int counter = 0;
		for(InputAction action : actions) {
			if(action.valid && action.willAutoCorrect)
				counter++;
		}
		return counter;
	}
	
	static protected int countSuggestions(List<InputAction> actions) {
		int counter = 0;
		for(InputAction action : actions) {
			if(action.valid && action.Suggestions.size() > 0)
				counter++;
		}
		return counter;
	}
	
	static protected float countAllCorrectSuggestions(String word, List<InputAction> actions, int nsuggestions) {
		float counter = 0;
		
		for(InputAction action : actions) {
			if(action.valid) {
				for(int i = 0; i < action.Suggestions.size() && i < nsuggestions; i++)
				{
					String suggestion = action.Suggestions.get(i);
					if(suggestion.equalsIgnoreCase(word)) 
						counter++;
				}
				
			}
		}
		return counter;
	}
	
	static protected float countAllCorrectAutoComplete(String word, List<InputAction> actions) {
		float counter = 0;
		
		for(InputAction action : actions) {
			if(action.valid) {
				for(int i = 0; i < action.Suggestions.size() && i < 1; i++)
				{
					String suggestion = action.Suggestions.get(i);
					if(action.willAutoCorrect && suggestion.equalsIgnoreCase(word)) 
						counter++;
				}
				
			}
		}
		return counter;
	}
}
