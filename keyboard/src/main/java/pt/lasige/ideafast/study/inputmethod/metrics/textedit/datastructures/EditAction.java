package pt.lasige.ideafast.study.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;
import java.util.Date;

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
