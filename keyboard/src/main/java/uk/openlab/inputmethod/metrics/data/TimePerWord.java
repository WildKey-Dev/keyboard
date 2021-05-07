package uk.openlab.inputmethod.metrics.data;

import java.util.ArrayList;

import uk.openlab.inputmethod.logger.Logger;

public class TimePerWord extends Metric {
    public TimePerWord() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        return logger.getTimePerWord();
    }
}
