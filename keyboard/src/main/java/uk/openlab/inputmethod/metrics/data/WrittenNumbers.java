package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.logger.Logger;

public class WrittenNumbers extends Metric {
    public WrittenNumbers() {
        super();
    }

    public int execute(Logger logger){
        return logger.getNumbers();
    }
}
