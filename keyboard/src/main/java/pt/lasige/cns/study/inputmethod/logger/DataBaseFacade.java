package pt.lasige.cns.study.inputmethod.logger;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import java.util.ArrayList;

import pt.lasige.cns.study.inputmethod.logger.data.TimeFrame;
import pt.lasige.cns.study.inputmethod.study.questionnaire.data.Tap;

public class DataBaseFacade {

    private static DataBaseFacade instance;
    private FirebaseController fb;
    private boolean demo = false;

    public static DataBaseFacade getInstance(){
        if(instance == null)
            instance = new DataBaseFacade();

        return instance;
    }

    private DataBaseFacade() {
        fb = new FirebaseController();
    }

    public FirebaseController getFb() {
        return fb;
    }

    public String getUserID() {
        return fb.getUserID();
    }

    public boolean isDemo() {
        return demo;
    }

    public void setDemo(boolean demo) {
        Log.d("DEMOMODE", "Demo mode: " + demo);
        this.demo = demo;
    }

    public void setLocalUserID(String localUserID) {
        fb.setLocalUserID(localUserID);
    }

    public void write(String key, Object value, String path){

        if (!LoggerController.getInstance().isLog() || isDemo())
            return;

        fb.write(key, value, path);
    }

    public void writeIfNotExists(String key, Object value, String path){
        if (!LoggerController.getInstance().isLog() || isDemo())
            return;

        fb.writeIfNotExists(key, value, path);
    }

    public boolean setFCMToken(String token) {
        return fb.setFCMToken(token);
    }

    public void setInitTS(String studyID, String questionID, long currentTimeMillis) {
        if (isDemo())
            return;

        writeIfNotExists("init", currentTimeMillis, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }

    public void setEndTS(String studyID, String questionID, long currentTimeMillis) {
        if (isDemo())
            return;

        write("end", currentTimeMillis, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }

    public void getCurrentPhrase(String studyID, String questionID, PhraseObserver obs) {
        fb.getCurrentPhrase(studyID, questionID, obs);
    }

    public void getTimeRemaining(String studyID, String questionID, PhraseObserver obs) {
        fb.getTimeRemaining(studyID, questionID, obs);
    }

    public void setInterruptionInitTS(String studyID, String questionID, int index, long currentTimeMillis) {
        if (isDemo())
            return;

        writeIfNotExists("init", currentTimeMillis, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/interruptions/"+index+"/");
    }

    public void setInterruptionEndTS(String studyID, String questionID, int index, long currentTimeMillis) {
        if (isDemo())
            return;

        write("end", currentTimeMillis, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/interruptions/"+index+"/");
    }

    public void setTimeRemaining(String studyID, String questionID, long currentTimeMillis) {
        if (isDemo())
            return;

        write("timeRemaining", currentTimeMillis, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }


    public void setFinished(String studyID, String questionID, boolean finished) {
        if (isDemo())
            return;

        write("finished", finished, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }

    public void setQuestionnaireCheckboxResponse(ArrayList<String> selected, String studyID, String questionID, String questionnaireID, long start, long end, long timeSpent) {
        if (isDemo())
            return;

        write("type", "questionnaire_multiple_choice",  "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("response", selected,  "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent,  "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("start", start,  "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("end", end,  "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireScaleResponse(int scale, String studyID, String questionID, String questionnaireID, long start, long end, long timeSpent) {
        if (isDemo())
            return;

        write("type", "questionnaire_select_scale", "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
        write("scale", scale, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
        write("time", timeSpent, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
        write("start", start, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
        write("end", end, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
    }

    public void setQuestionnaireSeekBarResponse(int scale, String studyID, String questionID, String questionnaireID, long start, long end, long timeSpent) {
        if (isDemo())
            return;

        write("type", "questionnaire_slider_scale", "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("response", scale, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("start", start, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("end", end, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireRadioResponse(String response, String studyID, String questionID, String questionnaireID, long start, long end, long timeSpent) {
        if (isDemo())
            return;

        write("type", "questionnaire_one_choice", "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("response", response, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("star", start, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("end", end, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireHourResponse(String hour, String studyID, String questionID, String questionnaireID, long start, long end, long timeSpent) {
        if (isDemo())
            return;

        write("type", "questionnaire_hour", "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("response", hour, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("start", start, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("end", end, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireTextResponse(String text, String studyID, String questionID, String questionnaireID, long start, long end, long timeSpent) {
        if (isDemo())
            return;

        write("type", "questionnaire_open",  "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("response", text,  "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("start", start, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("end", end, "/users/"+ getUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void getCurrentQuestionnaireQuestion(String studyID, String questionnaireID, QuestionnaireObserver obs) {
        fb.getCurrentQuestionnaireQuestion(studyID, questionnaireID, obs);
    }

    public void setFingerTapping(String studyID, String questionID, String finger, int right, int wrong, long start, long end, ArrayList<Tap> taps) {
        if (isDemo())
            return;

        write("type", "alternate_finger_tapping", "/users/"+ DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
        write("onTarget", right, "/users/"+ DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
        write("offTarget", wrong, "/users/"+ DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
        write("start", start, "/users/"+ DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
        write("end", end, "/users/"+ DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
        write("taps", taps, "/users/"+ DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
    }

    public void setFingerTappingAvg(String studyID, String questionID, double avg) {
        if (isDemo())
            return;

        write("avg", avg, "/users/"+ DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }

    public void cleanTasks(){
        fb.cleanTasks();
    }

    public void getPrompts(String promptID, String parentID, String studyID, TimeFrame timeframe) {
        fb.getPrompts(promptID, parentID, studyID, timeframe, true);
    }

    public void anonymousLogin(OnCompleteListener<AuthResult> listener) {
        fb.anonymousLogin(listener);
    }

    public void setConfigID(Context context, String configID, FirebaseController.ConfigCallback callback){
        fb.setConfigID(context, configID, callback);
    }

    public void changeDatabase(String url){
        fb.changeDatabase(url);
    }

    public void deleteConfig(){
        fb.deleteConfig();
    }

    public void deleteConfig(int phrase){
        fb.deleteConfig(phrase);
    }
}
