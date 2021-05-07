package uk.openlab.inputmethod.latin.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

import uk.openlab.inputmethod.latin.R;
import uk.openlab.inputmethod.logger.DataBaseFacade;
import uk.openlab.inputmethod.study.PromptLauncherActivity;
import uk.openlab.inputmethod.study.scheduler.ScheduleController;

public class SettingsLauncherActivity extends Activity {
    SettingsLauncherActivityUI settingsLauncherActivityUI;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_launcher);
        Objects.requireNonNull(getActionBar()).setTitle(R.string.english_ime_name);
        Objects.requireNonNull(getActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.study_accent_color)));

        DataBaseFacade.getInstance().setDemo(false);
        settingsLauncherActivityUI =
                new SettingsLauncherActivityUI(findViewById(R.id.tv_tasks_badge), findViewById(R.id.tasks_badge));

        settingsLauncherActivityUI.refresh(ScheduleController.getInstance().getQueue());

        ScheduleController.getInstance().setSettingsUiController(settingsLauncherActivityUI);

        findViewById(R.id.bt_start_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(SettingsActivity.EXTRA_ENTRY_KEY,
                        SettingsActivity.EXTRA_ENTRY_VALUE_APP_ICON);
                startActivity(intent);
            }
        });
        //gone from the UI
//        findViewById(R.id.bt_start_demo_activity).setVisibility(View.GONE);
//        findViewById(R.id.bt_start_demo_activity).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Intent intent = new Intent(getApplicationContext(), DemoActivity.class);
//                startActivity(intent);
//            }
//        });

        findViewById(R.id.bt_start_prompt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), PromptLauncherActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        settingsLauncherActivityUI.refresh(ScheduleController.getInstance().getQueue());
    }
}