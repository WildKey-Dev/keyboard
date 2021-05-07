package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class TotalChangedCharacters  extends Metric {
    public TotalChangedCharacters() {
        super();
    }

    public int execute(TextEntryTrial trial){
        return trial.getInputStream().replace("<<", "<").length();
    }
}
