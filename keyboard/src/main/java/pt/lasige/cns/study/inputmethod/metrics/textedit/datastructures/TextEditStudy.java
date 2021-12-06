package pt.lasige.cns.study.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class TextEditStudy implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected ArrayList<TextEditCondition> mConditions;
	protected TextEditResults mStudyResults;
	protected String mName = "";
	
	public TextEditStudy(String name)
	{
		mName = name;
		mConditions = new ArrayList<TextEditCondition>();
		mStudyResults = new TextEditResults();
	}

	public ArrayList<TextEditCondition> getConditions() {
		return mConditions;
	}

	public void setSessions(ArrayList<TextEditCondition> conditions) {
		this.mConditions = conditions;
	}

	public TextEditResults getConditionResults() {
		return mStudyResults;
	}

	public void setConditionResults(TextEditResults studyResults) {
		this.mStudyResults = studyResults;
	}
	
	public void setName(String name){
		mName = name;
	}
	
	public String getName(){
		return mName;
	}
}
