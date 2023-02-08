package pt.lasige.ideafast.study.inputmethod.metrics.data;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;

public class VoiceInput extends Metric {
    public VoiceInput() {
        super();
    }

    public int execute(Logger logger){
        return logger.getVoiceInput();
    }
}
