package pt.lasige.demo.inputmethod.study.questionnaire;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import com.quickbirdstudios.surveykit.FinishReason;
import com.quickbirdstudios.surveykit.OrderedTask;
import com.quickbirdstudios.surveykit.StepIdentifier;
import com.quickbirdstudios.surveykit.SurveyTheme;
import com.quickbirdstudios.surveykit.TaskIdentifier;
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration;
import com.quickbirdstudios.surveykit.result.QuestionResult;
import com.quickbirdstudios.surveykit.result.StepResult;
import com.quickbirdstudios.surveykit.result.TaskResult;
import com.quickbirdstudios.surveykit.steps.CompletionStep;
import com.quickbirdstudios.surveykit.steps.Step;
import com.quickbirdstudios.surveykit.survey.SurveyView;

import java.util.ArrayList;

import pt.lasige.demo.inputmethod.latin.R;
import pt.lasige.demo.inputmethod.logger.DataBaseFacade;
import pt.lasige.demo.inputmethod.logger.QuestionnaireObserver;
import pt.lasige.demo.inputmethod.logger.data.Prompt;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.Tuple;
import pt.lasige.demo.inputmethod.study.questionnaire.data.QuestionDataHolder;
import pt.lasige.demo.inputmethod.study.scheduler.ScheduleController;

public class QuestionnaireLauncherActivity extends Activity {
    int index = 0;
    String[] questionnaires;
    String questionID, questionnaireID, studyID;
    boolean started = false;
    ArrayList<Tuple> responses;
    ProgressDialog progressDialog;
    Handler timeOutHandler;
    SurveyView surveyView;
    ArrayList<Step> steps;

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

        setContentView(R.layout.activity_questionnaire);
        surveyView = findViewById(R.id.survey_view);

//        showProgressDialog();
//        QuestionnaireObserver obs = questionID -> {
//            if(questionID.isEmpty()){
//                dismissProgressDialog();
//                setContentView(R.layout.start_screen);
//                ((TextView) findViewById(R.id.tv_task)).setText(R.string.questionnaire_nothing_to_do);
//                ((Button) findViewById(R.id.bt_start)).setText(R.string.exit);
//                findViewById(R.id.bt_start).setOnClickListener(view1 -> finish());
//                ScheduleController.getInstance().dequeue(questionnaireID);
//            }else{
//                dismissProgressDialog();
//                setUpSurvey();
//            }
//        };
//        DataBaseFacade.getInstance().getCurrentQuestionnaireQuestion(studyID, questionnaireID, obs);

        setUpSurvey();
    }

    private void setUpSurvey() {

        CompletionStep completionStep = new CompletionStep(
                getString(R.string.done),
                getString(R.string.thank_you),
                getString(R.string.close),
                null,
                0,
                false,
                new StepIdentifier("COMPLETE"));

        steps = new ArrayList<>();
        Step s;
        while ((s = nextQuestion()) != null) {
            steps.add(s);
        };
        steps.add(completionStep);

        OrderedTask orderedTask = new OrderedTask(steps, new TaskIdentifier(questionnaireID));

        surveyView.setOnSurveyFinish((taskResult, finishReason) -> {
            onSurveyFinish(taskResult, finishReason);
            return null;
        });

        surveyView.start(orderedTask,
                new SurveyTheme(
                        getColor(R.color.study_accent_color),
                        getColor(R.color.study_accent_color),
                        getColor(R.color.study_accent_color),
                        new AbortDialogConfiguration(
                                R.string.leave,
                                R.string.leave_message,
                                R.string.leave_neutral_button,
                                R.string.leave_negative_button)
                )
        );
    }

    private Step nextQuestion(){

        if(questionnaires.length == index)
            return null;

        Step step = null;
        ArrayList<String> questions = new ArrayList<>();
        switch (questionnaires[index]){
            case "questionnaire_one_choice":
                questions.add("Less that one hour a day");
                questions.add("Between one and two hours a day");
                questions.add("Between two and three hours a day");
                questions.add("Between three and four hours a day");
                questions.add("Between four and five hours a day");
                questions.add("More than five hours a day");

                step = new SingleChoiceCustom(
                        "How many time do your phone?",
                        getString(R.string.next),
                        questions,
                        false,
                        new StepIdentifier("questionnaire_one_choice-demo"));

                break;
            case "questionnaire_open":
                step = new TextCustom(
                        "Do you any suggestions?",
                        getString(R.string.next),
                        false,
                        new StepIdentifier("questionnaire_open-demo"));
                break;
            case "questionnaire_select_scale":
                questions.add("1");
                questions.add("2");
                questions.add("3");
                questions.add("4");
                questions.add("5");
                questions.add("6");
                questions.add("7");

                step = new ScaleButtonCustom(
                        "How likely are you to recommend this monitoring approach to others?",
                        getString(R.string.next),
                        "Very Likely",
                        "Very Unlikely",
                        questions,
                        false,
                        new StepIdentifier("questionnaire_select_scale-demo"));

                break;
            case "questionnaire_slider_scale":

                step = new ScaleSliderCustom(
                        "How effective do you think this monitoring might be?",
                        getString(R.string.next),
                        "Very effective",
                        "Very ineffective",
                        ScaleSliderCustom.ORIENTATION.VERTICAL,
                        false,
                        new StepIdentifier("questionnaire_slider_scale-demo"));

                break;
            case "questionnaire_multiple_choice":
                questions.add("1");
                questions.add("2");
                questions.add("3");
                questions.add("4");
                questions.add("5");
                questions.add("6");
                questions.add("7");

                step = new MultiChoiceCustom(
                        "I would be willing to use this method (select all that apply)",
                        getString(R.string.next),
                        questions,
                        false,
                        new StepIdentifier("questionnaire_multiple_choice-demo"));
                break;
            case "questionnaire_hour":
                step = new TimePickerCustom(
                        "At what time did you had lunch?",
                        getString(R.string.next),
                        false,
                        new StepIdentifier("questionnaire_hour-demo"));
                break;
        }

        index++;
        return step;
    }

    private void finishQuestionnaire() {

        //write all responses on db
        //we writ all at once because of our verification on onCreate
        //this way we guarantee that the questionnaire is only saved if its finished
        save();

        ScheduleController.getInstance().dequeue(questionnaireID);

        ScheduleController.getInstance().dequeue(questionID);
        Handler handler=new Handler();
        Runnable r= () -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        };
        handler.postDelayed(r, 500);
    }

    private void onSurveyFinish(TaskResult taskResult, FinishReason finishReason){
        if(finishReason.toString().equals("Completed"))
            for(StepResult sr: taskResult.getResults()){
                for(QuestionResult qr: sr.getResults()){
                    switch (qr.getClass().getSimpleName()){
                        case "MultiChoiceCustomResult":
                            responses.add(
                                    new Tuple("questionnaire_multiple_choice",
                                            new QuestionDataHolder(
                                                    studyID,
                                                    qr.getId().getId(),
                                                    questionnaireID,
                                                    ((MultiChoiceCustom.MultiChoiceCustomResult) qr).getAnswer(),
                                                    qr.getEndDate().getTime() - qr.getStartDate().getTime())));
                            break;

                        case "SingleChoiceCustomResult":
                            responses.add(
                                    new Tuple("questionnaire_one_choice",
                                            new QuestionDataHolder(
                                                    studyID,
                                                    qr.getId().getId(),
                                                    questionnaireID,
                                                    ((SingleChoiceCustom.SingleChoiceCustomResult) qr).getAnswer(),
                                                    qr.getEndDate().getTime() - qr.getStartDate().getTime())));

                            break;

                        case "ScaleSliderCustomResult":
                            responses.add(
                                    new Tuple("questionnaire_slider_scale",
                                            new QuestionDataHolder(
                                                    studyID,
                                                    qr.getId().getId(),
                                                    questionnaireID,
                                                    Integer.parseInt(((ScaleSliderCustom.ScaleSliderCustomResult) qr).getAnswer()),
                                                    qr.getEndDate().getTime() - qr.getStartDate().getTime())));

                            break;

                        case "ScaleButtonCustomResult":
                            responses.add(
                                    new Tuple("questionnaire_select_scale",
                                            new QuestionDataHolder(
                                                    studyID,
                                                    qr.getId().getId(),
                                                    questionnaireID,
                                                    Integer.parseInt(((ScaleButtonCustom.ScaleButtonCustomResult) qr).getAnswer()),
                                                    qr.getEndDate().getTime() - qr.getStartDate().getTime())));

                            break;

                        case "TextCustomResult":
                            responses.add(
                                    new Tuple("questionnaire_open",
                                            new QuestionDataHolder(
                                                    studyID,
                                                    qr.getId().getId(),
                                                    questionnaireID,
                                                    ((TextCustom.TextCustomResult) qr).getAnswer(),
                                                    qr.getEndDate().getTime() - qr.getStartDate().getTime())));

                            break;

                        case "TimePickerCustomResult":
                            responses.add(
                                    new Tuple("questionnaire_hour",
                                            new QuestionDataHolder(
                                                    studyID,
                                                    qr.getId().getId(),
                                                    questionnaireID,
                                                    ((TimePickerCustom.TimePickerCustomResult) qr).getAnswer(),
                                                    qr.getEndDate().getTime() - qr.getStartDate().getTime())));
                            break;
                    }
                }
            }
        finishQuestionnaire();
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
        surveyView.backPressed();
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
}