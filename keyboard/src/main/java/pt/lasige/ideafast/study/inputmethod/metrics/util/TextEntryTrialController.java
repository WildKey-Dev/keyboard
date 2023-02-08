package pt.lasige.ideafast.study.inputmethod.metrics.util;

import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.measures.ComputeEffectiveness;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.measures.ComputeISCharacterLevel;

public class TextEntryTrialController {

    private TextEntryTrial trial;

    public TextEntryTrialController() {
        trial = new TextEntryTrial();
    }

    public TextEntryTrial getTrial() {
        return trial;
    }

    public void startTrial(String required, String inputStream, String transcribe){

        trial = new TextEntryTrial();
        trial.setRequiredSentence(required);
        trial.setInputStream(inputStream);
        trial.setTranscribedSentence(transcribe);
    }

    public void computeResults(boolean computeISCharacterLevelResults){
        trial = ComputeEffectiveness.computeTrialEffectivenessResults(trial);
        if(computeISCharacterLevelResults)
            trial = ComputeISCharacterLevel.computeISCharacterLevelResults(trial);
    }

    public void stopTrial(){
        trial = null;
    }
}

