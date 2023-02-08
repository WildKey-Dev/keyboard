package pt.lasige.ideafast.study.inputmethod.study.questionnaire.data;

public class Tap {
    public enum Side {
        LEFT,
        RIGHT,
        OUT
    }
    long downTimestamp;
    long upTimestamp;
    Point downAbsolute;
    Point downRelative;
    Point upAbsolute;
    Point upRelative;
    Side side;

    public long getDownTimestamp() {
        return downTimestamp;
    }

    public void setDownTimestamp(long downTimestamp) {
        this.downTimestamp = downTimestamp;
    }

    public long getUpTimestamp() {
        return upTimestamp;
    }

    public void setUpTimestamp(long upTimestamp) {
        this.upTimestamp = upTimestamp;
    }

    public Point getDownAbsolute() {
        return downAbsolute;
    }

    public void setDownAbsolute(Point downAbsolute) {
        this.downAbsolute = downAbsolute;
    }

    public Point getDownRelative() {
        return downRelative;
    }

    public void setDownRelative(Point downRelative) {
        this.downRelative = downRelative;
    }

    public Point getUpAbsolute() {
        return upAbsolute;
    }

    public void setUpAbsolute(Point upAbsolute) {
        this.upAbsolute = upAbsolute;
    }

    public Point getUpRelative() {
        return upRelative;
    }

    public void setUpRelative(Point upRelative) {
        this.upRelative = upRelative;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    @Override
    public String toString() {
        return "Tap{" +
                "downTimestamp=" + downTimestamp +
                ", upTimestamp=" + upTimestamp +
                ", downAbsolute=" + downAbsolute +
                ", downRelative=" + downRelative +
                ", upAbsolute=" + upAbsolute +
                ", upRelative=" + upRelative +
                ", side=" + side +
                '}';
    }
}
