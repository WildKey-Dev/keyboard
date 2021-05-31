package pt.lasige.demo.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.demo.inputmethod.logger.Logger;

public class HoldTimeDeviations extends Metric {
    public HoldTimeDeviations() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        return logger.getHoldTimeBuffer();
    }
}