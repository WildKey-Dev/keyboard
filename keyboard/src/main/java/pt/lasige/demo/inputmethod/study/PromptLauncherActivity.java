package pt.lasige.demo.inputmethod.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import pt.lasige.demo.inputmethod.latin.R;
import pt.lasige.demo.inputmethod.logger.DataBaseFacade;
import pt.lasige.demo.inputmethod.study.adapters.PromptLauncherUIController;
import pt.lasige.demo.inputmethod.study.scheduler.PromptIntentFactory;
import pt.lasige.demo.inputmethod.study.scheduler.ScheduleController;

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
            if(!controller.getData().isEmpty()){
                Intent intent = PromptIntentFactory.getIntentForPrompt(getApplicationContext(), ScheduleController.getInstance().getPrompt(controller.getData().get(0)), controller.getData().get(0));
                startActivity(intent);
                finish();
            }else {
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
