package pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class SuggestionEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String mRequiredWords;
	protected String mTranscribedWords;
	protected ArrayList<String> mSuggestions;
	
	public SuggestionEntry(String requiredWords, String transcribedWords, ArrayList<String> suggestions)
	{
		this.mRequiredWords = requiredWords;
		this.mTranscribedWords = transcribedWords;
		this.mSuggestions = suggestions;
	}

	public String getTranscribedWord() {
		return this.mTranscribedWords;
	}

	public void setTranscribedWord(String transcribedWord) {
		this.mTranscribedWords = transcribedWord;
	}

	public String getRequiredWord() {
		return mRequiredWords;
	}

	public void setRequiredWord(String requiredWord) {
		this.mRequiredWords = requiredWord;
	}

	public ArrayList<String> getSuggestions() {
		return mSuggestions;
	}

	public void setSuggestions(ArrayList<String> suggestions) {
		this.mSuggestions = suggestions;
	}
	
	
}
