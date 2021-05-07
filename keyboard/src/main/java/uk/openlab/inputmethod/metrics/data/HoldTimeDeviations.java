package uk.openlab.inputmethod.metrics.data;

import java.util.ArrayList;

import uk.openlab.inputmethod.logger.Logger;

public class HoldTimeDeviations extends Metric {
    public HoldTimeDeviations() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        return logger.getHoldTimeBuffer();
    }
}