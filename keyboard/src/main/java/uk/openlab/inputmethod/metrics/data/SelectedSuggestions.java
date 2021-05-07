package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.logger.Logger;

public class SelectedSuggestions extends Metric {
    public SelectedSuggestions() {
        super();
    }

    public int execute(Logger logger){
        return logger.getSuggestionsSelected();
    }
}
