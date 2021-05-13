package uk.openlab.inputmethod.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import uk.openlab.inputmethod.latin.R;
import uk.openlab.inputmethod.logger.DataBaseFacade;
import uk.openlab.inputmethod.study.adapters.PromptLauncherUIController;
import uk.openlab.inputmethod.study.scheduler.PromptIntentFactory;
import uk.openlab.inputmethod.study.scheduler.ScheduleController;

public class PromptLauncherActivity extends Activity {
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
                    startActivity(intent);
                    finish();
                }else {
                    finish();
                }
            }else{
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.refresh(this, ScheduleController.getInstance().getQueue());
    }
}