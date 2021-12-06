package pt.lasige.cns.study.inputmethod.metrics.data;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class UncorrectedErrorRate extends Metric {
    public UncorrectedErrorRate() {}

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getEffectivenessResults().getUncorrectedErrorRate();
    }
}