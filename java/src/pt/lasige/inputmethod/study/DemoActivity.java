package pt.lasige.inputmethod.study;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.logger.DataBaseFacade;
import pt.lasige.inputmethod.logger.data.Prompt;
import pt.lasige.inputmethod.logger.data.TimeFrame;
import pt.lasige.inputmethod.study.questionnaire.QuestionnaireLauncherActivity;
import pt.lasige.inputmethod.study.scheduler.ScheduleController;

public class DemoActivity extends Activity {

    boolean DEMOMODE = true;
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
        ((Spinner) findViewById(R.id.sp_transcription)).setAdapter(adapterTranscription);

        ArrayAdapter<String> adapterComposition = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerComposition);
        adapterComposition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.sp_composition)).setAdapter(adapterComposition);

        ArrayAdapter<String> adapterQuestionnaire = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerQuestionnaire);
        adapterQuestionnaire.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.sp_questionnaire)).setAdapter(adapterQuestionnaire);

        ArrayAdapter<String> adapterFinger = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerFinger);
        adapterFinger.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.sp_fingertapping)).setAdapter(adapterFinger);

        if(DEMOMODE){
            ((Spinner) findViewById(R.id.sp_transcription)).setVisibility(View.GONE);
            ((Spinner) findViewById(R.id.sp_composition)).setVisibility(View.GONE);
            ((Spinner) findViewById(R.id.sp_questionnaire)).setVisibility(View.GONE);
            ((Spinner) findViewById(R.id.sp_fingertapping)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.bt_clear_all)).setVisibility(View.GONE);
        }

        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(DemoActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Getting tasks");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/prompts/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    Prompt p = data.getValue(Prompt.class);
                    if (p != null) {

                        TimeFrame tf = new TimeFrame();
                        tf.setDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        tf.setMonth(Calendar.getInstance().get(Calendar.MONTH));
                        tf.setYear(Calendar.getInstance().get(Calendar.YEAR));
                        tf.setStart("1:00");
                        tf.setEnd("23:00");
                        p.setTimeFrame(tf);
                        prompts.put(p.getPromptId(), p);

                        switch (p.getType()){
                            case "transcription":
                                spinnerTranscription.add(p.getPromptId());
                                adapterTranscription.notifyDataSetChanged();
                                break;
                            case "composition":
                                spinnerComposition.add(p.getPromptId());
                                adapterComposition.notifyDataSetChanged();
                                break;
                            case "questionnaire":
                                spinnerQuestionnaire.add(p.getPromptId());
                                adapterQuestionnaire.notifyDataSetChanged();
                                if(p.getPromptId().equals("6vP6dUUSSv3znbYoCuReyGyoJwYWzx")){
                                    for (String questionID: p.getQuestions())
                                        ScheduleController.getInstance().putQuestionOnList(questionID);
                                }
                                break;
                            case "alternate-finger-tapping":
                                spinnerFinger.add(p.getPromptId());
                                adapterFinger.notifyDataSetChanged();
                                break;

                            case "questionnaire_slider_scale":
                            case "questionnaire_hour":
                            case "questionnaire_one_choice":
                            case "questionnaire_select_scale":
                            case "questionnaire_multiple_choice":
                            case "questionnaire_open":
                                ScheduleController.getInstance().putQuestion(p.getPromptId(), p);
                                break;
                        }
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        findViewById(R.id.bt_transcription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DEMOMODE)
                    p = prompts.get("UvftYx3tHauX6gf3S8Mh");
                else
                    p = prompts.get(((Spinner) findViewById(R.id.sp_transcription)).getSelectedItem().toString());
                intent = new Intent(getApplicationContext(), TranscriptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", "demo");
                intent.putExtra("question-id", p.getPromptId());
                intent.putExtra("sub-type", p.getSubType());
                if(p.getSubType().equals("time"))
                    intent.putExtra("duration", p.getDuration());
                String[] phrases = new String[p.getPhrases().size()];
                intent.putExtra("phrases", p.getPhrases().toArray(phrases));
                startActivity(intent);
            }
        });
        findViewById(R.id.bt_composition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DEMOMODE)
                    p = prompts.get("4wm8mDy2BEZmTUeP6ETJ");
                else
                    p = prompts.get(((Spinner) findViewById(R.id.sp_composition)).getSelectedItem().toString());

                intent = new Intent(getApplicationContext(), CompositionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", "demo");
                intent.putExtra("question-id", p.getPromptId());
                intent.putExtra("sub-type", p.getSubType());
                if(p.getSubType().equals("time"))
                    intent.putExtra("duration", p.getDuration());
                String[] mQuestions = new String[p.getQuestions().size()];
                intent.putExtra("questions", p.getQuestions().toArray(mQuestions));
                startActivity(intent);
            }
        });
        findViewById(R.id.bt_questionnaire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DEMOMODE)
                    p = prompts.get("6vP6dUUSSv3znbYoCuReyGyoJwYWzx");
                else
                    p = prompts.get(((Spinner) findViewById(R.id.sp_questionnaire)).getSelectedItem().toString());

                intent = new Intent(getApplicationContext(), QuestionnaireLauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", "demo");
                intent.putExtra("question-id", p.getPromptId());
                String[] questions = new String[p.getQuestions().size()];
                intent.putExtra("questions", p.getQuestions().toArray(questions));
                startActivity(intent);
            }
        });
        findViewById(R.id.bt_fingertapping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DEMOMODE)
                    p = prompts.get("29b2ZpngW84477WEen35DAbsp");
                else
                    p = prompts.get(((Spinner) findViewById(R.id.sp_fingertapping)).getSelectedItem().toString());

                intent = new Intent(getApplicationContext(), AlternateFingerTappingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("study-id", "demo");
                intent.putExtra("question-id", p.getPromptId());
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseFacade.getInstance().cleanTasks();
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

}
