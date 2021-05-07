package uk.openlab.inputmethod.metrics.data;

import java.util.ArrayList;

import uk.openlab.inputmethod.logger.Logger;
import uk.openlab.inputmethod.metrics.textentry.datastructures.Tuple;

public class TouchOffsetEntered extends Metric {
    public TouchOffsetEntered() {
        super();
    }

    public ArrayList<Tuple> execute(Logger logger){
        return logger.getTouchOffset();
    }
}