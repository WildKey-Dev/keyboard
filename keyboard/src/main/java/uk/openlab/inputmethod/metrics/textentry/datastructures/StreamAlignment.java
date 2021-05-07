package uk.openlab.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class StreamAlignment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String mRequired;
	protected String mTranscribed;
	protected String mInputstream;
	protected String mFlaggedIS;
	protected String mPositionValues;
	
	public StreamAlignment(String required, String transcribed, String inputstream, String flaggedInputstream)
	{
		mRequired = required;
		mTranscribed = transcribed;
		mInputstream = inputstream;
		mFlaggedIS = flaggedInputstream;
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
	
	public String getInputstream() {
		return mInputstream;
	}

	public void setInputstream(String inputstream) {
		mInputstream = inputstream;
	}
	
	public String getFlaggedInputstream() {
		return mFlaggedIS;
	}

	public void setFlaggedInputstream(String flaggedInputstream) {
		mFlaggedIS = flaggedInputstream;
	}
	
	public void setPositionValues(String positionvalues)
	{
		mPositionValues = positionvalues;
	}
	
	public String getPositionValues()
	{
		return mPositionValues;
	}
}
