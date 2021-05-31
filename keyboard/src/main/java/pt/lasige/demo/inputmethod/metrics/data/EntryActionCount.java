package pt.lasige.demo.inputmethod.metrics.data;

import pt.lasige.demo.inputmethod.logger.Logger;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.Input;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.Tuple;

public class EntryActionCount {
    public EntryActionCount() {
        super();
    }

    public int execute(Logger logger){
        int count = 0;
        for (Tuple t: logger.getActions()){
            if((int) t.t1 == Input.ACTION_INSERT || (int) t.t1 == Input.ACTION_SPACE || (int) t.t1 == Input.ACTION_SUBSTITUTION)
                count++;
        }
        return count;
    }
}