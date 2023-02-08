package pt.lasige.ideafast.study.inputmethod.metrics.data;

import pt.lasige.ideafast.study.inputmethod.logger.Logger;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.Input;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.Tuple;

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
