package pt.lasige.inputmethod.metrics.data;

import pt.lasige.inputmethod.logger.Logger;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Input;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Tuple;

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
