package pt.lasige.cns.study.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.cns.study.inputmethod.logger.Logger;

public class TimePerWord extends Metric {
    public TimePerWord() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        return logger.getTimePerWord();
    }
}
