package pt.lasige.demo.inputmethod.metrics.data;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class UncorrectedErrorRate extends Metric {
    public UncorrectedErrorRate() {}

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getUncorrectedErrorRate();
    }
}