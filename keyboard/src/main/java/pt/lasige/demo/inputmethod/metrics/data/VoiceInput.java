package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.logger.Logger;

public class VoiceInput extends Metric {
    public VoiceInput() {
        super();
    }

    public int execute(Logger logger){
        return logger.getVoiceInput();
    }
}
