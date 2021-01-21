package pt.lasige.inputmethod.metrics.data;
import pt.lasige.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class OmissionErrorRate extends Metric {
    public OmissionErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getCharacterLevelResults().getOmissionErrRate();
    }
}