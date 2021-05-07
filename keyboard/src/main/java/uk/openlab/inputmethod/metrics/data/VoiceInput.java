package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.logger.Logger;

public class VoiceInput extends Metric {
    public VoiceInput() {
        super();
    }

    public int execute(Logger logger){
        return logger.getVoiceInput();
    }
}
