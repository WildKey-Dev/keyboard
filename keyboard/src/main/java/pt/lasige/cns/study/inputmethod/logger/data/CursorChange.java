package pt.lasige.cns.study.inputmethod.logger.data;

public class CursorChange {
    int oldSelStart, oldSelEnd, newSelStart, newSelEnd, editTextLen;
    long timeStamp;
    String input = "";

    public CursorChange() {
    }

    public CursorChange(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int editTextLen, long timeStamp) {
        this.oldSelStart = oldSelStart;
        this.oldSelEnd = oldSelEnd;
        this.newSelStart = newSelStart;
        this.newSelEnd = newSelEnd;
        this.timeStamp = timeStamp;
        this.editTextLen = editTextLen;
    }

    public int getOldSelStart() {
        return oldSelStart;
    }

    public void setOldSelStart(int oldSelStart) {
        this.oldSelStart = oldSelStart;
    }

    public int getOldSelEnd() {
        return oldSelEnd;
    }

    public void setOldSelEnd(int oldSelEnd) {
        this.oldSelEnd = oldSelEnd;
    }

    public int getNewSelStart() {
        return newSelStart;
    }

    public void setNewSelStart(int newSelStart) {
        this.newSelStart = newSelStart;
    }

    public int getNewSelEnd() {
        return newSelEnd;
    }

    public void setNewSelEnd(int newSelEnd) {
        this.newSelEnd = newSelEnd;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void addToInput(String input) {
        this.input = this.input + input;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getEditTextLen() {
        return editTextLen;
    }

    public void setEditTextLen(int editTextLen) {
        this.editTextLen = editTextLen;
    }

    @Override
    public String toString() {
        return "CursorChange{" +
                "oldSelStart=" + oldSelStart +
                ", oldSelEnd=" + oldSelEnd +
                ", newSelStart=" + newSelStart +
                ", newSelEnd=" + newSelEnd +
                ", editTextLen=" + editTextLen +
                ", timeStamp=" + timeStamp +
                ", input='" + input + '\'' +
                '}';
    }
}
