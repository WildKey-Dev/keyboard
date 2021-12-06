package pt.lasige.cns.study.inputmethod.metrics.data;

import pt.lasige.cns.study.inputmethod.logger.Logger;

public class WrittenNumbers extends Metric {
    public WrittenNumbers() {
        super();
    }

    public int execute(Logger logger){
        return logger.getNumbers();
    }
}
