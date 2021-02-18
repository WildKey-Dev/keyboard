package pt.lasige.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.inputmethod.logger.Logger;
import pt.lasige.inputmethod.logger.LoggerController;

public class HoldTimeDeviations extends Metric {
    public HoldTimeDeviations() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        return logger.getHoldTimeBuffer();
    }
}