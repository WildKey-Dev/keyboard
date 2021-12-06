package pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class IntentResults implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String mIntendedSentence = "";
	
	// transcribed MSD
	protected float mTranscribedMSD = 0;
	protected float mTranscribedMWD = 0;
	
	// intent
	protected float mMSD = 0;
	protected float mMWD = 0;
	
	protected float mRER_MSD = 0;
	protected float mRER_MWD = 0;
	
	public void setMSD(float msd) { mMSD = msd; }
	public float getMSD() { return mMSD; }
	
	public void setMWD(float mwd) { mMWD = mwd; }
	public float getMWD() { return mMWD; }
	
	public void setRER_MSD(float rer_msd) { mRER_MSD = rer_msd; }
	public float getRER_MSD() { return mRER_MSD; }
	
	public void setRER_MWD(float rer_mwd) { mRER_MWD = rer_mwd; }
	public float getRER_MWD() { return mRER_MWD; }
	
	public void setIntendedSentence(String intended) { mIntendedSentence = intended; }
	public String getIntendedSentence() { return mIntendedSentence; }
	
	public void setTranscribedMSD(float msd) { mTranscribedMSD = msd; }
	public float getTranscribedMSD() { return mTranscribedMSD; }
	
	public void setTranscribedMWD(float mwd) { mTranscribedMWD = mwd; }
	public float getTranscribedMWD() { return mTranscribedMWD; }
	
	public boolean isValid()
	{
		return !(Float.isNaN(mTranscribedMSD) || Float.isNaN(mTranscribedMWD) || Float.isNaN(mMSD) || Float.isNaN(mMWD) ||
				Float.isNaN(mRER_MSD) || Float.isNaN(mRER_MWD));
	}
	
	static public String getHeader()
	{
		String ret = "Trans. MSD ErrRate\t Trans. MWD Err Rate\tIntent MSD ErrRate\tIntent MWD ErrRate\tRER MSD\tRER MWD";
		return ret;
	}
	
	static public String getIndividualHeader()
	{
		return "Intent\t" + getHeader();
	}
	
	public String toString()
	{
		String s = "";
		
		s += this.getTranscribedMSD() + "\t" + this.getTranscribedMWD() + "\t" + this.getMSD() + "\t" + 
				this.getMWD() + "\t" + this.getRER_MSD() + "\t" + this.getRER_MWD();
		return s;
	}
	
	public String toStringIndividual()
	{
		String s = mIntendedSentence;
		
		s += "\t" + toString();
		
		return s;
	}
		
}
