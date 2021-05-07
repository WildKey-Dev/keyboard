package uk.openlab.inputmethod.logger.data;

public class ScheduleEvent {
    int time_between_notifications = -1;
    String title = null;
    String description = null;

    public ScheduleEvent() {
    }

    public int getTime_between_notifications() {
        return time_between_notifications;
    }

    public void setTime_between_notifications(int time_between_notifications) {
        this.time_between_notifications = time_between_notifications;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
