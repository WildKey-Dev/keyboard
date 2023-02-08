package pt.lasige.ideafast.study.inputmethod.logger.data;

import java.util.ArrayList;

import pt.lasige.ideafast.study.inputmethod.latin.SuggestedWords;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.Tuple;

public class Buffers {
    private ArrayList<Long> flightTimeBuffer, holdTimeBuffer, timePerWordBuffer;
    private ArrayList<Tuple> touchMajorMinorBuffer, touchOffsetBuffer, motionBuffer, keysBuffer, actionsBuffer;
    private ArrayList<String> spellChecker;
    private ArrayList<SuggestedWords> suggestions;

    public Buffers() {
        flightTimeBuffer = new ArrayList<>();
        holdTimeBuffer = new ArrayList<>();
        timePerWordBuffer = new ArrayList<>();
        touchMajorMinorBuffer = new ArrayList<>();
        touchOffsetBuffer = new ArrayList<>();
        motionBuffer = new ArrayList<>();
        keysBuffer = new ArrayList<>();
        actionsBuffer = new ArrayList<>();
        spellChecker = new ArrayList<>();
        suggestions = new ArrayList<>();
    }

    public ArrayList<Long> getFlightTimeBuffer() {
        return flightTimeBuffer;
    }

    public void setFlightTimeBuffer(ArrayList<Long> flightTimeBuffer) {
        this.flightTimeBuffer = flightTimeBuffer;
    }

    public ArrayList<Long> getHoldTimeBuffer() {
        return holdTimeBuffer;
    }

    public void setHoldTimeBuffer(ArrayList<Long> holdTimeBuffer) {
        this.holdTimeBuffer = holdTimeBuffer;
    }

    public ArrayList<Long> getTimePerWordBuffer() {
        return timePerWordBuffer;
    }

    public void setTimePerWordBuffer(ArrayList<Long> timePerWordBuffer) {
        this.timePerWordBuffer = timePerWordBuffer;
    }

    public ArrayList<Tuple> getTouchMajorMinorBuffer() {
        return touchMajorMinorBuffer;
    }

    public void setTouchMajorMinorBuffer(ArrayList<Tuple> touchMajorMinorBuffer) {
        this.touchMajorMinorBuffer = touchMajorMinorBuffer;
    }

    public ArrayList<Tuple> getTouchOffsetBuffer() {
        return touchOffsetBuffer;
    }

    public void setTouchOffsetBuffer(ArrayList<Tuple> touchOffsetBuffer) {
        this.touchOffsetBuffer = touchOffsetBuffer;
    }

    public ArrayList<Tuple> getMotionBuffer() {
        return motionBuffer;
    }

    public void setMotionBuffer(ArrayList<Tuple> motionBuffer) {
        this.motionBuffer = motionBuffer;
    }

    public ArrayList<Tuple> getKeysBuffer() {
        return keysBuffer;
    }

    public void setKeysBuffer(ArrayList<Tuple> keysBuffer) {
        this.keysBuffer = keysBuffer;
    }

    public ArrayList<Tuple> getActionsBuffer() {
        return actionsBuffer;
    }

    public void setActionsBuffer(ArrayList<Tuple> actionsBuffer) {
        this.actionsBuffer = actionsBuffer;
    }

    public ArrayList<String> getSpellChecker() {
        return spellChecker;
    }

    public void setSpellChecker(ArrayList<String> spellChecker) {
        this.spellChecker = spellChecker;
    }

    public ArrayList<SuggestedWords> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(ArrayList<SuggestedWords> suggestions) {
        this.suggestions = suggestions;
    }
}
