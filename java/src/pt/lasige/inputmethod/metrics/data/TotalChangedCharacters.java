package pt.lasige.inputmethod.metrics.data;

import pt.lasige.inputmethod.logger.Logger;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Input;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Tuple;

public class TotalChangedCharacters  extends Metric {
    public TotalChangedCharacters() {
        super();
    }

    public int execute(TextEntryTrial trial){
        return trial.getInputStream().replace("<<", "<").length();
    }
}
