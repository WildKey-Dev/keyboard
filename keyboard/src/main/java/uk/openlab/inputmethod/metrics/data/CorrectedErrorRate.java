package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class CorrectedErrorRate extends Metric {

    public CorrectedErrorRate() {}

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getCorrectedErrorRate();
    }


}