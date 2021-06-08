package pt.lasige.inputmethod.study.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pt.lasige.inputmethod.latin.settings.SettingsLauncherActivityUI;
import pt.lasige.inputmethod.logger.DataBaseFacade;
import pt.lasige.inputmethod.logger.data.Config;
import pt.lasige.inputmethod.logger.data.Prompt;
import pt.lasige.inputmethod.logger.data.TimeFrame;
import pt.lasige.inputmethod.study.adapters.PromptLauncherUIController;

public class ScheduleController {
    private static ScheduleController instance;

    boolean ignoreDates = true;

    Config config;
    public ArrayList<String> queue = new ArrayList<>();
    ArrayList<PendingIntent> alarms = new ArrayList<>();
    HashMap<String, Prompt> prompts = new HashMap<>();
    HashMap<String, Prompt> singlePrompts = new HashMap<>();
    HashMap<String, Prompt> questions = new HashMap<>();
    HashMap<String, TimeFrame> timeFrames = new HashMap<>();
    PromptLauncherUIController promptUiController;
    SettingsLauncherActivityUI settingsUiController;

    //for demo only
    ArrayList<String> questionList = new ArrayList<>();
    int questionListIndex = 0;

    public static ScheduleController getInstance(){
        if(instance == null)
            instance = new ScheduleController();

        return instance;
    }

    public ScheduleController() {}

    public Config getConfig() {
        if(config == null){
            Config c = new Config();
            c.setStudyId("noConfigId");
            return c;
        }else
            return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getNextQuestionDate() {

        String start;
        Date dStart, dNow = Calendar.getInstance().getTime();
        long dif = Long.MAX_VALUE;
        String nextQuest = "";
        String nextDate = "";

        for (TimeFrame tf: timeFrames.values()){
            start = tf.getStart();
            dStart = getDate(Integer.parseInt(start.split(":")[0]),
                    Integer.parseInt(start.split(":")[1]),
                    tf.getDay(),
                    tf.getMonth(),
                    tf.getYear());

            if(dNow.before(dStart)) {
                if(dif > (dStart.getTime() - dNow.getTime())){
                    dif = dStart.getTime() - dNow.getTime();
                    nextQuest = tf.getTimeFrameID() + "_" + tf.getTasks().get(0);
                    nextDate = tf.getStart() + ", " + tf.getDay() + "/" + tf.getMonth() + "/" + tf.getYear();
                }
            }
        }

        return nextDate;
    }

    public ArrayList<String> getAllTasks() {
        ArrayList<String> result = new ArrayList<>();
        for (TimeFrame tf: timeFrames.values()) {
            for (String s : tf.getTasks()) {
                result.add(tf.getTimeFrameID() + "_" + s);
            }
        }
        return result;
    }

    public ArrayList<String> getQueue() {

        ArrayList<String> result = new ArrayList<>();
        String start, end;
        Date dStart, dEnd, dNow = Calendar.getInstance().getTime();

        for (TimeFrame tf: timeFrames.values()){
            if (ignoreDates){
                for (String s: tf.getTasks()){
                    Log.d("QUEUE", "inside  " + tf.getTimeFrameID()+"_"+s);
                    if(queue.contains(tf.getTimeFrameID()+"_"+s))
                        result.add(tf.getTimeFrameID()+"_"+s);
                }
            }else {
                start = tf.getStart();
                end = tf.getEnd();
                dStart = getDate(Integer.parseInt(start.split(":")[0]),
                        Integer.parseInt(start.split(":")[1]),
                        tf.getDay(),
                        tf.getMonth(),
                        tf.getYear());
                dEnd = getDate(Integer.parseInt(end.split(":")[0]),
                        Integer.parseInt(end.split(":")[1]),
                        tf.getDay(),
                        tf.getMonth(),
                        tf.getYear());

                if(dNow.after(dStart) && dNow.before(dEnd)) {
                    for (String s: tf.getTasks()){
                        if(queue.contains(tf.getTimeFrameID()+"_"+s))
                            result.add(tf.getTimeFrameID()+"_"+s);
                    }
                }
            }
        }

        Log.d("QUEUE", "GET QUEUE  " + queue.toString());
        Log.d("QUEUE", "GET result " + result.toString());
        return result;
    }

    public void setQueue(ArrayList<String> queue) {
        this.queue = queue;

        if(promptUiController != null)
            promptUiController.setData(getQueue());

        if(settingsUiController != null)
            settingsUiController.refresh(getQueue());
    }

    public Prompt getPrompt(String promptID){
        return singlePrompts.get(promptID);
    }

    public HashMap<String, Prompt> getPrompts() {
        return prompts;
    }

    public void enqueue(Prompt p, String parent){

        if(queue.contains(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId()))
            return;

        if(p.getType().startsWith("questionnaire_")){
            String id;
            if(parent != null)
                id = p.getTimeFrame().getTimeFrameID() + "_" + parent + "_" + p.getPromptId();
            else
                id = p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId();

            questions.put(p.getPromptId(), p);

        }else if(p.getType().equals("questionnaire")){
            for(String promptID: p.getQuestions())
                DataBaseFacade.getInstance().getPrompts(promptID, p.getPromptId(), config.getStudyId(), p.getTimeFrame());

            queue.add(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId());
            singlePrompts.put(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId(), p);
        }else {
            queue.add(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId());
            singlePrompts.put(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId(), p);
        }
        if(promptUiController != null)
            promptUiController.setData(getQueue());

        if(settingsUiController != null)
            settingsUiController.refresh(getQueue());
        prompts.put(p.getPromptId(), p);

    }

    public void enqueue(String promptID, String parent, String studyID, TimeFrame timeFrame){

        Prompt p = prompts.get(promptID);

        if(p == null)
            return;

        if(queue.contains(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId()))
            return;

        if(p.getType().startsWith("questionnaire_")){
//            String id;
//            if(parent != null)
//                id = p.getTimeFrameID() + "_" + parent + "_" + p.getPromptId();
//            else
//                id = p.getTimeFrameID() + "_" + p.getPromptId();
//
            questions.put(p.getPromptId(), p);
        }else if(p.getType().equals("questionnaire")){
            for(String pID: p.getQuestions())
                DataBaseFacade.getInstance().getPrompts(pID, promptID, config.getStudyId(), timeFrame);

            queue.add(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId());
            singlePrompts.put(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId(), p);
        }else {
            queue.add(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId());
            singlePrompts.put(p.getTimeFrame().getTimeFrameID() + "_" + p.getPromptId(), p);
        }
        if(promptUiController != null)
            promptUiController.setData(getQueue());

        if(settingsUiController != null)
            settingsUiController.refresh(getQueue());

    }

    //for demo only
    public void putQuestion(String id, Prompt p){
        questions.put(id, p);
    }
    public void putQuestionOnList(String id){
        questionList.add(id);
    }
    //for demo only
    public String getNextQuestionID(){
        try {
            return questionList.get(questionListIndex++);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public Prompt getQuestion(String id){
        return questions.get(id);
    }

    public String getType(String id){
        try {
            return singlePrompts.get(id).getType();
        }catch (Exception e){
            return "null";
        }
    }

    public void dequeue(String questionID) {

        queue.remove(questionID);
        singlePrompts.remove(questionID);

        if(promptUiController != null)
            promptUiController.setData(getQueue());

        if(settingsUiController != null)
            settingsUiController.refresh(getQueue());

    }

    public PromptLauncherUIController getPromptUiController() {
        return promptUiController;
    }

    public void setPromptUiController(PromptLauncherUIController promptUiController) {
        this.promptUiController = promptUiController;
    }

    public void setSettingsUiController(SettingsLauncherActivityUI settingsUiController) {
        this.settingsUiController = settingsUiController;
    }

    public Prompt getAtomicPrompt(String id){
        return prompts.get(id);
    }

    public void putTimeFrame(String id, TimeFrame tf){
        timeFrames.put(id, tf);
    }

    public TimeFrame getTimeFrame(String id){
        return timeFrames.get(id);
    }

    private int translateDayOfTheWeek(int dayOfTheWeek){
        switch (dayOfTheWeek){
            case Calendar.SUNDAY:
                return 0;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            default:
                return -1;
        }
    }

    private Date getDate(int hour, int minute, int day, int month, int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    public void addAlarm(PendingIntent pi){
        alarms.add(pi);
    }

    public void cancelAlarm(Context context, PendingIntent pi){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pi);
    }

    public void cancelAllAlarms(Context context){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (PendingIntent pi: alarms) {
            manager.cancel(pi);
        }

        alarms.clear();
    }

    public void cleanVars(){
        queue = new ArrayList<>();
        alarms = new ArrayList<>();
        prompts = new HashMap<>();
        singlePrompts = new HashMap<>();
        questions = new HashMap<>();
        timeFrames = new HashMap<>();
        questionList = new ArrayList<>();
        questionListIndex = 0;
    }
}
