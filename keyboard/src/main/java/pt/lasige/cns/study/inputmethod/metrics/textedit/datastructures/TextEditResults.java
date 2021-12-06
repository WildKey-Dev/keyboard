package pt.lasige.cns.study.inputmethod.metrics.textedit.datastructures;

import java.io.Serializable;

public class TextEditResults implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Efficiency results
	protected EfficiencyResults mEfficiencyResults;
	protected EffectivenessResults mEffectivenessResults;
	protected EventsResults mEventsResults;
	
	public TextEditResults()
	{
		mEfficiencyResults = new EfficiencyResults();
		mEffectivenessResults = new EffectivenessResults();
		mEventsResults = new EventsResults();
	}
	
	public EfficiencyResults getEfficiencyResults() {
		return mEfficiencyResults;
	}

	public void setEfficiencyResults(EfficiencyResults efficiencyResults) {
		this.mEfficiencyResults = efficiencyResults;
	}
	
	public EffectivenessResults getEffectivenessResults() {
		return mEffectivenessResults;
	}
	
	public void setEffectivenessResults(EffectivenessResults effectivenessResults) {
		this.mEffectivenessResults = effectivenessResults;
	}
	
	public EventsResults getEventsResults() {
		return mEventsResults;
	}
	
	public void setEventsResults(EventsResults eventsResults) {
		mEventsResults = eventsResults;
	}
}
