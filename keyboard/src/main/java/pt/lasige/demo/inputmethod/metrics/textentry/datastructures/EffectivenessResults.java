package pt.lasige.demo.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class EffectivenessResults implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float mCorrect;
	protected float mIncorrectNotFixed;
	protected float mFixes;
	protected float mIncorrectButFixed;
	
	protected float mKSPC;
	protected float mMSDErrorRate;
	protected float mTotalErrorRate;
	protected float mUncorrectedErrorRate;
	protected float mCorrectedErrorRate;
	
	public EffectivenessResults() 
	{
		mCorrect = 0;
		mIncorrectNotFixed = 0;
		mFixes = 0;
		mIncorrectButFixed = 0;
	}
	
	public EffectivenessResults(float correct, float incorrectnotfixed, float fixes, float incorrectbutfixed)
	{
		mCorrect = correct;
		mIncorrectNotFixed = incorrectnotfixed;
		mFixes = fixes;
		mIncorrectButFixed = incorrectbutfixed;
	}

	public float getKSPC() {
		return mKSPC;
	}

	public void setKSPC(float mKSPC) {
		this.mKSPC = mKSPC;
	}

	public float getMSDErrorRate() {
		return mMSDErrorRate;
	}

	public void setMSDErrorRate(float mMSDErroRate) {
		this.mMSDErrorRate = mMSDErroRate;
	}

	public float getTotalErrorRate() {
		return mTotalErrorRate;
	}

	public void setTotalErrorRate(float mTotalErrorRate) {
		this.mTotalErrorRate = mTotalErrorRate;
	}

	public float getUncorrectedErrorRate() {
		return mUncorrectedErrorRate;
	}

	public float getCorrectedErrorRate() {
		return mCorrectedErrorRate;
	}

	public void setCorrectedErrorRate(float mCorrectedErrorRate) {
		this.mCorrectedErrorRate = mCorrectedErrorRate;
	}

	public void setUncorrectedErrorRate(float mUncorrectedErrorRate) {
		this.mUncorrectedErrorRate = mUncorrectedErrorRate;
	}

	public float getCorrect() {
		return mCorrect;
	}

	public void setCorrect(float correct) {
		mCorrect = correct;
	}

	public float getIncorrectNotFixed() {
		return mIncorrectNotFixed;
	}

	public void setIncorrectNotFixed(float incorrectNotFixed) {
		mIncorrectNotFixed = incorrectNotFixed;
	}

	public float getFixes() {
		return mFixes;
	}

	public void setFixes(float fixes) {
		mFixes = fixes;
	}

	public float getIncorrectButFixed() {
		return mIncorrectButFixed;
	}

	public void setIncorrectButFixed(float incorrectButFixed) {
		mIncorrectButFixed = incorrectButFixed;
	}
	
	public String toString()
	{
		if(mCorrect == 0 && mIncorrectNotFixed == 0 && mFixes == 0 && mIncorrectButFixed == 0) { // no trials
			return " \t \t \t \t \t \t \t \t ";
		}
			
		String s = "";
		
		s += this.getKSPC() + "\t" + this.getMSDErrorRate() + "\t" + this.getCorrect() + "\t" + this.getIncorrectNotFixed()
		 + "\t" + this.getFixes() + "\t" + this.getIncorrectButFixed() + "\t" + this.getTotalErrorRate() + "\t" + 
		 this.getUncorrectedErrorRate() + "\t" + this.getCorrectedErrorRate();
		
		return s;
	}
	
	static public String getHeader()
	{
		String s = "KSPC\tMSDErrRate\t|C|\t|INF|\t|F|\t|IF|\tTotErrRate\tUncErrRate\tCorErrRate";
		return s;
	}

}
