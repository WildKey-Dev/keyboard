package pt.lasige.inputmethod.metrics.data;

import pt.lasige.inputmethod.logger.Logger;

public class VoiceInput extends Metric {
    public VoiceInput() {
        super();
    }

    public int execute(Logger logger){
        return logger.getVoiceInput();
    }
}
