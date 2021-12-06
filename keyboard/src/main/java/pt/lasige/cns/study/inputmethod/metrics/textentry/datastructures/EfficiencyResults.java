package pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.text.DecimalFormat;

public class EfficiencyResults implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float mTime; // in seconds
	protected float mCorrectionTime; // in seconds
	protected float mInterKeyTime; // in seconds
	protected float mWPM;
	protected float mCPS;
	
	public EfficiencyResults()
	{
		mTime = 0;
		mCorrectionTime = 0;
		mWPM = 0;
		mCPS = 0;
	}

	public float getWPM() {
		return mWPM;
	}

	public void setWPM(float wpm) {
		this.mWPM = wpm;
	}

	public float getCPS() {
		return mCPS;
	}

	public void setCPS(float cps) {
		this.mCPS = cps;
	}

	public float getTime() {
		return mTime;
	}

	public void setTime(float time) {
		this.mTime = time;
	}
	
	public float getCorrectionTime()
	{
		return mCorrectionTime;
	}
	
	public void setCorrectionTime(float correctionTime)
	{
		mCorrectionTime = correctionTime;
	}
	
	public float getInterKeyTime()
	{
		return mInterKeyTime;
	}
	
	public void setInterKeyTime(float time)
	{
		mInterKeyTime = time;
	}
	
	public boolean isValid()
	{
		return !(Float.isNaN(mTime) || Float.isNaN(mCorrectionTime) || Float.isNaN(mCPS) || Float.isNaN(mWPM) || 
				Float.isNaN(mInterKeyTime));
	}
	static public String getHeader()
	{
		return "Time\tCorr Time(%)\tCPS\tWPM\tInter-Key Time";
	}
	
	public String toString()
	{
		if(mTime == 0 && mCorrectionTime ==0 && mWPM == 0 && mCPS == 0) return " \t \t \t \t "; //no trials
		
		DecimalFormat df = new DecimalFormat("####.000");
		return df.format(this.getTime()) + "\t" + df.format(this.getCorrectionTime()) + "\t" + df.format(this.getCPS()) + "\t" + df.format(this.getWPM()) + "\t" + df.format(this.getInterKeyTime());
	}
	
}
