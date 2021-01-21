package pt.lasige.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial.INPUT_ACTION_TYPE;

public class InputAction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Date Timestamp;
	public String Character;
	public ArrayList<String> Intended = null;
	public ArrayList<String> Suggestions = null;
	public boolean willAutoCorrect = false;
	public INPUT_ACTION_TYPE Type;
	public int X;
	public int Y;
	public int PointerID;
	public boolean valid = true;
	
	public InputAction(INPUT_ACTION_TYPE type, String character, Date timestamp) 
	{
		Type = type;
		Character = character;
		Timestamp = timestamp;
		Intended = new ArrayList<String>();
		Suggestions = new ArrayList<String>();
	}
	
	public InputAction(INPUT_ACTION_TYPE type, String character, Date timestamp, boolean isValid) 
	{
		valid = isValid;
		Type = type;
		Character = character;
		Timestamp = timestamp;
		Intended = new ArrayList<String>();
		Suggestions = new ArrayList<String>();
	}
	
	public InputAction(INPUT_ACTION_TYPE type, String character, boolean isValid) 
	{
		valid = isValid;
		Type = type;
		Character = character;
		Timestamp = null;
		Intended = new ArrayList<String>();
		Suggestions = new ArrayList<String>();
	}
	
	public InputAction(INPUT_ACTION_TYPE type, String enteredChar, String intendedChar, Date timestamp, int x, int y)
	{
		Type = type;
		Character = enteredChar;
		Intended = new ArrayList<String>();
		Intended.add(intendedChar);
		Timestamp = timestamp;
		X = x;
		Y = y;
		Suggestions = new ArrayList<String>();
	}
	
	public InputAction() 
	{
		Intended = new ArrayList<String>();
		Suggestions = new ArrayList<String>();
	}
	
}
