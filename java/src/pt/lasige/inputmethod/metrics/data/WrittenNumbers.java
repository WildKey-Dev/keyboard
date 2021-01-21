package pt.lasige.inputmethod.metrics.data;

import pt.lasige.inputmethod.logger.Logger;

public class WrittenNumbers extends Metric {
    public WrittenNumbers() {
        super();
    }

    public int execute(Logger logger){
        return logger.getNumbers();
    }
}
