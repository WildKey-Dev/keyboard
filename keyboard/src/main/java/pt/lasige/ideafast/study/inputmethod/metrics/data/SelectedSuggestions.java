package pt.lasige.ideafast.study.inputmethod.metrics.data;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;

public class SelectedSuggestions extends Metric {
    public SelectedSuggestions() {
        super();
    }

    public int execute(Logger logger){
        return logger.getSuggestionsSelected();
    }
}
