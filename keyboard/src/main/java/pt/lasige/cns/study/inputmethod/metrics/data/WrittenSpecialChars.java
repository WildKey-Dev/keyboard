package pt.lasige.cns.study.inputmethod.metrics.data;

import pt.lasige.cns.study.inputmethod.logger.Logger;

public class WrittenSpecialChars extends Metric {
    public WrittenSpecialChars() {
        super();
    }

    public int execute(Logger logger){
        return logger.getSpecialChars();
    }
}
