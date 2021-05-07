package uk.openlab.inputmethod.logger.data;

import java.util.ArrayList;

public class CompletedTask {
    ArrayList<Object> phrases;
    boolean finished;

    public CompletedTask() { }

    public ArrayList<Object> getPhrases() {
        return phrases;
    }

    public void setPhrases(ArrayList<Object> phrases) {
        this.phrases = phrases;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
