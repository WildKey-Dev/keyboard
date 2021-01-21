package pt.lasige.inputmethod.logger.data;

public class CursorChange {
    int oldSelStart, oldSelEnd, newSelStart, newSelEnd;
    String input = "";

    public CursorChange() {
    }

    public CursorChange(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd) {
        this.oldSelStart = oldSelStart;
        this.oldSelEnd = oldSelEnd;
        this.newSelStart = newSelStart;
        this.newSelEnd = newSelEnd;
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
}
