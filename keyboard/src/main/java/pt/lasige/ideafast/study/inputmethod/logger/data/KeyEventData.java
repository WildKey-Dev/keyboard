package pt.lasige.ideafast.study.inputmethod.logger.data;

public class KeyEventData {
    private int code;
    private int altCode;
    private String printableCode;
    private int x;
    private int y;

    public KeyEventData(int code, int altCode, String printableCode, int x, int y) {
        this.code = code;
        this.altCode = altCode;
        this.printableCode = printableCode;
        this.x = x;
        this.y = y;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getAltCode() {
        return altCode;
    }

    public void setAltCode(int altCode) {
        this.altCode = altCode;
    }

    public String getPrintableCode() {
        return printableCode;
    }

    public void setPrintableCode(String printableCode) {
        this.printableCode = printableCode;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
