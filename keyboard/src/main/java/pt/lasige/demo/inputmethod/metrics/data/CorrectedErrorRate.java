package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class CorrectedErrorRate extends Metric {

    public CorrectedErrorRate() {}

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getCorrectedErrorRate();
    }


}