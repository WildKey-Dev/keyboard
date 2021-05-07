package uk.openlab.inputmethod.metrics.data;

import java.util.ArrayList;

import uk.openlab.inputmethod.logger.Logger;
import uk.openlab.inputmethod.metrics.textentry.datastructures.Tuple;

public class TouchMajorMinor extends Metric {
    public TouchMajorMinor() {
        super();
    }

    public ArrayList<Tuple> execute(Logger logger){
        return logger.getTouchMajorMinor();
    }
}