package pt.lasige.demo.inputmethod.study.scheduler.notification;

public class Notification {
    String message;
    String title;

    public Notification() {
    }

    public Notification(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
