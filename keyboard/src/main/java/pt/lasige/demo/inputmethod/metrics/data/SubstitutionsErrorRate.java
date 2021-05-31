package pt.lasige.demo.inputmethod.metrics.data;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class SubstitutionsErrorRate extends Metric {
    public SubstitutionsErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getCharacterLevelResults().getSubstitutionErrRate();
    }
}