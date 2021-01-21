package pt.lasige.inputmethod.metrics.data;

import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class CorrectedErrorRate extends Metric {

    public CorrectedErrorRate() {}

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getCorrectedErrorRate();
    }


}