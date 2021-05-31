package pt.lasige.demo.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.demo.inputmethod.logger.Logger;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.Tuple;

public class TouchOffsetEntered extends Metric {
    public TouchOffsetEntered() {
        super();
    }

    public ArrayList<Tuple> execute(Logger logger){
        return logger.getTouchOffset();
    }
}