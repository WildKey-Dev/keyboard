package pt.lasige.demo.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.demo.inputmethod.logger.Logger;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.Tuple;

public class TouchMajorMinor extends Metric {
    public TouchMajorMinor() {
        super();
    }

    public ArrayList<Tuple> execute(Logger logger){
        return logger.getTouchMajorMinor();
    }
}