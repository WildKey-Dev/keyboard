package pt.lasige.ideafast.study.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;

public class TimePerWord extends Metric {
    public TimePerWord() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        return logger.getTimePerWord();
    }
}
