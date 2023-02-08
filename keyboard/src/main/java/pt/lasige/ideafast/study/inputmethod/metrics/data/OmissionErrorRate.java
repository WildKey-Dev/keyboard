package pt.lasige.ideafast.study.inputmethod.metrics.data;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class OmissionErrorRate extends Metric {
    public OmissionErrorRate() {
        super();
    }

    public float execute(TextEntryTrial trial){
        return trial.getTrialResults().getCharacterLevelResults().getOmissionErrRate();
    }
}