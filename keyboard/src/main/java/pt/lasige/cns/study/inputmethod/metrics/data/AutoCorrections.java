package pt.lasige.cns.study.inputmethod.metrics.data;

import pt.lasige.cns.study.inputmethod.logger.Logger;

public class AutoCorrections extends Metric {
    public AutoCorrections() {
        super();
    }

    public int execute(Logger logger){
        return logger.getAutoCorrection();
    }
}
