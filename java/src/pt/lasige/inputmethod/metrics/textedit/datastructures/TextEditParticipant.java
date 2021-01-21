package pt.lasige.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class TextEditParticipant implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// session vars
	protected String mParticipant;
	protected String mCondition;
	protected ArrayList<TextEditTrial> mTrials;
	
	// participant results
	protected TextEditResults mParticipantResults;
	
	public TextEditParticipant(String participant, String condition)
	{
		this.mParticipant = participant;
		this.mCondition = condition;
		this.mTrials = new ArrayList<TextEditTrial>();
		this.mParticipantResults = new TextEditResults();
	}

	
	public TextEditResults getParticipantResults() {
		return mParticipantResults;
	}

	public void setParticipantResults(TextEditResults mParticipantResults) {
		this.mParticipantResults = mParticipantResults;
	}

	public String getCondition() {
		return mCondition;
	}

	public void setCondition(String mCondition) {
		this.mCondition = mCondition;
	}

	public String getName() {
		return mParticipant;
	}

	public void setName(String participant) {
		mParticipant = participant;
	}

	public ArrayList<TextEditTrial> getTrials() {
		return mTrials;
	}

	public void setTrials(ArrayList<TextEditTrial> trials) {
		mTrials = trials;
	}

}
