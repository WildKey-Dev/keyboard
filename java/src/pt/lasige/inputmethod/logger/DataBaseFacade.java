package pt.lasige.inputmethod.logger;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import pt.lasige.inputmethod.logger.data.TimeFrame;

public class DataBaseFacade {

    private static DataBaseFacade instance;
    private FirebaseController fb;
    private String localUserID;
    private boolean demo = false;

    public static DataBaseFacade getInstance(){
        if(instance == null)
            instance = new DataBaseFacade();

        return instance;
    }

    private DataBaseFacade() {
        fb = new FirebaseController();
    }

    public String getLocalUserID() {
        return localUserID;
    }

    public FirebaseController getFb() {
        return fb;
    }

    public String getFbUserID() {
        FirebaseUser user = fb.getUser();
        if(user != null)
            return user.getUid();
        else
            return null;
    }

    public boolean isDemo() {
        return demo;
    }

    public void setDemo(boolean demo) {
        Log.d("DEMOMODE", "Demo mode: " + demo);
        this.demo = demo;
    }

    public void setLocalUserID(String localUserID) {
        this.localUserID = localUserID;
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

        writeIfNotExists("init", currentTimeMillis, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }

    public void setEndTS(String studyID, String questionID, long currentTimeMillis) {
        if (isDemo())
            return;

        write("end", currentTimeMillis, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
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

        writeIfNotExists("init", currentTimeMillis, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/interruptions/"+index+"/");
    }

    public void setInterruptionEndTS(String studyID, String questionID, int index, long currentTimeMillis) {
        if (isDemo())
            return;

        write("end", currentTimeMillis, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/interruptions/"+index+"/");
    }

    public void setTimeRemaining(String studyID, String questionID, long currentTimeMillis) {
        if (isDemo())
            return;

        write("timeRemaining", currentTimeMillis, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }

    public void setQuestionnaireCheckboxResponse(ArrayList<String> selected, String studyID, String questionID, String questionnaireID, long timeSpent) {
        if (isDemo())
            return;

        write("response", selected,  "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent,  "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireScaleResponse(int scale, String text, String studyID, String questionID, String questionnaireID, long timeSpent) {
        if (isDemo())
            return;

        write("scale", scale, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
        write("desc", text,  "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
        write("time", timeSpent, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/response/");
    }

    public void setQuestionnaireSeekBarResponse(int scale, String studyID, String questionID, String questionnaireID, long timeSpent) {
        if (isDemo())
            return;

        write("response", scale, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireRadioResponse(String response, String studyID, String questionID, String questionnaireID, long timeSpent) {
        if (isDemo())
            return;

        write("response", response, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireHourResponse(String hour, String studyID, String questionID, String questionnaireID, long timeSpent) {
        if (isDemo())
            return;

        write("response", hour, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        write("time", timeSpent, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void setQuestionnaireTextResponse(String text, String studyID, String questionID, String questionnaireID, long timeSpent) {
        write("time", timeSpent, "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
        if (isDemo())
            return;

        write("response", text,  "/users/"+ getFbUserID()+"/completedTasks/"+studyID+"/"+questionnaireID+"/"+questionID+"/");
    }

    public void getCurrentQuestionnaireQuestion(String studyID, String questionnaireID, QuestionnaireObserver obs) {
        fb.getCurrentQuestionnaireQuestion(studyID, questionnaireID, obs);
    }

    public void setFingerTapping(String studyID, String questionID, String finger, int right, int wrong) {
        if (isDemo())
            return;

        write("right", right, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
        write("wrong", wrong, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/"+finger+"/");
    }

    public void setFingerTappingAvg(String studyID, String questionID, double avg) {
        if (isDemo())
            return;

        write("avg", avg, "/users/"+ DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+questionID+"/");
    }

    public void cleanTasks(){
        fb.cleanTasks();
    }

    public void getPrompts(String promptID, String parentID, String studyID, TimeFrame timeframe) {
        fb.getPrompts(promptID, parentID, studyID, timeframe, true);
    }
}
