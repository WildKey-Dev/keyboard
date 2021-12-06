package pt.lasige.cns.study.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class TextEditTrial implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// trial vars
	protected String mOriginalSentence;
	protected String mRequiredSentence;
	protected String mTranscribedSentence;
	protected String mInputStream;
	protected ArrayList<EditAction> mEditActions;
	
	// trial results
	protected TextEditResults mTrialResults;
	
	public TextEditTrial()
	{
		mEditActions = new ArrayList<EditAction>();
		mTrialResults = new TextEditResults();
		mRequiredSentence = "";
		mTranscribedSentence = "";
		mInputStream = "";
	}
	
	public String getInputStream() {
		return mInputStream;
	}

	public void setInputStream(String inputStream) {
		mInputStream = inputStream;
	}

	public TextEditResults getTrialResults() {
		return (TextEditResults)mTrialResults;
	}

	public void setTrialResults(TextEditResults trialResults) {
		mTrialResults = trialResults;
	}

	public ArrayList<EditAction> getEditActions() {
		return mEditActions;
	}

	public void setEditActions(ArrayList<EditAction> editActions) {
		mEditActions = editActions;
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
	
	public void setOriginalSentence(String originalSentence) {
		mOriginalSentence = originalSentence;
	}
	
	public String getOriginalSentence() {
		return mOriginalSentence;
	}

}
