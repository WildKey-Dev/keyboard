package pt.lasige.ideafast.study.inputmethod.logger.data;

public class MotionEventData {
    private int actionCode;
    private String action;
    private int actionIndex;
    private int actionMasked;
    private float axisXValue;
    private float axisYValue;
    private long downTime;
    private float pressure;
    private float xPrecision;
    private float yPrecision;
    private float x;
    private float y;
    private float rawX;
    private float rawY;
    private float size;
    private float toolMajor;
    private float toolMinor;
    private float touchMajor;
    private float touchMinor;

    public MotionEventData(int actionCode, String action, int actionIndex, int actionMasked, float axisXValue, float axisYValue, long downTime, float pressure, float xPrecision, float yPrecision, float x, float y, float rawX, float rawY, float size, float toolMajor, float toolMinor, float touchMajor, float touchMinor) {
        this.actionCode = actionCode;
        this.action = action;
        this.actionIndex = actionIndex;
        this.actionMasked = actionMasked;
        this.axisXValue = axisXValue;
        this.axisYValue = axisYValue;
        this.downTime = downTime;
        this.pressure = pressure;
        this.xPrecision = xPrecision;
        this.yPrecision = yPrecision;
        this.x = x;
        this.y = y;
        this.rawX = rawX;
        this.rawY = rawY;
        this.size = size;
        this.toolMajor = toolMajor;
        this.toolMinor = toolMinor;
        this.touchMajor = touchMajor;
        this.touchMinor = touchMinor;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(int actionIndex) {
        this.actionIndex = actionIndex;
    }

    public int getActionMasked() {
        return actionMasked;
    }

    public void setActionMasked(int actionMasked) {
        this.actionMasked = actionMasked;
    }

    public float getAxisXValue() {
        return axisXValue;
    }

    public void setAxisXValue(float axisXValue) {
        this.axisXValue = axisXValue;
    }

    public float getAxisYValue() {
        return axisYValue;
    }

    public void setAxisYValue(float axisYValue) {
        this.axisYValue = axisYValue;
    }

    public long getDownTime() {
        return downTime;
    }

    public void setDownTime(long downTime) {
        this.downTime = downTime;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getxPrecision() {
        return xPrecision;
    }

    public void setxPrecision(float xPrecision) {
        this.xPrecision = xPrecision;
    }

    public float getyPrecision() {
        return yPrecision;
    }

    public void setyPrecision(float yPrecision) {
        this.yPrecision = yPrecision;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRawX() {
        return rawX;
    }

    public void setRawX(float rawX) {
        this.rawX = rawX;
    }

    public float getRawY() {
        return rawY;
    }

    public void setRawY(float rawY) {
        this.rawY = rawY;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getToolMajor() {
        return toolMajor;
    }

    public void setToolMajor(float toolMajor) {
        this.toolMajor = toolMajor;
    }

    public float getToolMinor() {
        return toolMinor;
    }

    public void setToolMinor(float toolMinor) {
        this.toolMinor = toolMinor;
    }

    public float getTouchMajor() {
        return touchMajor;
    }

    public void setTouchMajor(float touchMajor) {
        this.touchMajor = touchMajor;
    }

    public float getTouchMinor() {
        return touchMinor;
    }

    public void setTouchMinor(float touchMinor) {
        this.touchMinor = touchMinor;
    }
}
