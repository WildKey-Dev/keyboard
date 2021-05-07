package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.logger.Logger;
import uk.openlab.inputmethod.metrics.textentry.datastructures.Input;
import uk.openlab.inputmethod.metrics.textentry.datastructures.Tuple;

public class CorrectionActionCount {
    public CorrectionActionCount() {
        super();
    }

    public int execute(Logger logger){
        int count = 0;
        for (Tuple t: logger.getActions()){
            if((int) t.t1 == Input.ACTION_DELETE || (int) t.t1 == Input.ACTION_SUBSTITUTION)
                count++;
        }
        return count;
    }
}
