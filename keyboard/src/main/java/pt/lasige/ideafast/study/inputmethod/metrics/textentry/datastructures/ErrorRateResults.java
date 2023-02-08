package pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class ErrorRateResults implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float mUnc = 0;
	protected float mCorr = 0;
	protected float mNR = 0;
	protected float mTotal = 0;
	
	protected float mCorrCount = 0;
	protected float mUncCount = 0;
	protected float mNRCount = 0;
	
	public ErrorRateResults(){}

	public float getUnc() {
		return mUnc;
	}

	public void setUnc(float Unc) {
		this.mUnc = Unc;
	}

	public float getCorr() {
		return mCorr;
	}

	public void setCorr(float Corr) {
		this.mCorr = Corr;
	}

	public float getTotal() {
		return mTotal;
	}

	public void setTotal(float Total) {
		this.mTotal = Total;
	}

	public float getCorrCount() {
		return mCorrCount;
	}

	public void setCorrCount(float corrCount) {
		this.mCorrCount = corrCount;
	}
	
	public float addCorrCount()
	{
		mCorrCount++;
		return mCorrCount;
	}
	
	public float addCorrCount(float n)
	{
		mCorrCount += n;
		return mCorrCount;
	}
	
	public float getUncCount() {
		return mUncCount;
	}

	public void setUncCount(float uncCount) {
		this.mUncCount = uncCount;
	}
	
	public float addUncCount()
	{
		mUncCount++;
		return mUncCount;
	}

	public float addUncCount(float n)
	{
		mUncCount += n;
		return mUncCount;
	}

	public float getNRCount() {
		return mNRCount;
	}

	public void setNRCount(float NRCount) {
		this.mNRCount = NRCount;
	}
	
	public float addNonRecCount()
	{
		mNRCount++;
		return mNRCount;
	}
	
	public float addNonRecCount(float n)
	{
		mNRCount += n;
		return mNRCount;
	}
	
	public void setNR(float nr)
	{
		mNR = nr;
	}
	
	public float getNR()
	{
		return mNR;
	}
}
