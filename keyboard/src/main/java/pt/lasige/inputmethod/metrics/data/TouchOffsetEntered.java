package pt.lasige.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.inputmethod.logger.Logger;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Tuple;

public class TouchOffsetEntered extends Metric {
    public TouchOffsetEntered() {
        super();
    }

    public ArrayList<Tuple> execute(Logger logger){
        return logger.getTouchOffset();
    }
}