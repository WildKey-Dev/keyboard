package pt.lasige.cns.study.inputmethod.metrics.data;

import pt.lasige.cns.study.inputmethod.logger.Logger;

public class VoiceInput extends Metric {
    public VoiceInput() {
        super();
    }

    public int execute(Logger logger){
        return logger.getVoiceInput();
    }
}
