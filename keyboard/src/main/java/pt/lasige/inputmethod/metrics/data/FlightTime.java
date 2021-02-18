package pt.lasige.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.inputmethod.logger.Logger;
import pt.lasige.inputmethod.logger.LoggerController;

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