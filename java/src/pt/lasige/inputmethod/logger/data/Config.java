package pt.lasige.inputmethod.logger.data;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Config {
    String studyId;
    String configId;
    String fromDate;
    String toDate;
    String fromTime;
    String toTime;
    String dayEndTime;
    String dayStartTime;
    int periodicity;
    ArrayList<TimeFrame> timeFrames;
    ArrayList<String> sequenceTasks;
    ArrayList<String> timesADay;
    String periodicityUnit;
    String reminderType;

    public Config() {
    }

    public int getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(int periodicity) {
        this.periodicity = periodicity;
    }

    public String getDayEndTime() {
        return dayEndTime;
    }

    public void setDayEndTime(String dayEndTime) {
        this.dayEndTime = dayEndTime;
    }

    public String getDayStartTime() {
        return dayStartTime;
    }

    public void setDayStartTime(String dayStartTime) {
        this.dayStartTime = dayStartTime;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public ArrayList<String> getSequenceTasks() {
        return sequenceTasks;
    }

    public void setSequenceTasks(ArrayList<String> sequenceTasks) {
        this.sequenceTasks = sequenceTasks;
    }

    public String getPeriodicityUnit() {
        return periodicityUnit;
    }

    public void setPeriodicityUnit(String periodicityUnit) {
        this.periodicityUnit = periodicityUnit;
    }

    public ArrayList<String> getTimesADay() {
        return timesADay;
    }

    public void setTimesADay(ArrayList<String> timesADay) {
        this.timesADay = timesADay;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public ArrayList<TimeFrame> getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(ArrayList<TimeFrame> timeFrames) {
        this.timeFrames = timeFrames;
    }

    public boolean isValid(){
        try {
            return Calendar.getInstance().getTime().before(parseDate(getToDate()));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Date parseDate(String date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
