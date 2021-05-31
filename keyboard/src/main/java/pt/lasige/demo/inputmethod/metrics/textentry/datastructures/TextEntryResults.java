package pt.lasige.demo.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class TextEntryResults implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Effectiveness results
	protected EffectivenessResults mEffectivenessResults;
	
	// Efficiency results
	protected EfficiencyResults mEfficiencyResults;
	
	// Character-level results
	protected ISCharacterLevelResults mCharacterLevelResults;
	
	// Spellchecker results
	protected SpellCheckerResults mSpellCheckerResults;
	
	// Intent results
	protected IntentResults mIntentResults;
	
	// Word Completion results
	protected WordCompletionResults mWordCompletionResults;
	
	public boolean COMPUTE_TIME = true;
	public boolean COMPUTE_ERRORS = true;
	
	public TextEntryResults()
	{
		mEffectivenessResults = new EffectivenessResults();
		mEfficiencyResults = new EfficiencyResults();
		mCharacterLevelResults = new ISCharacterLevelResults();
		mSpellCheckerResults = new SpellCheckerResults();
		mIntentResults = new IntentResults();
		mWordCompletionResults = new WordCompletionResults();
	}
		
	public WordCompletionResults getWordCompletionResults() {
		return mWordCompletionResults;
	}
	
	public void setWordCompletionResults(WordCompletionResults results) {
		this.mWordCompletionResults = results;
	}
	
	public SpellCheckerResults getSpellCheckerResults() {
		return mSpellCheckerResults;
	}

	public void setSpellCheckerResults(SpellCheckerResults spellCheckerResults) {
		this.mSpellCheckerResults = spellCheckerResults;
	}

	public TextEntryResults(EffectivenessResults effectivenessResults)
	{
		this.mEffectivenessResults = effectivenessResults;
	}
	
	public EffectivenessResults getEffectivenessResults() {
		return mEffectivenessResults;
	}
	
	public void setEffectivenessResults(EffectivenessResults effectivenessResults) {
		this.mEffectivenessResults = effectivenessResults;
	}

	public EfficiencyResults getEfficiencyResults() {
		return mEfficiencyResults;
	}

	public void setmEfficiencyResults(EfficiencyResults efficiencyResults) {
		this.mEfficiencyResults = efficiencyResults;
	}

	public ISCharacterLevelResults getCharacterLevelResults() {
		return mCharacterLevelResults;
	}

	public void setCharacterLevelResults(
			ISCharacterLevelResults characterLevelResults) {
		this.mCharacterLevelResults = characterLevelResults;
	}
	
	public IntentResults getIntentResults() { return mIntentResults; }
	public void setIntentResults(IntentResults results) { mIntentResults = results; }
	
}
