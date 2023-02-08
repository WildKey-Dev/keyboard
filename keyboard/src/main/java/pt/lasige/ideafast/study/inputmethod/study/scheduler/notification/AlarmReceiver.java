package pt.lasige.ideafast.study.inputmethod.study.scheduler.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import pt.lasige.ideafast.study.inputmethod.logger.DataBaseFacade;
import pt.lasige.ideafast.study.inputmethod.logger.Implicit;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.Tuple;
import pt.lasige.ideafast.study.latin.R;
import pt.lasige.ideafast.study.inputmethod.latin.settings.SettingsLauncherActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "1904";

    @Override
    public void onReceive(Context context, Intent intent) {
        createChannel(context);

        int requestCode = intent.getIntExtra("requestCode", -1);

        if(requestCode == -1)
            return;

        if ("fixed_time_notification".equals(intent.getStringExtra("type")))
            sendNotification(context, intent.getStringExtra("message"), intent.getStringExtra("title"), requestCode);

        if ("cancel_fixed_time_notification".equals(intent.getStringExtra("type")))
            cancelNotification(context, requestCode);

        if ("start_implicit".equals(intent.getStringExtra("type"))) {

            String triggerTime = intent.getStringExtra("triggerTime");

            Log.d("DEBUG", "Wildkey started collecting data");

            // if the collection is already on due to conflicting schedules
            // we don't want to send another message
            if (!Implicit.getInstance().isOn()) {
                sendNotification(context, "Wildkey started collecting data", "Wildkey started collecting data", 4500);

                // if the collection is already on due to conflicting schedules
                // we don't want to override the first saved
                SimpleDateFormat spf = Implicit.getSDF();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(context.getString(R.string.schedule_start), triggerTime);
                editor.putString(context.getString(R.string.schedule_activated), Implicit.getCurrentDateString());
                editor.apply();
            }

            //update the db
            DataBaseFacade.getInstance().startImplicit();

            //update memory if the device is offline
            Implicit.getInstance().setOn(true);

        }

        if ("stop_implicit".equals(intent.getStringExtra("type"))) {

            String triggerTime = intent.getStringExtra("triggerTime");
            ArrayList<Tuple> schedules = Implicit.getInstance().getSchedules();
            ArrayList<Tuple> contenders = new ArrayList<>();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            String start = prefs.getString(context.getString(R.string.schedule_start), "");
            String activated = prefs.getString(context.getString(R.string.schedule_activated), "");

            // in the case that we have a schedule added at a later date that incorporates a current
            // one its possible that an end trigger that was replaced to  be fired
            // T1 - T4
            // T2 - T5
            // T0 - T6 added when the T1 - T4 is running
            // when T4 fires we see what is the the end time that is further in time
            // in this case is T6 so we activate the T0 - T6
            // but when T5 fires we have saved T0 and are trying to save T0 with T5 as ending
            // in this case T2 - T5 is to be ignored
            if(Implicit.getInstance().containsSchedule(start, triggerTime)) {
                editor.remove(context.getString(R.string.schedule_start));
                editor.remove(context.getString(R.string.schedule_activated));
                editor.apply();

                DataBaseFacade.getInstance().saveScheduleToHistory(
                        start,
                        triggerTime,
                        activated,
                        Implicit.getCurrentDateString());

                for (Tuple t: schedules){
                    if(Implicit.isDateContained(
                            Implicit.parseDateString((String) t.t1),
                            Implicit.parseDateString((String) t.t2),
                            Implicit.parseDateString(triggerTime))
                    ){
                        contenders.add(t);
                    }
                }

                if(contenders.size() > 0){
                    String chosenOneEnd = "";
                    String chosenOneStart = "";
                    long auxEnd = Long.MIN_VALUE;
                    long auxStart = Long.MIN_VALUE;

                    for (Tuple t: contenders){
                        if(auxEnd <  Implicit.parseDateString((String) t.t2).getTime()){
                            chosenOneEnd = (String) t.t2;
                            chosenOneStart = (String) t.t1;
                        }
                    }

                    // save new start time for history purposes
                    editor.putString(context.getString(R.string.schedule_start), chosenOneStart);
                    editor.putString(context.getString(R.string.schedule_activated), Implicit.getCurrentDateString());
                    editor.apply();

                    //set new stop alarm
                    Implicit.getInstance().sendAlarm(context, "stop_implicit", new Random().nextInt(), chosenOneEnd);
                } else {

                    // if the collection is already off due to conflicting schedules
                    // we don't want to send another message
                    if (!Implicit.getInstance().isOn())
                        sendNotification(context, "Wildkey stopped collecting data", "Wildkey stopped collecting data", 4501);

                    //update the db
                    DataBaseFacade.getInstance().stopImplicit();

                    //update memory if the device is offline
                    Implicit.getInstance().setOn(false);
                }
            } else {

                // if the collection is already off due to conflicting schedules
                // we don't want to send another message
                if (!Implicit.getInstance().isOn())
                    sendNotification(context, "Wildkey stopped collecting data", "Wildkey stopped collecting data", 4501);

                //update the db
                DataBaseFacade.getInstance().stopImplicit();

                //update memory if the device is offline
                Implicit.getInstance().setOn(false);
            }
        }
    }


    private static void sendNotification(Context context, String messageBody, String title, int id) {
        Intent pra = new Intent(context, SettingsLauncherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, pra, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build notification based on Intent
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_keyboard)
                .setContentTitle(title)
                .setStyle(new
                        NotificationCompat.BigTextStyle().bigText(messageBody))
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        // Show notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }

    private void cancelNotification(Context context, int id){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public void createChannel(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(CHANNEL_ID, "Tasks", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            mChannel.setDescription("This channel shows notifications for user to complete tasks");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.YELLOW);
            mChannel.enableVibration(true);

            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
