package pt.lasige.cns.study.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;

public class EffectivenessResults implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float mMSDTranscribed;
	protected float mMSDOriginal;
	protected float mMSDEdit;
	
	protected float mCompletionRate;
	
	// TASK-DEPENDENT
	public float[] mMSDTasks = new float[7];
	public float[] mCompletionTasks = new float[7];
	
	public float[] mKeyboardTasks = new float[2]; //2, 4
	public float[] mCaretTasks = new float[2]; //1, 3, 7
	public float[] mClipboardTasks = new float[2]; //5, 6
	
	public EffectivenessResults() 
	{
		mMSDTranscribed = 0.0f;
		mMSDOriginal = 0;
		mMSDEdit = 0;
		mCompletionRate = 0.0f;
		
		for(int i = 0 ; i < 7; i++) mMSDTasks[i] = 100.0f;
		/*mMSDTasks[0] = 1;
		mMSDTasks[1] = 6;
		mMSDTasks[2] = 1;
		mMSDTasks[3] = 5;
		mMSDTasks[4] = 6;
		mMSDTasks[5] = 24;
		mMSDTasks[6] = 9;*/
	}
	
	public EffectivenessResults(float msdtranscribed, float msdoriginal, float msdedit, float completion)
	{
		mMSDTranscribed = msdtranscribed;
		mMSDOriginal = msdoriginal;
		mMSDEdit = msdedit;
		mCompletionRate = completion;
		
		for(int i = 0 ; i < 7; i++) mMSDTasks[i] = 100.0f;
		/*mMSDTasks[0] = 1.0f / (1.0f + 24.0f) * 100.0f;
		mMSDTasks[1] = 6.0f / (6.0f + 19.0f) * 100.0f;
		mMSDTasks[2] = 1.0f / (1.0f + 24.0f) * 100.0f;
		mMSDTasks[3] = 5.0f / (5.0f + 20.0f) * 100.0f;
		mMSDTasks[4] = 6.0f / (5.0f + 20.0f) * 100.0f;
		mMSDTasks[5] = 24.0f / (24.0f + 79.0f) * 100.0f;;
		mMSDTasks[6] = 9.0f / (9.0f + 83.0f) * 100.0f;;*/
	}

	public void averageByTypeOfTask() {
		mKeyboardTasks[0] = (mCompletionTasks[1] + mCompletionTasks[3]) / 2;
		mCaretTasks[0] = (mCompletionTasks[0] + mCompletionTasks[2] + mCompletionTasks[6]) / 3;
		mClipboardTasks[0] = (mCompletionTasks[4] + mCompletionTasks[5]) / 2;
		
		mKeyboardTasks[1] = (mMSDTasks[1] + mMSDTasks[3]) / 2;
		mCaretTasks[1] = (mMSDTasks[0] + mMSDTasks[2] + mMSDTasks[6]) / 3;
		mClipboardTasks[1] = (mMSDTasks[4] + mMSDTasks[5]) / 2;
	}
	
	public float getMSDTranscribed() {
		return mMSDTranscribed;
	}
	
	public float getMSDOriginal() {
		return mMSDOriginal;
	}
	
	public float getMSDEdit() {
		return mMSDEdit;
	}
	
	public float getCompletionRate() {
		return mCompletionRate;
	}
	
	public void setMSDTranscribed(float msd) {
		mMSDTranscribed = msd;
	}
	
	public void setMSDOriginal(float msd) {
		mMSDOriginal = msd;
	}
	
	public void setMSDEdit(float msd) {
		mMSDEdit = msd;
	}
	
	public void setCompletionRate(float completion) {
		mCompletionRate = completion;
	}
	
	public String toString()
	{
		String s = "";
		
		s += this.mCompletionRate + "\t" + this.mMSDTranscribed;
		
		return s;
	}
	
	static public String getHeader()
	{
		String s = "CompletionRate\tMSD";
		return s;
	}
}
