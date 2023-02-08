package pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class TextEntryMSD implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int[][] mMatrix;
	//protected int mSubstitutions;
	//protected int mInsertions;
	//protected int mOmissions;
	protected int mCorrect;
	
	public TextEntryMSD()
	{
		/*this.mSubstitutions = 0;
		this.mOmissions = 0;
		this.mInsertions = 0;*/
		this.mCorrect = 0;
	}
	
	public int[][] getMatrix() {
		return mMatrix;
	}

	public void setMatrix(int[][] Matrix) {
		this.mMatrix = Matrix;
	}

	public int getMSD()
	{
		int l = mMatrix.length;
		int c = mMatrix[mMatrix.length - 1].length;
		return mMatrix[l - 1][c - 1];
	}
	
	public int getCorrect() {
		return mCorrect;
	}

	public void setCorrect(int Correct) {
		this.mCorrect = Correct;
	}

	/*public int getSubstitutions() {
		return mSubstitutions;
	}

	public void setSubstitutions(int Substitutions) {
		this.mSubstitutions = Substitutions;
	}

	public int getInsertions() {
		return mInsertions;
	}

	public void setInsertions(int Insertions) {
		this.mInsertions = Insertions;
	}

	public int getOmissions() {
		return mOmissions;
	}

	public void setOmissions(int Omissions) {
		this.mOmissions = Omissions;
	}*/

	
}
