package pt.lasige.ideafast.study.inputmethod.study;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import pt.lasige.ideafast.study.latin.R;
import pt.lasige.ideafast.study.inputmethod.logger.DataBaseFacade;
import pt.lasige.ideafast.study.inputmethod.study.adapters.PromptLauncherUIController;
import pt.lasige.ideafast.study.inputmethod.study.scheduler.PromptIntentFactory;
import pt.lasige.ideafast.study.inputmethod.study.scheduler.ScheduleController;

public class PromptLauncherActivity extends AppCompatActivity {
    private PromptLauncherUIController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_reminder);
        if (getActionBar() != null)
            getActionBar().hide();

        DataBaseFacade.getInstance().setDemo(false);

        controller = new PromptLauncherUIController(this, ScheduleController.getInstance().getQueue(), findViewById(R.id.tv_title), findViewById(R.id.tv_message), findViewById(R.id.bt_next));
        ScheduleController.getInstance().setPromptUiController(controller);

        findViewById(R.id.bt_next).setOnClickListener(view -> {
            if (!ScheduleController.getInstance().getConfig().getStudyId().equals("noConfigId") && ScheduleController.getInstance().getConfig().isValid()){
                if(!controller.getData().isEmpty()){
                    Intent intent = PromptIntentFactory.getIntentForPrompt(getApplicationContext(), ScheduleController.getInstance().getPrompt(controller.getData().get(0)), controller.getData().get(0));
                    if(intent == null){
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("A aplicação encontrou um problema")
                                .setMessage("Por favor feche a aplicação, confirme que tem acesso à Internet e volte a tentar.")
                                .setNeutralButton("Fechar", (dialog1, which) -> {
                                    finishAffinity(); // Close all activites
                                    System.exit(0);  // Releasing resources
                                })
                                .setOnDismissListener(dialog12 -> {
                                    finishAffinity(); // Close all activites
                                    System.exit(0);  // Releasing resources
                                })
                                .create();

                        dialog.setOnShowListener(arg0 -> {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.study_accent_color));
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.study_accent_color));
                        });
                        dialog.show();
                    }else {
                        startActivity(intent);
                        finish();
                    }
                }else {
                    finish();
                }
            }else{
                finish();
            }
        });
        if(ScheduleController.getInstance().getQueue().size() == 0)
            findViewById(R.id.bt_change_config).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.bt_change_config).setVisibility(View.GONE);

        findViewById(R.id.bt_change_config).setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.change_session_code));

            View layout = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.change_config_dialog, null, false);
            builder.setView(layout);
            EditText input = layout.findViewById(R.id.et_config_id);

            // Set up the buttons
            builder.setPositiveButton(getString(R.string.setup_step0_action), (dialog, which) -> {

                String newConfigID = input.getText().toString();
                DataBaseFacade.getInstance().setConfigID(getApplicationContext(), newConfigID, result -> {
                    if (result){
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.config_id), newConfigID);
                        editor.apply();
                        editor.commit();
                        Toast.makeText(getApplicationContext(), getString(R.string.user_is_valid), Toast.LENGTH_SHORT).show();
                        Runnable r = this::recreate;
                        new Handler().postDelayed(r, 1000);
                    }else {
                        Toast.makeText(getApplicationContext(),getString(R.string.user_is_invalid), Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

            builder.show();

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.refresh(this, ScheduleController.getInstance().getQueue());
    }
}
