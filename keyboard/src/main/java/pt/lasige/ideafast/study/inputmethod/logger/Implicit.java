package pt.lasige.ideafast.study.inputmethod.logger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.Tuple;
import pt.lasige.ideafast.study.inputmethod.study.scheduler.ScheduleController;
import pt.lasige.ideafast.study.inputmethod.study.scheduler.notification.AlarmReceiver;

public class Implicit {

    private static Implicit instance;
    private boolean on;
//    private ValueEventListener implicitListener;
    private ValueEventListener scheduleListener;
    private ValueEventListener programmedListener;
    private ArrayList<String> scheduleTimestamps = new ArrayList<>();
    private ArrayList<Tuple> schedules = new ArrayList<>();

    public static Implicit getInstance() {

        if(instance == null)
            instance = new Implicit();

        return instance;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
       return on;
    }

    private Implicit(){
        on = false;
    }

    public ArrayList<Tuple> getSchedules() {
        return schedules;
    }

    public void setImplicitListener(Context context) {
//        if (implicitListener == null){
//            implicitListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    boolean implicit = false;
//
//                    if(snapshot.getValue() != null)
//                        implicit = (boolean) snapshot.getValue();
//
//                    setOn(implicit);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    setOn(false);
//                }
//            };
//            DataBaseFacade.getInstance().setImplicitListener(implicitListener);
//        }
//        if(scheduleListener == null){
//            scheduleListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.getValue() != null) {
//                        HashMap<String, Long> activeSchedule = (HashMap<String, Long>) snapshot.getValue();
//                        if(isDateInTheFuture(timestampToCalendar(activeSchedule.get("start")).getTime())){
//                            DataBaseFacade.getInstance().stopImplicit();
//                            setOn(false);
//                            sendAlarm(context, "start_implicit", new Random().nextInt(), activeSchedule.get("start"));
//                        } else {
//                            // means that we are inside the schedule
//                            if(isDateInTheFuture(timestampToCalendar(activeSchedule.get("end")).getTime())){
//                                if(!isOn()){
//                                    sendAlarm(context, "start_implicit", new Random().nextInt(), System.currentTimeMillis() + 1000);
//                                } else {
//                                    // to be sure
//                                    DataBaseFacade.getInstance().startImplicit();
//                                    setOn(true);
//                                }
//                            } else {
//                                // both dates are in the past turn off recording
//                                if(!isOn()){
//                                    // to be sure
//                                    DataBaseFacade.getInstance().stopImplicit();
//                                    setOn(false);
//                                } else {
//                                    sendAlarm(context, "stop_implicit", new Random().nextInt(), System.currentTimeMillis() + 1000);
//                                }
//                            }
//                        }
//                        if(isDateInTheFuture(timestampToCalendar(activeSchedule.get("end")).getTime())){
//                            sendAlarm(context, "stop_implicit", new Random().nextInt(), activeSchedule.get("end"));
//                        }
//                    } else {
//                        if(isOn())
//                            sendAlarm(context, "stop_implicit", new Random().nextInt(), System.currentTimeMillis() + 1000);
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            };
//            DataBaseFacade.getInstance().setImplicitScheduleListener(scheduleListener);
//        }
        if(programmedListener == null) {
            programmedListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null) {
                        HashMap<String, HashMap<String, String>> programmedSchedule = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                        for (String key: programmedSchedule.keySet()){

                            HashMap<String, String> hm = programmedSchedule.get(key);
                            String start = hm.get("start");
                            String end = hm.get("end");
                            scheduleImplicit(context, start, end);
                        }
                    } else {
                        if(isOn())
                            sendAlarm(context, "stop_implicit", new Random().nextInt(), getCurrentDateString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            DataBaseFacade.getInstance().setImplicitProgrammedScheduleListener(programmedListener);
        }
    }

    private void scheduleImplicit(Context context, String start, String end){
        
        if (!containsSchedule(start, end)){

            schedules.add(new Tuple(start, end));

            if(isDateInTheFuture(parseDateString(start))){
                sendAlarm(context, "start_implicit", new Random().nextInt(), start);
            } else {
                // means that we are inside the schedule
                if(isDateInTheFuture(parseDateString(end))){
                    if(!isOn()){
                        sendAlarm(context, "start_implicit", new Random().nextInt(), getCurrentDateString());
                    } else {
                        // to be sure
                        DataBaseFacade.getInstance().startImplicit();
                        setOn(true);
                    }
                } else {
                    // both dates are in the past turn off recording
                    if(!isOn()){
                        // to be sure
                        DataBaseFacade.getInstance().stopImplicit();
                        setOn(false);
                    } else {
                        sendAlarm(context, "stop_implicit", new Random().nextInt(), getCurrentDateString());
                    }
                }
            }
            if(isDateInTheFuture(parseDateString(end))){
                sendAlarm(context, "stop_implicit", new Random().nextInt(), end);
            }
        }
    }

    public void sendAlarm(Context context, String type, int requestCode, String triggerDate){

        if (!scheduleTimestamps.contains(triggerDate)){
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pending;
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            intent.putExtra("type", type);
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("triggerTime", triggerDate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pending = PendingIntent.getBroadcast(context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            } else {
                pending = PendingIntent.getBroadcast(context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(parseDateString(triggerDate).getTime(), pending), pending);
            ScheduleController.getInstance().addAlarm(pending);
            scheduleTimestamps.add(triggerDate);
        }
    }

    static public boolean isDateContained(Date d1, Date d2, Date test){
        return test.after(d1) && test.before(d2);
    }

    static public boolean isDateInTheFuture(Date date){
        return Calendar.getInstance().getTime().before(date);
    }

    static public Date parseDateString(String date){

        SimpleDateFormat simpleDateFormat = getSDF();
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Calendar.getInstance().getTime();
    }

    public boolean containsSchedule(String start, String end){

        for (Tuple t: schedules){
            if(((String) t.t1).equals(start) && ((String) t.t2).equals(end)){
                return true;
            }
        }

        return false;
    }

    public static String getCurrentDateString(){
        return getSDF().format(Calendar.getInstance().getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getSDF(){
        return  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

}
