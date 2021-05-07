package uk.openlab.inputmethod.metrics.data;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class TotalErrorRate extends Metric {
    public TotalErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getTotalErrorRate();
    }
}