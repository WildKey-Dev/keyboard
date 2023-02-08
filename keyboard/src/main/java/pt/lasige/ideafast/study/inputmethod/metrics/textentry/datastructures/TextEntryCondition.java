package pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class TextEntryCondition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<TextEntryParticipantSession> mSessions;
	protected TextEntryResults mConditionResults;
	protected String mName="";
	
	public TextEntryCondition(String name)
	{
		mName = name;
		mSessions = new ArrayList<TextEntryParticipantSession>();
		mConditionResults = new TextEntryResults();
	}

	public ArrayList<TextEntryParticipantSession> getSessions() {
		return mSessions;
	}

	public void setSessions(ArrayList<TextEntryParticipantSession> sessions) {
		this.mSessions = sessions;
	}

	public TextEntryResults getConditionResults() {
		return mConditionResults;
	}

	public void setConditionResults(TextEntryResults conditionResults) {
		this.mConditionResults = conditionResults;
	}
	
	public void setName(String name){
		mName = name;
	}
	
	public String getName(){
		return mName;
	}
}
