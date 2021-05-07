package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.logger.Logger;

public class WrittenSpecialChars extends Metric {
    public WrittenSpecialChars() {
        super();
    }

    public int execute(Logger logger){
        return logger.getSpecialChars();
    }
}
