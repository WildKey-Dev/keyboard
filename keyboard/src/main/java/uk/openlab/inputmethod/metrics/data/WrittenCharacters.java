package uk.openlab.inputmethod.metrics.data;

import uk.openlab.inputmethod.logger.Logger;
import uk.openlab.inputmethod.metrics.textentry.datastructures.Input;
import uk.openlab.inputmethod.metrics.textentry.datastructures.Tuple;

public class WrittenCharacters extends Metric {
    public WrittenCharacters() {
        super();
    }

    public int execute(Logger logger){
        int count = 0;
        for (Tuple t: logger.getActions()){
            if((int) t.t1 == Input.ACTION_INSERT)
                count++;
        }
        return count;
    }
}
