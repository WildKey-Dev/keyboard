package uk.openlab.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class InputGesture implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int mID;
	protected String mIntended;
	protected ArrayList<InputAction> mActions;
	
	public InputGesture(int id, String intended)
	{
		mID = id;
		mIntended = intended;
		mActions = new ArrayList<InputAction>();
	}
	
	public void addInputAction(InputAction action)
	{
		mActions.add(action);
	}
	
	public ArrayList<InputAction> getInputActions()
	{
		return mActions;
	}
	
	public int getID()
	{
		return mID;
	}
	
	public String getIntended()
	{
		return mIntended;
	}
}
