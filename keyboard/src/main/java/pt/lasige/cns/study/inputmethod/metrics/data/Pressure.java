package pt.lasige.cns.study.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.cns.study.inputmethod.logger.Logger;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.Tuple;

public class Pressure extends Metric {
    public Pressure() {
        super();
    }
    public ArrayList<Float> execute(Logger logger){
        return logger.getPressure();
    }
}