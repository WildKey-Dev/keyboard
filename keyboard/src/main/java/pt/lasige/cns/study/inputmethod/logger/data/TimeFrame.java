package pt.lasige.cns.study.inputmethod.logger.data;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import pt.lasige.cns.study.latin.R;
import pt.lasige.cns.study.inputmethod.study.scheduler.ScheduleController;
import pt.lasige.cns.study.inputmethod.study.scheduler.notification.AlarmReceiver;
import pt.lasige.cns.study.inputmethod.study.scheduler.notification.Notification;

public class TimeFrame {
    String startDate;
    String endDate;
    String timeFrameID;
    Notification notification;

    ArrayList<String> tasks;

    public TimeFrame() {
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String start) {
        this.startDate = start;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String end) {
        this.endDate = end;
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

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void setAlarm(Context context) {

        Calendar startDate = getStartCalendar();
        Calendar endDate = getEndCalendar();

        if(Calendar.getInstance().getTime().before(startDate.getTime())){
            Intent intent = new Intent(context, AlarmReceiver.class);
            int requestCode = startDate.get(Calendar.YEAR) + startDate.get(Calendar.MONTH) + startDate.get(Calendar.DAY_OF_MONTH) + startDate.get(Calendar.HOUR_OF_DAY) + startDate.get(Calendar.MINUTE);
            PendingIntent pending;
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            intent.putExtra("type", "fixed_time_notification");
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("title", notification.getTitle() + " (" + getHours(startDate) + " - " + getHours(endDate) + ")");
            intent.putExtra("message", notification.getMessage() + ". " + context.getString(R.string.you_have_until) + " " + getHours(endDate) + " " + context.getString(R.string.to_finish));
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
            int requestCodeCancel = endDate.get(Calendar.YEAR) + endDate.get(Calendar.MONTH) + endDate.get(Calendar.DAY_OF_MONTH) + endDate.get(Calendar.HOUR_OF_DAY) + endDate.get(Calendar.MINUTE);
            pending = PendingIntent.getBroadcast(context,
                    requestCodeCancel,
                    cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(endDate.getTimeInMillis(), pending), pending);
            ScheduleController.getInstance().addAlarm(pending);

        }
    }

    public String getHours(Calendar c){
        return (c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR_OF_DAY)) + ":" + (c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE));
    }

    public Calendar getStartCalendar(){
        Calendar s = parseDate(startDate);
        if(s == null)
            return Calendar.getInstance();
        else
            return s;
    }

    public Calendar getEndCalendar(){
        Calendar e = parseDate(endDate);
        if(e == null)
            return Calendar.getInstance();
        else
            return e;
    }

    private Calendar parseDate(String date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(date));
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

