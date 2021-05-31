package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.logger.Logger;

public class AutoCorrections extends Metric {
    public AutoCorrections() {
        super();
    }

    public int execute(Logger logger){
        return logger.getAutoCorrection();
    }
}
