package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.logger.Logger;

public class SelectedSuggestions extends Metric {
    public SelectedSuggestions() {
        super();
    }

    public int execute(Logger logger){
        return logger.getSuggestionsSelected();
    }
}
