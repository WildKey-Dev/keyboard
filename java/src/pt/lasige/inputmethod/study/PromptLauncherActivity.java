package pt.lasige.inputmethod.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.logger.DataBaseFacade;
import pt.lasige.inputmethod.logger.data.Config;
import pt.lasige.inputmethod.logger.data.Prompt;
import pt.lasige.inputmethod.study.adapters.PromptAdapter;
import pt.lasige.inputmethod.study.scheduler.PromptIntentFactory;
import pt.lasige.inputmethod.study.scheduler.ScheduleController;

public class PromptLauncherActivity extends Activity {
    private PromptAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_reminder);
        if (getActionBar() != null)
            getActionBar().hide();

        DataBaseFacade.getInstance().setDemo(false);

        ListView mListView = (ListView) findViewById(R.id.lv_prompts);
        adapter = new PromptAdapter(this, R.layout.listview_item_prompt, ScheduleController.getInstance().getQueue(), (Button) findViewById(R.id.bt_next), (TextView) findViewById(R.id.tv_empty_list));
        ScheduleController.getInstance().setAdapter(adapter);
        mListView.setAdapter(adapter);

        findViewById(R.id.bt_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ScheduleController.getInstance().getConfig().getStudyId().equals("noConfigId") && ScheduleController.getInstance().getConfig().isValid()){
                    if(!adapter.getData().isEmpty()){
                        Intent intent = PromptIntentFactory.getIntentForPrompt(getApplicationContext(), ScheduleController.getInstance().getPrompt(adapter.getData().get(0)), adapter.getData().get(0));
                        startActivity(intent);
                    }else {
                        finish();
                    }
                }else{
                    finish();
                }
            }
        });
    }

}
