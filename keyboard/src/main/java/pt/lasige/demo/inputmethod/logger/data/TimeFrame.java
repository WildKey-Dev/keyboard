package pt.lasige.demo.inputmethod.logger.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import pt.lasige.demo.inputmethod.latin.R;
import pt.lasige.demo.inputmethod.study.scheduler.ScheduleController;
import pt.lasige.demo.inputmethod.study.scheduler.notification.AlarmReceiver;
import pt.lasige.demo.inputmethod.study.scheduler.notification.Notification;

public class TimeFrame {
    String start;
    String end;
    String timeFrameID;
    Notification notification;
    int day;
    int month;
    int year;

    ArrayList<String> tasks;

    public TimeFrame() {
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    public String getTimeFrameID() {
        return timeFrameID;
    }

    public void setTimeFrameID(String timeFrameID) {
        this.timeFrameID = timeFrameID;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public void setAlarm(Context context) {

        Calendar startDate = getStartDate();
        Calendar endDate = getEndDate();

        if(Calendar.getInstance().getTime().before(startDate.getTime())){
            Intent intent = new Intent(context, AlarmReceiver.class);
            int requestCode = year + month + day + Integer.parseInt(start.replace(":", ""));
            PendingIntent pending;
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            intent.putExtra("type", "fixed_time_notification");
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("title", notification.getTitle() + " (" + start + " - " + end + ")");
            intent.putExtra("message", notification.getMessage() + ". " + context.getString(R.string.you_have_until) + " " + end + " " + context.getString(R.string.to_finish));
            pending = PendingIntent.getBroadcast(context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(startDate.getTimeInMillis(), pending), pending);
            ScheduleController.getInstance().addAlarm(pending);

            //schedule alarm to cancel notification if time frame expires
            Intent cancelIntent = new Intent(context, AlarmReceiver.class);
            cancelIntent.putExtra("type", "cancel_fixed_time_notification");
            cancelIntent.putExtra("requestCode", requestCode);
            int requestCodeCancel = year + month + day + Integer.parseInt(end.replace(":", ""));
            pending = PendingIntent.getBroadcast(context,
                    requestCodeCancel,
                    cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(endDate.getTimeInMillis(), pending), pending);
            ScheduleController.getInstance().addAlarm(pending);

        }
    }

    private Calendar getDayCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        return calendar;
    }

    private Calendar getStartDate(){
        Calendar calendar = getDayCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(start.split(":")[1]));
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private Calendar getEndDate(){
        Calendar calendar = getDayCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(end.split(":")[1]));
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}

