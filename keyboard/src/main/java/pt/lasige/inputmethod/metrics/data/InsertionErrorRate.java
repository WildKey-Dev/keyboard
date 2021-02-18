package pt.lasige.inputmethod.metrics.data;

import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class InsertionErrorRate extends Metric {
    public InsertionErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getCharacterLevelResults().getInsertionErrRate();
    }
}