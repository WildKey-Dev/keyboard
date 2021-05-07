package uk.openlab.inputmethod.metrics.data;

import java.util.ArrayList;

import uk.openlab.inputmethod.logger.Logger;

public class TimeOutsidePhrase {
    public TimeOutsidePhrase() {
        super();
    }

    public ArrayList<Long> execute(Logger logger){

        ArrayList<Long> res = logger.getTimeOutsideCurrentPhrase();

        if(logger.getOutPhraseTS() != -1){
            res.add(System.currentTimeMillis() - logger.getOutPhraseTS());
        }

        return res;
    }
}
