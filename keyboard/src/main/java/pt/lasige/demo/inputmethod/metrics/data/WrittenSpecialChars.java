package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.logger.Logger;

public class WrittenSpecialChars extends Metric {
    public WrittenSpecialChars() {
        super();
    }

    public int execute(Logger logger){
        return logger.getSpecialChars();
    }
}
