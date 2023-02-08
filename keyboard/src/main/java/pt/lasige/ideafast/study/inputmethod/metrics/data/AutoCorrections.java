package pt.lasige.ideafast.study.inputmethod.metrics.data;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;

public class AutoCorrections extends Metric {
    public AutoCorrections() {
        super();
    }

    public int execute(Logger logger){
        return logger.getAutoCorrection();
    }
}
