package pt.lasige.ideafast.study.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class TextEditCondition implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected ArrayList<TextEditParticipant> mParticipants;
	protected TextEditResults mConditionResults;
	protected String mName="";
	
	public TextEditCondition(String name)
	{
		mName = name;
		mParticipants = new ArrayList<TextEditParticipant>();
		mConditionResults = new TextEditResults();
	}

	public ArrayList<TextEditParticipant> getParticipants() {
		return mParticipants;
	}

	public void setParticipants(ArrayList<TextEditParticipant> participants) {
		this.mParticipants = participants;
	}

	public TextEditResults getConditionResults() {
		return mConditionResults;
	}

	public void setConditionResults(TextEditResults conditionResults) {
		this.mConditionResults = conditionResults;
	}
	
	public void setName(String name){
		mName = name;
	}
	
	public String getName(){
		return mName;
	}
}
