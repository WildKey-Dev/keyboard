package pt.lasige.demo.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class TextEntryParticipantSession implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// session vars
	protected String mParticipant;
	protected String mCondition;
	protected ArrayList<TextEntryTrial> mTrials;
	
	public int nsuggestions = 0;
	
	// session results
	protected TextEntryResults mSessionResults;
	
	public TextEntryParticipantSession(String participant, String condition)
	{
		this.mParticipant = participant;
		this.mCondition = condition;
		this.mTrials = new ArrayList<TextEntryTrial>();
		this.mSessionResults = new TextEntryResults();
	}

	
	public TextEntryResults getSessionResults() {
		return mSessionResults;
	}

	public void setSessionResults(TextEntryResults mSessionResults) {
		this.mSessionResults = mSessionResults;
	}

	public String getCondition() {
		return mCondition;
	}

	public void setCondition(String mCondition) {
		this.mCondition = mCondition;
	}

	public String getParticipant() {
		return mParticipant;
	}

	public void setParticipant(String participant) {
		mParticipant = participant;
	}

	public ArrayList<TextEntryTrial> getTrials() {
		return mTrials;
	}

	public void setTrials(ArrayList<TextEntryTrial> trials) {
		mTrials = trials;
	}

}
