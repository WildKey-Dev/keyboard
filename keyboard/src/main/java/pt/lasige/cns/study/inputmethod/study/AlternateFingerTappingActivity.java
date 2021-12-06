package pt.lasige.cns.study.inputmethod.study;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import pt.lasige.cns.study.inputmethod.logger.DataBaseFacade;
import pt.lasige.cns.study.inputmethod.logger.LoggerController;
import pt.lasige.cns.study.inputmethod.metrics.textentry.datastructures.Tuple;
import pt.lasige.cns.study.inputmethod.study.questionnaire.data.Point;
import pt.lasige.cns.study.inputmethod.study.questionnaire.data.Tap;
import pt.lasige.cns.study.inputmethod.study.scheduler.ScheduleController;
import pt.lasige.cns.study.latin.R;

public class AlternateFingerTappingActivity  extends Activity {
    Activity myActivity;
    String questionID, studyID;
    TextView textView;
    int right = 0, wrong = 0;
    HashMap<String, Tuple> test = new HashMap<>();
    String lastClicked = "";
    long startTS;

    final float oneMMinInches = 0.0393701f;
    final int targetWidthMM = 30, targetHeightMM = 45, targetSpacingMM = 15;
    float targetWidthPX, targetHeightPX, targetSpacingPX;
    int screenWidthPX, screenHeightPX;
    float screeSizeHeightMM, screeSizeWidthMM;
    float dpiScale = 1f;

    ArrayList<Tap> taps;
    Tap currentTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActionBar() != null)
            getActionBar().hide();
        Intent i = getIntent();
        studyID = i.getStringExtra("study-id");
        questionID = i.getStringExtra("question-id");
        startTS = System.currentTimeMillis();
        setFinger("right");
        myActivity = this;
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        DisplayMetrics realMetrics = new DisplayMetrics();
        d.getRealMetrics(realMetrics);

        screenHeightPX = realMetrics.heightPixels;
        screenWidthPX = realMetrics.widthPixels;
        screeSizeHeightMM = screenHeightPX / (realMetrics.ydpi * oneMMinInches);
        screeSizeWidthMM = screenWidthPX / (realMetrics.xdpi * oneMMinInches);

        targetWidthPX = targetWidthMM * realMetrics.widthPixels / screeSizeWidthMM;
        targetHeightPX = targetHeightMM * realMetrics.heightPixels / screeSizeHeightMM;
        targetSpacingPX = targetSpacingMM * realMetrics.widthPixels / screeSizeWidthMM;

//        if( realMetrics.densityDpi != DisplayMetrics.DENSITY_DEVICE_STABLE){
//            dpiScale = (float) realMetrics.densityDpi / DisplayMetrics.DENSITY_DEVICE_STABLE;
//        }

        taps = new ArrayList<>();
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

    @SuppressLint("ClickableViewAccessibility")
    private void setUpTest(String finger){
        right = 0;
        wrong = 0;
        LoggerController.getInstance().getLogger().setEventRunning(true);
        textView.setVisibility(View.GONE);
        Button leftFinger = findViewById(R.id.bt_left);
        leftFinger.setVisibility(View.VISIBLE);
        leftFinger.getLayoutParams().width = Math.round(targetWidthPX);
        leftFinger.getLayoutParams().height = Math.round(targetHeightPX);
        leftFinger.setX((targetSpacingPX / 2) * -1);
        leftFinger.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                currentTap = new Tap();
                currentTap.setSide(Tap.Side.LEFT);
                currentTap.setDownAbsolute(new Point(event.getRawX(), event.getRawY()));
                currentTap.setDownRelative(new Point(event.getX(), event.getY()));
                currentTap.setDownTimestamp(System.currentTimeMillis());
            }
            if(event.getAction() == MotionEvent.ACTION_UP){
                currentTap.setUpAbsolute(new Point(event.getRawX(), event.getRawY()));
                currentTap.setUpRelative(new Point(event.getX(), event.getY()));
                currentTap.setUpTimestamp(System.currentTimeMillis());
                taps.add(currentTap);
            }
            return false;
        });
        leftFinger.setOnClickListener(view1 -> {
            if(lastClicked.equals("") || lastClicked.equals("right"))
                right++;
            else if(lastClicked.equals("left"))
                wrong++;
            else
                wrong++;
            lastClicked = "left";
        });
        Button rightFinger = findViewById(R.id.bt_right);
        rightFinger.setVisibility(View.VISIBLE);
        rightFinger.getLayoutParams().width = Math.round(targetWidthPX);
        rightFinger.getLayoutParams().height = Math.round(targetHeightPX);
        rightFinger.setX((targetSpacingPX / 2));
        rightFinger.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                currentTap = new Tap();
                currentTap.setSide(Tap.Side.RIGHT);
                currentTap.setDownAbsolute(new Point(event.getRawX(), event.getRawY()));
                currentTap.setDownRelative(new Point(event.getX(), event.getY()));
                currentTap.setDownTimestamp(System.currentTimeMillis());
            }
            if(event.getAction() == MotionEvent.ACTION_UP){
                currentTap.setUpAbsolute(new Point(event.getRawX(), event.getRawY()));
                currentTap.setUpRelative(new Point(event.getX(), event.getY()));
                currentTap.setUpTimestamp(System.currentTimeMillis());
                taps.add(currentTap);
            }
            return false;
        });
        rightFinger.setOnClickListener(view1 -> {
            if(lastClicked.equals("") || lastClicked.equals("left"))
                right++;
            else if(lastClicked.equals("right"))
                wrong++;
            else
                wrong++;
            lastClicked = "right";
        });
        findViewById(R.id.rl_bg).setOnClickListener(view -> {
            wrong++;
        });
        findViewById(R.id.rl_bg).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                currentTap = new Tap();
                currentTap.setSide(Tap.Side.OUT);
                currentTap.setDownAbsolute(new Point(event.getRawX(), event.getRawY()));
                currentTap.setDownRelative(new Point(event.getX(), event.getY()));
                currentTap.setDownTimestamp(System.currentTimeMillis());
            }
            if(event.getAction() == MotionEvent.ACTION_UP){
                currentTap.setUpAbsolute(new Point(event.getRawX(), event.getRawY()));
                currentTap.setUpRelative(new Point(event.getX(), event.getY()));
                currentTap.setUpTimestamp(System.currentTimeMillis());
                taps.add(currentTap);
            }
            return false;
        });

        new CountDownTimer(20 * 1000, 10 * 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish(){
                DataBaseFacade.getInstance().setFingerTapping(studyID, questionID, finger, right, wrong, startTS, System.currentTimeMillis(), taps);
                taps = new ArrayList<>();
                test.put(finger, new Tuple(right, wrong));
                if(finger.equals("right"))
                    setFinger("left");
                else
                    finishTask();
            }
        }.start();
    }

    private void setFinger(String finger){

        if(finger.equals("right")) {
            setContentView(R.layout.start_screen);
            ((TextView) findViewById(R.id.tv_task)).setText(R.string.index_finger_r);
            findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setContentView(R.layout.activity_alternate_finger_tapping);
                    textView = findViewById(R.id.tv_text);
                    startCountDown(finger);
                }
            });
        }else{
            setContentView(R.layout.finish_screen);
            ((TextView) findViewById(R.id.tv_finish)).setText(R.string.index_finger_r_saved);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    setContentView(R.layout.start_screen);
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
            }, 2000);
        }
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
        DataBaseFacade.getInstance().setFinished(studyID, questionID, true);
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
