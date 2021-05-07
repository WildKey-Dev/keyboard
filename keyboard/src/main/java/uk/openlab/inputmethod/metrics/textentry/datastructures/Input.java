package uk.openlab.inputmethod.metrics.textentry.datastructures;

import java.util.ArrayList;

public class Input {
    public final static int ACTION_DELETE = 0;
    public final static int ACTION_INSERT = 1;
    public final static int ACTION_SPACE = 2;
    public final static int ACTION_SUGGESTION = 3;
    public final static int ACTION_SUBSTITUTION = 4;

    ArrayList<Tuple> actions;
    String transcribe;

    public Input(ArrayList<Tuple> actions, String transcribe) {
        this.actions = actions;
        this.transcribe = transcribe;
    }

    public ArrayList<Tuple> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Tuple> actions) {
        this.actions = actions;
    }

    public String getTranscribe() {
        return transcribe;
    }

    public void setTranscribe(String transcribe) {
        this.transcribe = transcribe;
    }
}
