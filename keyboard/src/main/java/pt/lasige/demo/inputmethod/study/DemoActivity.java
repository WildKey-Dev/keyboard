package pt.lasige.demo.inputmethod.study;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import pt.lasige.demo.inputmethod.latin.R;
import pt.lasige.demo.inputmethod.logger.DataBaseFacade;
import pt.lasige.demo.inputmethod.logger.data.Prompt;
import pt.lasige.demo.inputmethod.logger.data.TimeFrame;
import pt.lasige.demo.inputmethod.study.questionnaire.QuestionnaireLauncherActivity;
import pt.lasige.demo.inputmethod.study.scheduler.ScheduleController;
import pt.lasige.demo.inputmethod.study.scheduler.notification.Notification;

public class DemoActivity extends Activity {

    boolean DEMOMODE = true;
    boolean COOLDOWN = false;
    HashMap<String, Prompt> prompts = new HashMap<>();
    ArrayList<String> spinnerTranscription =  new ArrayList<>();
    ArrayList<String> spinnerComposition =  new ArrayList<>();
    ArrayList<String> spinnerQuestionnaire =  new ArrayList<>();
    ArrayList<String> spinnerFinger =  new ArrayList<>();
    Intent intent;
    Prompt p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        if(getActionBar() != null)
            getActionBar().hide();

        DataBaseFacade.getInstance().setDemo(true);

        ArrayAdapter<String> adapterTranscription = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerTranscription);
        adapterTranscription.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        ArrayAdapter<String> adapterComposition = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerComposition);
        adapterComposition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        ArrayAdapter<String> adapterQuestionnaire = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerQuestionnaire);
        adapterQuestionnaire.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterFinger = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerFinger);
        adapterFinger.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        findViewById(R.id.bt_transcription).setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), TranscriptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("study-id", "demo");
            intent.putExtra("question-id", "transcription-demo");
            intent.putExtra("sub-type", "time");
            intent.putExtra("duration", 10);
            String[] phrases = new String[]{"better late than never", "an apple a day keeps the doctor away", "the early bird catches the worm"};
            intent.putExtra("phrases", phrases);
            startActivity(intent);
        });

        findViewById(R.id.bt_transcription_schedule).setOnClickListener(v -> {
            Log.d("DEBUGUGUGU", "cooldown " + COOLDOWN);
            if(COOLDOWN)
                return;
            startCooldown();
            TimeFrame tf = new TimeFrame();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 1);

            tf.setTimeFrameID("tf-id-transcription");
            tf.setDay(cal.get(Calendar.DAY_OF_MONTH));
            tf.setMonth(cal.get(Calendar.MONTH) + 1);
            tf.setYear(cal.get(Calendar.YEAR));
            tf.setStart(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            cal.add(Calendar.MINUTE, 60);
            tf.setEnd(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            tf.setNotification(new Notification("WildKey", "You have a task to do!"));
            ArrayList<String> task = new ArrayList<>();
            task.add("transcription-demo");
            tf.setTasks(task);
            tf.setAlarm(getApplicationContext());

            Prompt p = new Prompt();
            p.setPromptId("transcription-demo");
            p.setType("transcription");
            p.setSubType("time");
            p.setDuration(10);
            ArrayList<String> phrases = new ArrayList<>();
            phrases.add("better late than never");
            phrases.add("an apple a day keeps the doctor away");
            phrases.add("the early bird catches the worm");
            p.setPhrases(phrases);
            p.setTimeFrame(tf);
            ScheduleController.getInstance().enqueue(p, "demo");
            ScheduleController.getInstance().putTimeFrame("tf-id-transcription", tf);
        });

        findViewById(R.id.bt_composition).setOnClickListener(view -> {

            intent = new Intent(getApplicationContext(), CompositionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("study-id", "demo");
            intent.putExtra("question-id", "composition-demo");
            intent.putExtra("sub-type", "time");
            intent.putExtra("duration", 10);
            String[] questions = new String[]{"What was the last object you used before the smartphone and why?", "What did you last eat?", "How was your day?"};
            intent.putExtra("questions", questions);
            startActivity(intent);
        });

        findViewById(R.id.bt_composition_schedule).setOnClickListener(v -> {
            if(COOLDOWN)
                return;
            startCooldown();
            TimeFrame tf = new TimeFrame();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 1);

            tf.setTimeFrameID("tf-id-composition");
            tf.setDay(cal.get(Calendar.DAY_OF_MONTH));
            tf.setMonth(cal.get(Calendar.MONTH) + 1);
            tf.setYear(cal.get(Calendar.YEAR));
            tf.setStart(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            cal.add(Calendar.MINUTE, 60);
            tf.setEnd(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            tf.setNotification(new Notification("WildKey", "You have a task to do!"));
            ArrayList<String> task = new ArrayList<>();
            task.add("composition-demo");
            tf.setTasks(task);
            tf.setAlarm(getApplicationContext());

            Prompt p = new Prompt();
            p.setPromptId("composition-demo");
            p.setType("composition");
            p.setSubType("time");
            p.setDuration(10);
            ArrayList<String> phrases = new ArrayList<>();
            phrases.add("What was the last object you used before the smartphone and why?");
            phrases.add("What did you last eat?");
            phrases.add("How was your day?");
            p.setPhrases(phrases);
            p.setTimeFrame(tf);
            ScheduleController.getInstance().enqueue(p, "demo");
            ScheduleController.getInstance().putTimeFrame("tf-id-composition", tf);
        });

        findViewById(R.id.bt_questionnaire).setOnClickListener(view -> {

            intent = new Intent(getApplicationContext(), QuestionnaireLauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
            intent.putExtra("question-id", "questionnaire-demo");
            String[] questions = new String[]{"questionnaire_one_choice", "questionnaire_multiple_choice", "questionnaire_select_scale", "questionnaire_slider_scale", "questionnaire_open", "questionnaire_hour"};
            intent.putExtra("questions", questions);
            startActivity(intent);
        });

        findViewById(R.id.bt_questionnaire_schedule).setOnClickListener(v -> {
            if(COOLDOWN)
                return;
            startCooldown();
            TimeFrame tf = new TimeFrame();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 1);

            tf.setTimeFrameID("tf-id-questionnaire");
            tf.setDay(cal.get(Calendar.DAY_OF_MONTH));
            tf.setMonth(cal.get(Calendar.MONTH) + 1);
            tf.setYear(cal.get(Calendar.YEAR));
            tf.setStart(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            cal.add(Calendar.MINUTE, 60);
            tf.setEnd(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            tf.setNotification(new Notification("WildKey", "You have a task to do!"));
            ArrayList<String> task = new ArrayList<>();
            task.add("questionnaire-demo");
            tf.setTasks(task);
            tf.setAlarm(getApplicationContext());

            Prompt p = new Prompt();
            p.setPromptId("questionnaire-demo");
            p.setType("questionnaire");
            ArrayList<String> questions = new ArrayList<>();
            questions.add("questionnaire_one_choice");
            questions.add("questionnaire_multiple_choice");
            questions.add("questionnaire_select_scale");
            questions.add("questionnaire_slider_scale");
            questions.add("questionnaire_open");
            questions.add("questionnaire_hour");
            p.setPhrases(questions);
            p.setTimeFrame(tf);
            ScheduleController.getInstance().enqueue(p, "demo");
            ScheduleController.getInstance().putTimeFrame("tf-id-questionnaire", tf);
        });

        findViewById(R.id.bt_fingertapping).setOnClickListener(view -> {

            intent = new Intent(getApplicationContext(), AlternateFingerTappingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("study-id", "demo");
            intent.putExtra("question-id", "alternate-finger-tapping-demo");
            startActivity(intent);
        });

        findViewById(R.id.bt_fingertapping_schedule).setOnClickListener(v -> {
            if(COOLDOWN)
                return;
            startCooldown();
            TimeFrame tf = new TimeFrame();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 1);

            tf.setTimeFrameID("tf-id-fingertapping");
            tf.setDay(cal.get(Calendar.DAY_OF_MONTH));
            tf.setMonth(cal.get(Calendar.MONTH) + 1);
            tf.setYear(cal.get(Calendar.YEAR));
            tf.setStart(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            cal.add(Calendar.MINUTE, 60);
            tf.setEnd(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
            tf.setNotification(new Notification("WildKey", "You have a task to do!"));
            ArrayList<String> task = new ArrayList<>();
            task.add("alternate-finger-tapping-demo");
            tf.setTasks(task);
            tf.setAlarm(getApplicationContext());

            Prompt p = new Prompt();
            p.setType("alternate-finger-tapping");
            p.setPromptId("alternate-finger-tapping-demo");
            p.setTimeFrame(tf);
            ScheduleController.getInstance().enqueue(p, "demo");
            ScheduleController.getInstance().putTimeFrame("tf-id-fingertapping", tf);
        });

//        findViewById(R.id.bt_clear_all).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DataBaseFacade.getInstance().cleanTasks();
//            }
//        });

        findViewById(R.id.bt_save).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("text/json");
            i.putExtra(Intent.EXTRA_TITLE, "metrics.json");
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    i.putExtra(DocumentsContract.EXTRA_INITIAL_URI, new URI("/Wildkey/"));
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivityForResult(i, 1993);
        });
        findViewById(R.id.bt_delete).setOnClickListener(v -> {
            File f = new File(getFilesDir().getAbsolutePath(), "metrics.json");
            if(f.exists()){
                boolean b = f.delete();
                if(b){
                    Toast.makeText(getApplicationContext(), "Internal file deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Something went wrong deleting internal file", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Internal file doesn't exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        DataBaseFacade.getInstance().setDemo(false);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == 1993
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    File f = new File(getFilesDir().getAbsolutePath(), "metrics.json");
                    if(f.exists()){
                        StringBuilder stringBuilder = new StringBuilder();

                        BufferedReader reader = new BufferedReader(new FileReader(new File(getFilesDir().getAbsolutePath(), "metrics.json")));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }

                        ParcelFileDescriptor pfd = getContentResolver().
                                openFileDescriptor(uri, "w");
                        FileOutputStream fileOutputStream =
                                new FileOutputStream(pfd.getFileDescriptor());
                        fileOutputStream.write((stringBuilder.toString()).getBytes());
                        // Let the document provider know you're done by closing the stream.
                        fileOutputStream.close();
                        pfd.close();
                    }else {
                        Toast.makeText(getApplicationContext(), "Internal file doesn't exists", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Perform operations on the document using its URI.
            }
        }
    }

    private void startCooldown(){
        COOLDOWN = true;
        new Handler().postDelayed(() -> COOLDOWN = false, 90 * 1000);
    }
}
