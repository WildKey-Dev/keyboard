package pt.lasige.demo.inputmethod.study;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.lasige.demo.inputmethod.latin.R;
import pt.lasige.demo.inputmethod.logger.DataBaseFacade;
import pt.lasige.demo.inputmethod.logger.Logger;
import pt.lasige.demo.inputmethod.logger.LoggerController;
import pt.lasige.demo.inputmethod.logger.PhraseObserver;
import pt.lasige.demo.inputmethod.logger.data.StudyConstants;
import pt.lasige.demo.inputmethod.metrics.MetricsController;
import pt.lasige.demo.inputmethod.study.scheduler.ScheduleController;

public class CompositionActivity extends Activity {

    int index = 0, charsWritten = 0;
    String currentTargetPhrase;
    String[] phrases;
    String questionID, studyID, subType;
    boolean stop = false;
    long timeRemaining;
    boolean started = false;
    boolean recordEndPauseTS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActionBar() != null)
            getActionBar().hide();
        Intent i = getIntent();
        studyID = i.getStringExtra("study-id");
        questionID = i.getStringExtra("question-id");
        phrases =  i.getStringArrayExtra("questions");
        subType = i.getStringExtra("sub-type");
        started = false;


        setContentView(R.layout.start_screen);
        ((TextView) findViewById(R.id.tv_task)).setText(R.string.composition_desc);


        if(this.index != 0) {
            ((Button) findViewById(R.id.bt_start)).setText(R.string.str_continue);
            recordEndPauseTS = true;
        }else if(this.index == phrases.length){
            //the user has already completed the test
            finishTask("You already have completed this task");
            return;
        }

        if(subType.equals("time")){

            setContentView(R.layout.start_screen);
            ((TextView) findViewById(R.id.tv_task)).setText(R.string.composition_desc);
            long timer = i.getIntExtra("duration",0);

            //if the task has a time period to end
            //if the task was paused we get the remaining recorded time
            //we use the default value otherwise
            new CountDownTimer(timer * 60 * 1000, 60 * 1000) {
                public void onTick(long millisUntilFinished) {
                    timeRemaining = millisUntilFinished;}
                public void onFinish(){stop = true;}
            }.start();

            setStudy();

        }else {
            setStudy();
        }

    }

    private void setStudy(){
        findViewById(R.id.bt_start).setOnClickListener(view -> {
            // no index-1 because this runs before nextPhrase()
            if(recordEndPauseTS)
                DataBaseFacade.getInstance().setInterruptionEndTS(studyID, questionID, this.index, System.currentTimeMillis());

            started = true;
            setContentView(R.layout.activity_composition);
            nextPhrase();
            DataBaseFacade.getInstance().setInitTS(studyID, questionID, System.currentTimeMillis());
            EditText response = findViewById(R.id.et_response);
            response.setOnEditorActionListener((view12, actionId, event) -> {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch(result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        if (response.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), R.string.your_response_is_empty, Toast.LENGTH_SHORT).show();
                        }else {
                            recordMetrics();
                            charsWritten += response.getText().toString().length();
                            if(charsWritten > 75 || stop || this.index == phrases.length)
                                finishTask(null);
                            else
                                nextPhrase();

                            response.getText().clear();
                        }
                        break;
                }
                return true;
            });
            findViewById(R.id.bt_next).setOnClickListener(view1 -> {
                if (response.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.your_response_is_empty, Toast.LENGTH_SHORT).show();
                }else {
                    recordMetrics();
                    charsWritten += response.getText().toString().length();
                    if(charsWritten > 75 || stop || this.index == phrases.length)
                        finishTask(null);
                    else
                        nextPhrase();

                    response.setText("");
                }
            });
        });
    }

    private void nextPhrase(){
        currentTargetPhrase = phrases[this.index];
        ((TextView) findViewById(R.id.tv_question)).setText(currentTargetPhrase);
        this.index++;
        findViewById(R.id.et_response).requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(findViewById(R.id.et_phrase), InputMethodManager.SHOW_IMPLICIT);
    }

    private void recordMetrics(){
        DataBaseFacade.getInstance().setEndTS(studyID, questionID, System.currentTimeMillis());
        final Logger log = LoggerController.getInstance().getLogger();
        LoggerController.getInstance().resetLogger();
        MetricsController.getInstance().runMetricCalculation(log, null, questionID, studyID, this.index-1);
    }

    private void finishTask(String s){

        if(subType.equals("time")){
            DataBaseFacade.getInstance().setTimeRemaining(studyID, questionID, 0);
        }

        DataBaseFacade.getInstance().setFinished(studyID, questionID, true);

        setContentView(R.layout.finish_screen);
        if(s != null)
            ((TextView) findViewById(R.id.tv_finish)).setText(s);
        else
            ScheduleController.getInstance().dequeue(questionID);

        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                finish();
            }
        };
        handler.postDelayed(r, 1500);
    }

    @Override
    public void onBackPressed() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.close_window)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(R.string.no, null).create();

        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.study_accent_color));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.study_accent_color));
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        MetricsController.getInstance().setMode(StudyConstants.COMPOSITION_MODE);

        super.onResume();
    }

    @Override
    protected void onPause() {
        MetricsController.getInstance().setMode(StudyConstants.IMPLICIT_MODE);

        super.onPause();
    }
}