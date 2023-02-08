package pt.lasige.ideafast.study.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;

public class FlightTime extends Metric {
    public FlightTime() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){
        ArrayList<Long> flightTimeBuffer = logger.getFlightTimeBuffer();
        ArrayList<Long> values = new ArrayList<>();

        for (int i = 1; i < flightTimeBuffer.size(); i++ ){
            values.add(flightTimeBuffer.get(i) - flightTimeBuffer.get(i-1));
        }

        return values;
    }
}