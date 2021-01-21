package pt.lasige.inputmethod.metrics.data;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class TotalErrorRate extends Metric {
    public TotalErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getTotalErrorRate();
    }
}