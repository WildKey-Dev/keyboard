package pt.lasige.demo.inputmethod.logger;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import pt.lasige.demo.inputmethod.logger.data.CompletedTask;
import pt.lasige.demo.inputmethod.logger.data.Config;
import pt.lasige.demo.inputmethod.logger.data.KeyEventData;
import pt.lasige.demo.inputmethod.logger.data.MotionEventData;
import pt.lasige.demo.inputmethod.logger.data.Prompt;
import pt.lasige.demo.inputmethod.logger.data.TimeFrame;
import pt.lasige.demo.inputmethod.metrics.textentry.datastructures.Tuple;
import pt.lasige.demo.inputmethod.study.scheduler.ScheduleController;

public class FirebaseController {
    private static final String TAG = "FirebaseController";

    public FirebaseController() {
    }

    public void setConfigIDListener(Context context){
        getConfig(context);
    }

    public void getConfig(Context context){

    }

    public void getPrompts(String promptID, String parent, String studyID, TimeFrame timeFrame, boolean isQuestion){

        //check if we already get it
        if(ScheduleController.getInstance().getAtomicPrompt(promptID) != null){
            ScheduleController.getInstance().enqueue(promptID, parent, studyID, timeFrame);
        }
    }

    public JSONObject addToJSON(JSONObject object, String[] path, int index, String key, Object val) throws JSONException {

        if(index < path.length){

            if(object.isNull(path[index])){
                object.put(path[index], new JSONObject());
            }
            addToJSON(object.getJSONObject(path[index]), path, ++index, key, val);

        }else{
            if (val instanceof ArrayList){
                if(((ArrayList<?>) val).size() > 0){
                    if (((ArrayList<?>) val).get(0) instanceof Tuple){
                        JSONArray tupleArray = new JSONArray();
                        for(Tuple t: (ArrayList<Tuple>) val){
                            JSONObject tuple = new JSONObject();
                            if(t.t1 instanceof Tuple){
                                JSONObject tuple2 = new JSONObject();
                                tuple2.put("t1", ((Tuple) t.t1).t1);
                                tuple2.put("t2", ((Tuple) t.t1).t2);
                                tuple.put("t1", tuple2);
                            }else if(t.t1 instanceof MotionEventData){
                                tuple.put("t1", t.t1.toString());
                            }else if(t.t1 instanceof KeyEventData){
                                tuple.put("t1", t.t1.toString());
                            }else {
                                tuple.put("t1", t.t1);
                            }
                            tuple.put("t2", t.t2);
                            tupleArray.put(tuple);
                        }
                        object.put(key, tupleArray);
                    }
                }
            }else
                object.put(key, val);
        }

        return object;
    }

    public void write(String key, Object value, String path, String dirPath){

        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        JSONObject obj = readFileToJSON(dirPath);
        String[] data = path.split("/");

        try {
            addToJSON(obj, data, 0, key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        writeFileToJSON(obj, dirPath);

    }

    private JSONObject readFileToJSON(String dirPath){

        File dir = new File(dirPath);
        if(!dir.exists()) {
            return new JSONObject();
        }
        File jsonFile = new File(dir, "metrics.json");
        if(!jsonFile.exists()) {
            return new JSONObject();
        }

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private void writeFileToJSON(JSONObject object, String dirPath){

        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            File jsonFile = new File(dir, "metrics.json");
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
            Log.d("CAN", "can write: " + jsonFile.canWrite());
            writer.write(object.toString());
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeIfNotExists(String key, Object value, String path){
    }


    public boolean setFCMToken(String token) {
        return false;
    }

    public void getCurrentPhrase(String studyID, String questionID, PhraseObserver obs) {
    }

    public void getTimeRemaining(String studyID, String questionID, PhraseObserver obs) {
    }

    public void getCurrentQuestionnaireQuestion(String studyID, String questionnaireID, QuestionnaireObserver obs) {
    }

    public void cleanTasks(){
    }
}
