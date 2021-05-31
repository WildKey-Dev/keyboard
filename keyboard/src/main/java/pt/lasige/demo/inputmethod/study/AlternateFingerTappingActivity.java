package pt.lasige.demo.inputmethod.study;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import pt.lasige.demo.inputmethod.latin.R;
import pt.lasige.demo.inputmethod.logger.DataBaseFacade;
import pt.lasige.demo.inputmethod.logger.LoggerController;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.Tuple;
import pt.lasige.demo.inputmethod.study.scheduler.ScheduleController;

public class AlternateFingerTappingActivity  extends Activity {
    String questionID, studyID;
    TextView textView;
    int right = 0, wrong = 0;
    HashMap<String, Tuple> test = new HashMap<>();
    String lastClicked = "";
    ProgressDialog progressDialog;
    Handler timeOutHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActionBar() != null)
            getActionBar().hide();
        Intent i = getIntent();
        studyID = i.getStringExtra("study-id");
        questionID = i.getStringExtra("question-id");

        setFinger("right");
    }

    private void startCountDown(String finger) {
        Handler handler=new Handler();
        Runnable r;

        textView.setText("3");
        r = () -> textView.setText("2");
        handler.postDelayed(r, 1000);
        handler=new Handler();
        r = () -> textView.setText("1");
        handler.postDelayed(r, 2000);
        r = () -> setUpTest(finger);
        handler.postDelayed(r, 3000);

    }

    private void setUpTest(String finger){
        right = 0;
        wrong = 0;
        LoggerController.getInstance().getLogger().setEventRunning(true);
        textView.setVisibility(View.GONE);
        findViewById(R.id.bt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.bt_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if(lastClicked.equals("") || lastClicked.equals("right"))
                    right++;
                else if(lastClicked.equals("left"))
                    wrong++;
                else
                    wrong++;
                lastClicked = "left";
            }
        });
        findViewById(R.id.bt_right).setVisibility(View.VISIBLE);
        findViewById(R.id.bt_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(lastClicked.equals("") || lastClicked.equals("left"))
                    right++;
                else if(lastClicked.equals("right"))
                    wrong++;
                else
                    wrong++;
                lastClicked = "right";
            }
        });
        findViewById(R.id.rl_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wrong++;
            }
        });

        new CountDownTimer(20 * 1000, 10 * 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish(){
                DataBaseFacade.getInstance().setFingerTapping(studyID, questionID, finger, right, wrong);
                test.put(finger, new Tuple(right, wrong));
                if(finger.equals("right"))
                    setFinger("left");
                else
                    finishTask();
            }
        }.start();
    }

    private void setFinger(String finger){

        setContentView(R.layout.start_screen);
        if(finger.equals("right"))
            ((TextView) findViewById(R.id.tv_task)).setText(R.string.index_finger_r);
        else
            ((TextView) findViewById(R.id.tv_task)).setText(R.string.index_finger_l);

        findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_alternate_finger_tapping);
                textView = findViewById(R.id.tv_text);
                startCountDown(finger);
            }
        });
    }

    private void showProgressDialog() {
        if(progressDialog != null)
            dismissProgressDialog();

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMax(100);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.loading_taks));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,"Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismissProgressDialog();
                finish();
            }
        });
        progressDialog.show();
        progressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE).setEnabled(false);
        timeOutHandler = new Handler();
        timeOutHandler.postDelayed(createRunnable(), 1000 * 5);
    }

    private void dismissProgressDialog(){
        progressDialog.dismiss();
    }

    private Runnable createRunnable(){
        return () -> {
            progressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE).setEnabled(true);
            progressDialog.setMessage(getString(R.string.is_taking_to_long));
        };
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
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void finishTask(){
        double avg = ((double) ((int) test.get("right").t1 + (int) test.get("left").t1)) / 2;
        DataBaseFacade.getInstance().setFingerTappingAvg(studyID, questionID, avg);
        setContentView(R.layout.finish_screen);
        ScheduleController.getInstance().dequeue(questionID);
        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {finish(); }
        };
        handler.postDelayed(r, 1500);
    }

}
