package pt.lasige.demo.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.demo.inputmethod.logger.Logger;

public class TimePerWord extends Metric {
    public TimePerWord() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        return logger.getTimePerWord();
    }
}
