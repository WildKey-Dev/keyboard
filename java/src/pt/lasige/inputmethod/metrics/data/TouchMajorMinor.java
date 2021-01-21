package pt.lasige.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.inputmethod.logger.Logger;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Tuple;

public class TouchMajorMinor extends Metric {
    public TouchMajorMinor() {
        super();
    }

    public ArrayList<Tuple> execute(Logger logger){
        return logger.getTouchMajorMinor();
    }
}