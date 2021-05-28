package pt.lasige.inputmethod.study.scheduler.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.latin.settings.SettingsLauncherActivity;

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

    }

    private void sendNotification(Context context, String messageBody, String title, int id) {
        Intent pra = new Intent(context, SettingsLauncherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, pra, PendingIntent.FLAG_UPDATE_CURRENT);

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
