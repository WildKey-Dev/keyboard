package pt.lasige.ideafast.study.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;

public class Pressure extends Metric {
    public Pressure() {
        super();
    }
    public ArrayList<Float> execute(Logger logger){
        return logger.getPressure();
    }
}