package pt.lasige.ideafast.study.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.text.DecimalFormat;

public class EventsResults implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float mSelections; // number of selections
	protected float mCaretMovements; // number of cursor movements
	protected float mEvents; // number of events
	
	// task-specific #events
	public float[] mEventsTasks = new float[7];
	public float mKeyboardTasks = Float.NaN; //2, 4
	public float mCaretTasks = Float.NaN; //1, 3, 7
	public float mClipboardTasks = Float.NaN; //5, 6
	
	public EventsResults()
	{
		mSelections = 0;
		mCaretMovements = 0;
		mEvents = 0;
		
		for(int i = 0; i < mEventsTasks.length; i++) {
			mEventsTasks[i] = Float.NaN;
		}
	}
	
	public void averageByTypeOfTask() {
		if(Float.isNaN(mEventsTasks[1]))
			if(Float.isNaN(mEventsTasks[2])) mKeyboardTasks = Float.NaN;
			else mKeyboardTasks = mEventsTasks[2];
		else if(Float.isNaN(mEventsTasks[2])) mKeyboardTasks = mEventsTasks[1];
		else mKeyboardTasks = (mEventsTasks[1] + mEventsTasks[3]) / 2;
		
		float aux = 0;
		float counter = 0;
		if(!Float.isNaN(mEventsTasks[0])) { aux += mEventsTasks[0]; counter++; }
		if(!Float.isNaN(mEventsTasks[2])) { aux += mEventsTasks[2]; counter++; }
		if(!Float.isNaN(mEventsTasks[6])) { aux += mEventsTasks[6]; counter++; }
		if(counter>0) mCaretTasks = aux / counter;
		else mCaretTasks = Float.NaN;
		
		if(Float.isNaN(mEventsTasks[4]))
			if(Float.isNaN(mEventsTasks[5])) mClipboardTasks = Float.NaN;
			else mClipboardTasks = mEventsTasks[5];
		else if(Float.isNaN(mEventsTasks[5])) mClipboardTasks = mEventsTasks[4];
		else mClipboardTasks = (mEventsTasks[4] + mEventsTasks[5]) / 2;
	}
	
	public float getEvents() { return mEvents; }
	public float getCaretMovements() { return mCaretMovements; }
	public float getSelections() { return mSelections; }
	public void setEvents(float events) { mEvents = events; }
	public void setCaretMovements(float caret) { mCaretMovements = caret; }
	public void setSelections(float selections) { mSelections = selections; }

	static public String getHeader()
	{
		return "Events\tCaretMovements\tSelections";
	}
	
	public String toString()
	{
		if(mEvents == 0) return " \t \t "; //no trials
		
		DecimalFormat df = new DecimalFormat("####.000");
		return df.format(this.getEvents()) + "\t" + df.format(this.getCaretMovements()) + "\t" + df.format(this.getSelections());
	}
	
}
