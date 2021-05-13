package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class InsertionErrorRate extends Metric {
    public InsertionErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getCharacterLevelResults().getInsertionErrRate();
    }
}