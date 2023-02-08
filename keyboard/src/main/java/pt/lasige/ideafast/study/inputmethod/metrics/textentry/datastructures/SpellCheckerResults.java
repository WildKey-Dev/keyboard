package pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class SpellCheckerResults implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int N = 10;
	protected float mCorrectEntries;
	protected float mIncorrectEntries;
	protected float[] mIncorrectButTopFix;
	protected float mIncorrectButSuggested;
	protected float mIncorrectNotFixed;
	protected float mCorrectNotFixed;
	protected float mCorrectButIncorrectlyFixed;
	
	protected float mWordsEditDistance[] = {0, 0, 0, 0, 0, 0};
	
	public SpellCheckerResults()
	{
		this.mCorrectEntries = 0;
		this.mIncorrectEntries = 0;
		this.mIncorrectButTopFix = new float[N];
		this.mIncorrectButSuggested = 0;
		this.mIncorrectNotFixed = 0;
		this.mCorrectNotFixed = 0;
		this.mCorrectButIncorrectlyFixed = 0;
	}
	
	public SpellCheckerResults(float correctEntries, float incorrectEntries, float[] incorrectButTopFix,
			float incorrectButSuggested, float incorrectNotFixed, float correctNotFixed, float correctButIncorrectlyFixed) {
		this.mCorrectEntries = correctEntries;
		this.mIncorrectEntries = incorrectEntries;
		this.mIncorrectButTopFix = incorrectButTopFix;
		this.mIncorrectButSuggested = incorrectButSuggested;
		this.mIncorrectNotFixed = incorrectNotFixed;
		this.mCorrectNotFixed = correctNotFixed;
		this.mCorrectButIncorrectlyFixed = correctButIncorrectlyFixed;
	}

	public SpellCheckerResults(float correctEntries, float incorrectEntries, float[] incorrectButTopFix,
			float incorrectButSuggested, float incorrectNotFixed, float correctNotFixed, float correctButIncorrectlyFixed,
			float[] wordsEditDistance) 
	{
		this.mCorrectEntries = correctEntries;
		this.mIncorrectEntries = incorrectEntries;
		this.mIncorrectButTopFix = incorrectButTopFix;
		this.mIncorrectButSuggested = incorrectButSuggested;
		this.mIncorrectNotFixed = incorrectNotFixed;
		this.mCorrectNotFixed = correctNotFixed;
		this.mCorrectButIncorrectlyFixed = correctButIncorrectlyFixed;
		
		this.mWordsEditDistance = wordsEditDistance;
	}
	
	public float[] getWordDistance()
	{
		return mWordsEditDistance;
	}
	
	public void setWordDistance(float[] wordDistance)
	{
		mWordsEditDistance = wordDistance;
	}
	
	public float getCorrectEntries() {
		return mCorrectEntries;
	}

	public void setCorrectEntries(float correctEntries) {
		this.mCorrectEntries = correctEntries;
	}

	public float getIncorrectEntries() {
		return mIncorrectEntries;
	}

	public void setIncorrectEntries(float incorrectEntries) {
		this.mIncorrectEntries = incorrectEntries;
	}

	public float[] getIncorrectButTopFix() {
		return mIncorrectButTopFix;
	}

	public void setIncorrectButTopFix(float[] incorrectButTopFix) {
		this.mIncorrectButTopFix = incorrectButTopFix;
	}

	public float getIncorrectButSuggested() {
		return mIncorrectButSuggested;
	}

	public void setIncorrectButSuggested(float incorrectButSuggested) {
		this.mIncorrectButSuggested = incorrectButSuggested;
	}

	public float getIncorrectNotFixed() {
		return mIncorrectNotFixed;
	}

	public void setIncorrectNotFixed(float incorrectNotFixed) {
		this.mIncorrectNotFixed = incorrectNotFixed;
	}

	public float getCorrectNotFixed() {
		return mCorrectNotFixed;
	}

	public void setCorrectNotFixed(float correctNotFixed) {
		this.mCorrectNotFixed = correctNotFixed;
	}

	public float getCorrectButIncorrectlyFixed() {
		return mCorrectButIncorrectlyFixed;
	}

	public void setCorrectButIncorrectlyFixed(float correctButIncorrectlyFixed) {
		this.mCorrectButIncorrectlyFixed = correctButIncorrectlyFixed;
	}
	
	static public String getHeader()
	{
		String ret = "|CorrEntries|\t|IncEntries|";
		for(int i = 0; i < N; i++)
		{
			ret += "\tIncTopF" + (i+1); 
		}
		ret += "\tIncSuggested\tIncNF\tCorrNF\tCorrIF\tEditDistance1\tEditDistance2\tEditDistance3\tEditDistance4\tEditDistance5\tEditDistance6+";
		return ret;
	}
	
	public String toString()
	{
		String ret = "";
		ret += this.getCorrectEntries() + "\t" + this.getIncorrectEntries();
		
		for(int i = 0; i < this.getIncorrectButTopFix().length; i++)
		{
			if(this.getIncorrectButTopFix()[i] == 0)
				ret += "\t" + 0;
			else
				ret += "\t" + this.getIncorrectButTopFix()[i] / this.getIncorrectEntries();
		}
		
		float incsuggested = this.getIncorrectEntries() == 0 ? 0 : this.getIncorrectButSuggested() / this.getIncorrectEntries();
		float incnf = this.getIncorrectEntries() == 0 ? 0 : this.getIncorrectNotFixed() / this.getIncorrectEntries();
		float corrnf = this.getCorrectEntries() == 0 ? 0 : this.getCorrectNotFixed() / this.getCorrectEntries();
		float corrif = this.getCorrectEntries() == 0 ? 0 : this.getCorrectButIncorrectlyFixed() / this.getCorrectEntries();
		
		ret += "\t" + incsuggested + "\t" + incnf + "\t" + corrnf  + "\t" + corrif;
		
		for(int i = 0; i < mWordsEditDistance.length; i++)
		{
			ret += "\t" + mWordsEditDistance[i] / this.getIncorrectEntries();
		}
		
		return ret;
	}

}
