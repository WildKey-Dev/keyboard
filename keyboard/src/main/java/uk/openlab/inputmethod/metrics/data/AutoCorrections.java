package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.logger.Logger;

public class AutoCorrections extends Metric {
    public AutoCorrections() {
        super();
    }

    public int execute(Logger logger){
        return logger.getAutoCorrection();
    }
}
