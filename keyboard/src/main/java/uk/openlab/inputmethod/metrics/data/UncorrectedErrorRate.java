package uk.openlab.inputmethod.metrics.data;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class UncorrectedErrorRate extends Metric {
    public UncorrectedErrorRate() {}

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getUncorrectedErrorRate();
    }
}