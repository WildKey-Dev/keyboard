package pt.lasige.ideafast.study.inputmethod.metrics.data;

import java.util.ArrayList;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.Input;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.Tuple;

public class ErrorCorrectionAttempts extends Metric {
    public ErrorCorrectionAttempts() {
        super();
    }

    public int execute(Logger logger) {

        ArrayList<Tuple> actions = logger.getActions();
        boolean lastInput = false;
        int attempts = 0;
        for (Tuple action: actions) {
            if((int) action.t1 == Input.ACTION_DELETE) {
                if(lastInput)
                    attempts++;
                lastInput = false;
            }else{
                lastInput = true;
            }
        }
        return attempts;
    }
}
