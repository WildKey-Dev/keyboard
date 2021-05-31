package pt.lasige.demo.inputmethod.latin.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import pt.lasige.demo.inputmethod.latin.R;
import pt.lasige.demo.inputmethod.logger.DataBaseFacade;
import pt.lasige.demo.inputmethod.logger.LoggerController;

/**
 * "Advanced" settings sub screen.
 *
 * This settings sub screen handles the following advanced preferences.
 * - Audio engine type
 */
public final class StudySettings extends SubScreenFragment {
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_study);

        final SwitchPreference read =
                (SwitchPreference) findPreference(Settings.PREF_LOG);

        read.setEnabled(true);

        final SwitchPreference log_implicit =
                (SwitchPreference) findPreference(Settings.PREF_LOG_IMPLICIT);

        log_implicit.setEnabled(true);

        final SwitchPreference log_u_d =
                (SwitchPreference) findPreference(Settings.PREF_LOG_UP_DOWN);

        log_u_d.setEnabled(true);


        createNotificationChannel();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if(key.equals(Settings.PREF_USER_ID)){
                DataBaseFacade.getInstance().setLocalUserID(sharedPreferences.getString(Settings.PREF_USER_ID, "null"));
            }
            if(key.equals(Settings.PREF_LOG)){
                LoggerController.getInstance().setLog(getContext(), sharedPreferences.getBoolean(Settings.PREF_LOG, true));
            }
            if(key.equals(Settings.PREF_LOG_UP_DOWN)){
                LoggerController.getInstance().setLogTouch(sharedPreferences.getBoolean(Settings.PREF_LOG_UP_DOWN, true));
            }
            if(key.equals(Settings.PREF_LOG_IMPLICIT)){
                LoggerController.getInstance().setImplicitLog(sharedPreferences.getBoolean(Settings.PREF_LOG_IMPLICIT, true));
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "eventos";
            String description = "Notificações sobre ações a desempenhar";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1904", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
