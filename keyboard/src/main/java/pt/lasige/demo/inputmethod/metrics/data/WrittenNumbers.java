package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.logger.Logger;

public class WrittenNumbers extends Metric {
    public WrittenNumbers() {
        super();
    }

    public int execute(Logger logger){
        return logger.getNumbers();
    }
}
