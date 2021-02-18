package pt.lasige.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial.INPUT_ACTION_TYPE;

public class EditAction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Date Timestamp;
	public String Character;
	
	public EditAction(String character, Date timestamp) 
	{
		Character = character;
		Timestamp = timestamp;
	}
	
}
