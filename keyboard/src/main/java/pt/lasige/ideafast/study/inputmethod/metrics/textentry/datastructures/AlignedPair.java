package pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class AlignedPair implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String mRequired;
	protected String mTranscribed;
	
	public AlignedPair(String required, String transcribed)
	{
		mRequired = required;
		mTranscribed = transcribed;
	}

	public String getRequired() {
		return mRequired;
	}

	public void setRequired(String required) {
		mRequired = required;
	}

	public String getTranscribed() {
		return mTranscribed;
	}

	public void setTranscribed(String transcribed) {
		mTranscribed = transcribed;
	}
}
