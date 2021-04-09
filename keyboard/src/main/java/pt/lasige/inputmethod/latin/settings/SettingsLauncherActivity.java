package pt.lasige.inputmethod.latin.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.logger.DataBaseFacade;
import pt.lasige.inputmethod.study.DemoActivity;
import pt.lasige.inputmethod.study.PromptLauncherActivity;
import pt.lasige.inputmethod.study.scheduler.ScheduleController;

public class SettingsLauncherActivity extends Activity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_launcher);
        Objects.requireNonNull(getActionBar()).setTitle(R.string.english_ime_name);
        Objects.requireNonNull(getActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.study_accent_color)));

        DataBaseFacade.getInstance().setDemo(false);

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
        ArrayList<String> data = ScheduleController.getInstance().getQueue();

        if(data.isEmpty()) {
            findViewById(R.id.tasks_badge).setVisibility(View.GONE);
        }else {
            findViewById(R.id.tasks_badge).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_tasks_badge)).setText(String.valueOf(data.size()));
        }
        findViewById(R.id.bt_start_prompt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), PromptLauncherActivity.class);
                startActivity(intent);
            }
        });
    }
}