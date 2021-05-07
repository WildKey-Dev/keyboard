package uk.openlab.inputmethod.metrics.data;
import uk.openlab.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class SubstitutionsErrorRate extends Metric {
    public SubstitutionsErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getCharacterLevelResults().getSubstitutionErrRate();
    }
}