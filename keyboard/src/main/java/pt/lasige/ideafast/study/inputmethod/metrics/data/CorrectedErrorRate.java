package pt.lasige.ideafast.study.inputmethod.metrics.data;

import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class CorrectedErrorRate extends Metric {

    public CorrectedErrorRate() {}

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getCorrectedErrorRate();
    }


}