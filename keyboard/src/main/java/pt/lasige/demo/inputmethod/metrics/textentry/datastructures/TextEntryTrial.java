package pt.lasige.demo.inputmethod.metrics.textentry.datastructures;
import java.io.Serializable;
import java.util.ArrayList;


public class TextEntryTrial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// trial vars
	protected String mRequiredSentence;
	protected String mTranscribedSentence;
	protected String mInputStream;
	public enum INPUT_ACTION_TYPE {FOCUS, ENTER, TOUCH_DOWN, TOUCH_MOVE, TOUCH_UP, WORD_COMPLETION, WORD_SUGGESTION};
	protected ArrayList<InputAction> mInputActions;
	private ArrayList<SuggestionEntry> mSuggestions;
	
	// trial results
	protected TextEntryResults mTrialResults;
	
	public TextEntryTrial()
	{
		mInputActions = new ArrayList<InputAction>();
		mTrialResults = new TextEntryResults();
		mSuggestions = new ArrayList<SuggestionEntry>();
		mRequiredSentence = "";
		mTranscribedSentence = "";
		mInputStream = "";
	}
	
	public ArrayList<SuggestionEntry> getSuggestions() {
		return mSuggestions;
	}

	public void setSuggestions(ArrayList<SuggestionEntry> suggestions) {
		this.mSuggestions = suggestions;
	}
	
	public String getInputStream() {
		return mInputStream;
	}

	public void setInputStream(String inputStream) {
		mInputStream = inputStream;
	}

	public TextEntryResults getTrialResults() {
		return (TextEntryResults)mTrialResults;
	}

	public void setTrialResults(TextEntryResults trialResults) {
		mTrialResults = trialResults;
	}

	public ArrayList<InputAction> getInputActions() {
		return mInputActions;
	}

	public void setInputActions(ArrayList<InputAction> inputActions) {
		mInputActions = inputActions;
	}
	
	public String getRequiredSentence() {
		return mRequiredSentence;
	}

	public void setRequiredSentence(String requiredSentence) {
		mRequiredSentence = requiredSentence;
	}

	public String getTranscribedSentence() {
		return mTranscribedSentence;
	}

	public void setTranscribedSentence(String transcribedSentence) {
		mTranscribedSentence = transcribedSentence;
	}
}
