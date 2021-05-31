package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class TotalChangedCharacters  extends Metric {
    public TotalChangedCharacters() {
        super();
    }

    public int execute(TextEntryTrial trial){
        return trial.getInputStream().replace("<<", "<").length();
    }
}
