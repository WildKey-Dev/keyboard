package pt.lasige.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class TextEntryStudy implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<TextEntryCondition> mConditions;
	protected TextEntryResults mStudyResults;
	protected String mName = "";
	
	public TextEntryStudy(String name)
	{
		mName = name;
		mConditions = new ArrayList<TextEntryCondition>();
		mStudyResults = new TextEntryResults();
	}

	public ArrayList<TextEntryCondition> getConditions() {
		return mConditions;
	}

	public void setSessions(ArrayList<TextEntryCondition> conditions) {
		this.mConditions = conditions;
	}

	public TextEntryResults getConditionResults() {
		return mStudyResults;
	}

	public void setConditionResults(TextEntryResults studyResults) {
		this.mStudyResults = studyResults;
	}
	
	public void setName(String name){
		mName = name;
	}
	
	public String getName(){
		return mName;
	}
}
