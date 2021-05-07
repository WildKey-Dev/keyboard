package uk.openlab.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.text.DecimalFormat;

public class EfficiencyResults implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float mTime; // in seconds
	
	public float[] mTimeTasks = new float[7];
	public float mKeyboardTasks = Float.NaN; //2, 4
	public float mCaretTasks = Float.NaN; //1, 3, 7
	public float mClipboardTasks = Float.NaN; //5, 6
	
	public EfficiencyResults()
	{
		mTime = 0;
		for(int i = 0; i < mTimeTasks.length; i++) {
			mTimeTasks[i] = Float.NaN;
		}
	}

	public void averageByTypeOfTask() {
		if(Float.isNaN(mTimeTasks[1]))
			if(Float.isNaN(mTimeTasks[2])) mKeyboardTasks = Float.NaN;
			else mKeyboardTasks = mTimeTasks[2];
		else if(Float.isNaN(mTimeTasks[2])) mKeyboardTasks = mTimeTasks[1];
		else mKeyboardTasks = (mTimeTasks[1] + mTimeTasks[3]) / 2;
		
		float aux = 0;
		float counter = 0;
		if(!Float.isNaN(mTimeTasks[0])) { aux += mTimeTasks[0]; counter++; }
		if(!Float.isNaN(mTimeTasks[2])) { aux +=mTimeTasks[2]; counter++; }
		if(!Float.isNaN(mTimeTasks[6])) { aux +=mTimeTasks[6]; counter++; }
		if(counter>0) mCaretTasks = aux / counter;
		else mCaretTasks = Float.NaN;
		
		if(Float.isNaN(mTimeTasks[4]))
			if(Float.isNaN(mTimeTasks[5])) mClipboardTasks = Float.NaN;
			else mClipboardTasks = mTimeTasks[5];
		else if(Float.isNaN(mTimeTasks[5])) mClipboardTasks = mTimeTasks[4];
		else mClipboardTasks = (mTimeTasks[4] + mTimeTasks[5]) / 2;
	}
	
	public float getTime() {
		return mTime;
	}

	public void setTime(float time) {
		this.mTime = time;
	}
	
	public boolean isValid()
	{
		return !(Float.isNaN(mTime));
	}
	static public String getHeader()
	{
		return "Time";
	}
	
	public String toString()
	{
		if(mTime == 0) return " "; //no trials
		
		DecimalFormat df = new DecimalFormat("####.000");
		return df.format(this.getTime());
	}
	
}
