package pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures;

import java.util.ArrayList;

public class Error {
    private String name;
    private String message;
    private ArrayList<Tuple> additionalInfo;
    private long timestamp = System.currentTimeMillis();

    public Error(String name, String message, ArrayList<Tuple> additionalInfo) {
        this.name = name;
        this.message = message;
        this.additionalInfo = additionalInfo;
    }

    public Error(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Tuple> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(ArrayList<Tuple> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
