package pt.lasige.inputmethod.study.questionnaire;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.logger.DataBaseFacade;
import pt.lasige.inputmethod.logger.QuestionnaireObserver;
import pt.lasige.inputmethod.logger.data.Prompt;
import pt.lasige.inputmethod.metrics.textentry.datastructures.Tuple;
import pt.lasige.inputmethod.study.questionnaire.data.QuestionDataHolder;
import pt.lasige.inputmethod.study.scheduler.ScheduleController;

public class QuestionnaireLauncherActivity extends Activity {
    int index = 0;
    String[] questionnaires;
    String questionID, questionnaireID, studyID;
    boolean started = false;
    boolean recordEndPauseTS = false;
    ArrayList<Tuple> responses;
    ProgressDialog progressDialog;
    Handler timeOutHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActionBar() != null)
            getActionBar().hide();
        Intent intent = getIntent();
        studyID = intent.getStringExtra("study-id");
        questionnaireID = intent.getStringExtra("question-id");
        questionnaires =  intent.getStringArrayExtra("questions");
        started = false;
        responses = new ArrayList<>();

        if (DataBaseFacade.getInstance().isDemo()){
            nextDemoQuestion();
        }else {
            showProgressDialog();
            QuestionnaireObserver obs = questionID -> {
                if(questionID.isEmpty()){
                    dismissProgressDialog();
                    setContentView(R.layout.start_screen);
                    ((TextView) findViewById(R.id.tv_task)).setText(R.string.questionnaire_nothing_to_do);
                    ((Button) findViewById(R.id.bt_start)).setText(R.string.exit);
                    findViewById(R.id.bt_start).setOnClickListener(view1 -> finish());
                    ScheduleController.getInstance().dequeue(questionnaireID);
                }else{
                    dismissProgressDialog();
                    nextQuestion();
                }
            };
            DataBaseFacade.getInstance().getCurrentQuestionnaireQuestion(studyID, questionnaireID, obs);
        }
    }

    private void nextQuestion(){

        if(questionnaires.length == responses.size()){
            finishQuestionnaire();
        }else{
            Prompt p = ScheduleController.getInstance().getQuestion(questionnaires[index++]);

            Intent newActivity = null;
            String[] scaleSteps, options;

            switch (p.getType()){
                case "questionnaire_one_choice":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireRadioActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    options = new String[p.getOptions().size()];
                    newActivity.putExtra("options", p.getOptions().toArray(options));
                    newActivity.putExtra("isLast", ((questionnaires.length-responses.size())==1));
                    break;
                case "questionnaire_open":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireTextActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("isLast", ((questionnaires.length-responses.size())==1));
                    break;
                case "questionnaire_select_scale":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireScaleActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("scale", p.getScale());
                    newActivity.putExtra("question", p.getQuestion());
                    scaleSteps = new String[p.getScaleSteps().size()];
                    newActivity.putExtra("scale-steps", p.getScaleSteps().toArray(scaleSteps));
                    newActivity.putExtra("isLast", ((questionnaires.length-responses.size())==1));
                    break;
                case "questionnaire_slider_scale":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireScaleRadioBtActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("higher_bound", p.getHigherBound());
                    newActivity.putExtra("lower_bound", p.getLowerBound());
                    newActivity.putExtra("max_scale", p.getMaxScale());
                    newActivity.putExtra("isLast", ((questionnaires.length-responses.size())==1));
                    break;
                case "questionnaire_multiple_choice":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireCheckboxActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    options = new String[p.getOptions().size()];
                    newActivity.putExtra("options", p.getOptions().toArray(options));
                    newActivity.putExtra("isLast", ((questionnaires.length-responses.size())==1));
                    break;
                case "questionnaire_hour":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireHourActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("isLast", ((questionnaires.length-responses.size())==1));
                    break;
            }
            if(newActivity != null) {
                startActivityForResult(newActivity, 1904);
            }
        }
    }

    private void finishQuestionnaire() {
        setContentView(R.layout.finish_screen);

        //write all responses on db
        //we writ all at once because of our verification on onCreate
        //this way we guarantee that the questionnaire is only saved if its finished
        save();

        ScheduleController.getInstance().dequeue(questionnaireID);

        ScheduleController.getInstance().dequeue(questionID);
        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        };
        handler.postDelayed(r, 1500);
    }

    private void save(){
        for (Tuple t: responses){
            QuestionDataHolder qdh = (QuestionDataHolder) t.t2;

            switch ((String) t.t1){
                case "questionnaire_one_choice":

                    DataBaseFacade.getInstance().setQuestionnaireRadioResponse(
                            qdh.getResponse(),
                            qdh.getStudyID(),
                            qdh.getQuestionID(),
                            qdh.getQuestionnaireID(),
                            qdh.getTimeSpent());

                    ScheduleController.getInstance().dequeue(qdh.getQuestionID());
                    break;

                case "questionnaire_open":

                    DataBaseFacade.getInstance().setQuestionnaireTextResponse(
                            qdh.getResponse(),
                            qdh.getStudyID(),
                            qdh.getQuestionID(),
                            qdh.getQuestionnaireID(),
                            qdh.getTimeSpent());

                    ScheduleController.getInstance().dequeue(qdh.getQuestionID());
                    break;
                case "questionnaire_select_scale":

                    DataBaseFacade.getInstance().setQuestionnaireScaleResponse(
                            qdh.getScale(),
                            qdh.getResponse(),
                            qdh.getStudyID(),
                            qdh.getQuestionID(),
                            qdh.getQuestionnaireID(),
                            qdh.getTimeSpent());

                    ScheduleController.getInstance().dequeue(qdh.getQuestionID());
                    break;

                case "questionnaire_slider_scale":

                    DataBaseFacade.getInstance().setQuestionnaireSeekBarResponse(
                            qdh.getScale(),
                            qdh.getStudyID(),
                            qdh.getQuestionID(),
                            qdh.getQuestionnaireID(),
                            qdh.getTimeSpent());

                    ScheduleController.getInstance().dequeue(qdh.getQuestionID());
                    break;

                case "questionnaire_multiple_choice":

                    DataBaseFacade.getInstance().setQuestionnaireCheckboxResponse(
                            qdh.getResponses(),
                            qdh.getStudyID(),
                            qdh.getQuestionID(),
                            qdh.getQuestionnaireID(),
                            qdh.getTimeSpent());

                    ScheduleController.getInstance().dequeue(qdh.getQuestionID());
                    break;

                case "questionnaire_hour":

                    DataBaseFacade.getInstance().setQuestionnaireHourResponse(
                            qdh.getResponse(),
                            qdh.getStudyID(),
                            qdh.getQuestionID(),
                            qdh.getQuestionnaireID(),
                            qdh.getTimeSpent());

                    ScheduleController.getInstance().dequeue(qdh.getQuestionID());
                    break;
            }
        }
    }

    private void nextDemoQuestion(){
        setContentView(R.layout.start_screen);
        ((TextView) findViewById(R.id.tv_task)).setText(R.string.questionnaire_desc);
        String nextDemoQuestion = ScheduleController.getInstance().getNextQuestionID();
        if(nextDemoQuestion != null){
            Prompt p = ScheduleController.getInstance().getQuestion(nextDemoQuestion);
            Intent newActivity = null;
            String[] scaleSteps, options;
            switch (p.getType()){
                case "questionnaire_one_choice":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireRadioActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    options = new String[p.getOptions().size()];
                    newActivity.putExtra("options", p.getOptions().toArray(options));
                    break;
                case "questionnaire_open":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireTextActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    break;
                case "questionnaire_select_scale":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireScaleActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("scale", p.getScale());
                    newActivity.putExtra("question", p.getQuestion());
                    scaleSteps = new String[p.getScaleSteps().size()];
                    newActivity.putExtra("scale-steps", p.getScaleSteps().toArray(scaleSteps));
                    break;
                case "questionnaire_slider_scale":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireScaleRadioBtActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    newActivity.putExtra("higher_bound", p.getHigherBound());
                    newActivity.putExtra("lower_bound", p.getLowerBound());
                    newActivity.putExtra("max_scale", p.getMaxScale());
                    break;
                case "questionnaire_multiple_choice":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireCheckboxActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    options = new String[p.getOptions().size()];
                    newActivity.putExtra("options", p.getOptions().toArray(options));
                    break;
                case "questionnaire_hour":
                    newActivity = new Intent(getApplicationContext(), QuestionnaireHourActivity.class);
                    newActivity.putExtra("study-id", ScheduleController.getInstance().getConfig().getStudyId());
                    newActivity.putExtra("question-id", p.getPromptId());
                    newActivity.putExtra("questionnaire-id", questionnaireID);
                    newActivity.putExtra("question", p.getQuestion());
                    break;
            }
            if(newActivity != null) {
                Intent finalNewActivity = newActivity;// this is ridiculous!
                findViewById(R.id.bt_start).setOnClickListener(view1 -> startActivityForResult(finalNewActivity, 1904));
            }
        }else{
            ((TextView) findViewById(R.id.tv_task)).setText(R.string.questionnaire_nothing_to_do);
            ((Button) findViewById(R.id.bt_start)).setText(R.string.exit);
            findViewById(R.id.bt_start).setOnClickListener(view1 -> finish());
        }

    }

    private void showProgressDialog() {
        if(progressDialog != null)
            dismissProgressDialog();

        progressDialog = new ProgressDialog(QuestionnaireLauncherActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setMax(100);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.loading_next_question));
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(started){
            DataBaseFacade.getInstance().setInterruptionEndTS(studyID, questionID, this.index-1, System.currentTimeMillis());
        }
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1904) {
            if(resultCode == Activity.RESULT_OK){
                if (DataBaseFacade.getInstance().isDemo()){
                    nextDemoQuestion();
                }else{

                    switch (data.getStringExtra("type")){
                        case "questionnaire_one_choice":
                        case "questionnaire_open":
                        case "questionnaire_hour":

                            responses.add(
                                    new Tuple(data.getStringExtra("type"),
                                            new QuestionDataHolder(data.getStringExtra("studyID"),
                                                    data.getStringExtra("questionID"),
                                                    data.getStringExtra("questionnaireID"),
                                                    data.getStringExtra("response"),
                                                    data.getLongExtra("time", -1))));

                            break;

                        case "questionnaire_select_scale":

                            responses.add(
                                    new Tuple(data.getStringExtra("type"),
                                            new QuestionDataHolder(data.getStringExtra("studyID"),
                                                    data.getStringExtra("questionID"),
                                                    data.getStringExtra("questionnaireID"),
                                                    data.getIntExtra("scale", -1),
                                                    data.getStringExtra("desc"),
                                                    data.getLongExtra("time", -1))));

                            break;

                        case "questionnaire_slider_scale":

                            responses.add(
                                    new Tuple(data.getStringExtra("type"),
                                            new QuestionDataHolder(data.getStringExtra("studyID"),
                                                    data.getStringExtra("questionID"),
                                                    data.getStringExtra("questionnaireID"),
                                                    data.getIntExtra("scale", -1),
                                                    data.getLongExtra("time", -1))));

                            break;

                        case "questionnaire_multiple_choice":

                            responses.add(
                                    new Tuple(data.getStringExtra("type"),
                                            new QuestionDataHolder(data.getStringExtra("studyID"),
                                                    data.getStringExtra("questionID"),
                                                    data.getStringExtra("questionnaireID"),
                                                    data.getStringArrayListExtra("responses"),
                                                    data.getLongExtra("time", -1))));

                            break;

                    }
                    nextQuestion();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }
}