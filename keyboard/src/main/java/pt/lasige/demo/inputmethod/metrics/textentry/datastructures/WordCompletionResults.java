package pt.lasige.demo.inputmethod.metrics.textentry.datastructures;

import java.text.DecimalFormat;

public class WordCompletionResults {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// efficiency
	protected float mTime; // in seconds
	protected float mWPM;
	
	// effectiveness
	protected float mErrorRate;
	
	protected float mKSROptimal;
	protected float mKSROffered;
	protected float mKSRUsed;
	protected float mKSSRUserLen;
	
	protected float mNSelections;
	
	protected float mShown; //%
	protected float mCorrect; //%
	protected float mUsed; //%
	
	public WordCompletionResults() {
		mTime = 0;
		mWPM = 0;
		
		mErrorRate = 0;
		
		mKSRUsed = 0;
		mKSROptimal = 0;
		mKSROffered = 0;
		mKSSRUserLen = 0;
		
		mNSelections = 0;
		
		mShown = 0;
		mCorrect = 0;
		mUsed = 0;
	}

	public float getWPM() { return mWPM; }
	public void setWPM(float wpm) {	this.mWPM = wpm; }
	
	public float getTime() { return mTime; }
	public void setTime(float time) { this.mTime = time; }
	
	public float getErrorRate() { return mErrorRate; }
	public void setErrorRate(float errorrate) { this.mErrorRate = errorrate; }
	
	public float getKSROptimal() { return mKSROptimal; }
	public void setKSROptimal(float kssr) { this.mKSROptimal = kssr; }
	
	public float getKSROffered() { return mKSROffered; }
	public void setKSROffered(float kssr) { this.mKSROffered = kssr; }
	
	public float getKSRUsed() { return mKSRUsed; }
	public void setKSRUsed(float kssr) { this.mKSRUsed = kssr; }
	
	public float getKSSRUserLen() { return mKSSRUserLen; }
	public void setKSSRUserLen(float kssr) { this.mKSSRUserLen = kssr; }
	
	public float getNSelections() { return mNSelections; }
	public void setNSelections(float n) { this.mNSelections = n; }
	
	public float getShown() { return mShown; }
	public void setShown(float shown) { this.mShown = shown; }
	
	public float getCorrect() { return mCorrect; }
	public void setCorrect(float correct) { this.mCorrect = correct; }
	
	public float getUsed() { return mUsed; }
	public void setUsed(float used) { this.mUsed= used; }
	
	static public String getHeader()
	{
		return "Time\tWPM\tErrRate\tKSROptimal\tKSROffered\tKSRUsed\tKSSRUserLen\tTotal Selections\tShown(%)\tCorrect(%)\tUsed(%)";
	}
	
	public String toString()
	{
		if(mTime == 0 && mWPM == 0) return " \t \t \t \t \t \t \t"; //no trials
		
		DecimalFormat df = new DecimalFormat("####.000");
		return df.format(this.getTime()) + "\t" + df.format(this.getWPM()) + "\t" + df.format(this.getErrorRate()) +
				"\t" + df.format(this.getKSROptimal()) +
				"\t" + df.format(this.getKSROffered()) +
				"\t" + df.format(this.getKSRUsed()) + 
				"\t" + df.format(this.getKSSRUserLen()) + 
				"\t" + df.format(this.getNSelections()) +
				"\t" + df.format(this.getShown()) + 
				"\t" + df.format(this.getCorrect()) + 
				"\t" + df.format(this.getUsed());
	}
}
