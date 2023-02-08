package pt.lasige.ideafast.study.inputmethod.metrics.data;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;

public class WrittenSpecialChars extends Metric {
    public WrittenSpecialChars() {
        super();
    }

    public int execute(Logger logger){
        return logger.getSpecialChars();
    }
}
