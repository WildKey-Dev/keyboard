package pt.lasige.inputmethod.logger;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import pt.lasige.inputmethod.logger.data.CompletedTask;
import pt.lasige.inputmethod.logger.data.Config;
import pt.lasige.inputmethod.logger.data.Prompt;
import pt.lasige.inputmethod.logger.data.TimeFrame;
import pt.lasige.inputmethod.study.scheduler.ScheduleController;

public class FirebaseController {
    private static final String TAG = "FirebaseController";
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private String configID;
    private String localUserID;

    public FirebaseController() {
        this.mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = this.mAuth.getCurrentUser();

        if (user != null){
            if(user.isAnonymous()){
                this.database = FirebaseDatabase.getInstance("https://cns-study.europe-west1.firebasedatabase.app/");
            }
//            else {
//                this.database = FirebaseDatabase.getInstance();
//            }
        }else {
            anonymousLogin(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success");
                    changeDatabase("https://cns-study.europe-west1.firebasedatabase.app/");
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                }
            });
        }
    }

    /**
     *
     * @param url if url == null database will bew the default
     */
    public void changeDatabase(String url){
        Log.d(TAG, "changeDatabase: url " + url);
        if(url != null){
            this.database = FirebaseDatabase.getInstance(url);
        }else {
            this.database = FirebaseDatabase.getInstance();
        }
    }

    public void setConfigIDListener(Context context){
        if (configID != null)
            return;

        getConfig(context);
    }

    public void setLocalUserID(String localUserID) {
        this.localUserID = localUserID;
    }

    public void getConfig(Context context){
        if(this.mAuth.getCurrentUser() != null){
            database.getReference("/users/"+this.mAuth.getCurrentUser().getUid()+"/config/").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try {
                        Config c = dataSnapshot.getValue(Config.class);
                        if (c != null) {
                            ScheduleController.getInstance().cancelAllAlarms(context);
                            for(TimeFrame timeFrame : c.getTimeFrames()){
                                ScheduleController.getInstance().putTimeFrame(timeFrame.getTimeFrameID(), timeFrame);
                                timeFrame.setAlarm(context);
                                for (String task: timeFrame.getTasks()) {
                                    getPrompts(task, null, c.getStudyId(), timeFrame, false);
                                }
                            }
                            ScheduleController.getInstance().setConfig(c);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    public void getPrompts(String promptID, String parent, String studyID, TimeFrame timeFrame, boolean isQuestion){

        //check if we already get it
        if(ScheduleController.getInstance().getAtomicPrompt(promptID) != null){
            ScheduleController.getInstance().enqueue(promptID, parent, studyID, timeFrame);
        }else {
            database.getReference("/prompts/").child(promptID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Prompt p = dataSnapshot.getValue(Prompt.class);
                    if(p != null){
                        p.setTimeFrame(timeFrame);
                        String ref;
                        if(parent != null)
                            if(isQuestion)
                                ref = "/users/"+DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+timeFrame.getTimeFrameID()+"_"+parent+"/"+p.getPromptId()+"/";
                            else //this is probably never reached
                                ref = "/users/"+DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+timeFrame.getTimeFrameID()+"_"+parent+"_"+p.getPromptId()+"/";
                        else
                            ref = "/users/"+DataBaseFacade.getInstance().getUserID()+"/completedTasks/"+studyID+"/"+timeFrame.getTimeFrameID()+"_"+p.getPromptId()+"/";

                        database.getReference(ref).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                int tasksToDo = 0;
                                if(p.getQuestions() != null)
                                    tasksToDo = p.getQuestions().size();
                                if(p.getPhrases() != null)
                                    tasksToDo = p.getPhrases().size();

                                CompletedTask ct = snapshot.getValue(CompletedTask.class);

                                if(ct != null && ct.isFinished())
                                    return;

                                if(ct != null && ct.getPhrases() == null){
                                    ScheduleController.getInstance().enqueue(p, parent);
                                }else if(ct != null && ct.getPhrases() != null && tasksToDo > ct.getPhrases().size()){
                                    ScheduleController.getInstance().enqueue(p, parent);
                                }else if (snapshot.getValue() == null){
                                    ScheduleController.getInstance().enqueue(p, parent);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "Failed to read value.", databaseError.toException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }
    }

    public String getConfigID() {
        return configID;
    }

    public void setConfigID(Context context, String configID, ConfigCallback callback) {
        getConfig(context, configID, callback);
    }

    public void getConfig(Context context, String newConfigID, ConfigCallback callback){
        if(this.mAuth.getCurrentUser() != null){
            database.getReference("/config/"+newConfigID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try {
                        Config c = dataSnapshot.getValue(Config.class);
                        if (c != null) {
                            ScheduleController.getInstance().cancelAllAlarms(context);
                            ScheduleController.getInstance().cleanVars();
                            for(TimeFrame timeFrame : c.getTimeFrames()){
                                ScheduleController.getInstance().putTimeFrame(timeFrame.getTimeFrameID(), timeFrame);
                                timeFrame.setAlarm(context);
                                for (String task: timeFrame.getTasks()) {
                                    getPrompts(task, null, c.getStudyId(), timeFrame, false);
                                }
                            }
                            ScheduleController.getInstance().setConfig(c);
                            configID = newConfigID;
                            callback.onResponse(true);
                        }else {
                            callback.onResponse(false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }else {
            anonymousLogin(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success");
                    changeDatabase("https://cns-study.europe-west1.firebasedatabase.app/");
                    getConfig(context, configID, callback);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    Toast.makeText(context, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void write(String key, Object value, String path){
        // Write a message to the database
        try{
            DatabaseReference myRef = database.getReference(path);
            myRef.child(key).setValue(value);
        }catch (Exception e){
            e.printStackTrace();
            DatabaseReference myRef = database.getReference(path);
            myRef.child(key).setValue("invalid value");
        }
    }

    public void writeIfNotExists(String key, Object value, String path){
        DatabaseReference myRef = database.getReference(path);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(key))
                    write(key, value, path);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public String getUserID(){

        if (localUserID != null)
            return localUserID;
        else
        if(mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getUid();
        else
            return "no_uid";
    }

    public void firebaseAuthWithGoogle(Context context, String idToken, int densityDpi, int height, int width, boolean saveEmail) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.getResult() == null && !task1.isSuccessful()) {
                                            Log.w(TAG, "getInstanceId failed", task1.getException());
                                            return;
                                        }

                                        // Get new Instance ID token
                                        String token = task1.getResult().getToken();
                                        write("brand", Build.BRAND, "/users/"+user.getUid()+"/device/");
                                        write("device", Build.DEVICE, "/users/"+user.getUid()+"/device/");
                                        write("densityDpi", densityDpi, "/users/"+user.getUid()+"/device/display/");
                                        write("display", Build.DISPLAY, "/users/"+user.getUid()+"/device/display/");
                                        write("height", height, "/users/"+user.getUid()+"/device/display/");
                                        write("width", width, "/users/"+user.getUid()+"/device/display/");
                                        write("model", Build.MODEL, "/users/"+user.getUid()+"/device/");
                                        write("release", Build.VERSION.RELEASE, "/users/"+user.getUid()+"/device/");
                                        write("sdk", Build.VERSION.SDK_INT, "/users/"+user.getUid()+"/device/");
                                        write("fcmToken", token, "/users/"+user.getUid()+"/");
                                        write("id", user.getUid(), "/user-list/"+user.getUid()+"/");
                                        setConfigIDListener(context);
                                    });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    public void firebaseAuthAnonymous(Context context, String userID, int densityDpi, int height, int width) {

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){

            write("brand", Build.BRAND, "/users/"+user.getUid()+"/device/");
            write("device", Build.DEVICE, "/users/"+user.getUid()+"/device/");
            write("densityDpi", densityDpi, "/users/"+user.getUid()+"/device/display/");
            write("display", Build.DISPLAY, "/users/"+user.getUid()+"/device/display/");
            write("height", height, "/users/"+user.getUid()+"/device/display/");
            write("width", width, "/users/"+user.getUid()+"/device/display/");
            write("model", Build.MODEL, "/users/"+user.getUid()+"/device/");
            write("release", Build.VERSION.RELEASE, "/users/"+user.getUid()+"/device/");
            write("sdk", Build.VERSION.SDK_INT, "/users/"+user.getUid()+"/device/");
            write("id", user.getUid(), "/user-list/"+user.getUid()+"/");
            setConfigIDListener(context);

        }

    }

    public boolean setFCMToken(String token) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            write("fcmToken", token, "/users/"+user.getUid()+"/");
            return true;
        }else{
            return false;
        }
    }

    public void getCurrentPhrase(String studyID, String questionID, PhraseObserver obs) {
        database.getReference("/users/").child(getUserID()).child("completedTasks").child(studyID).child(questionID).child("phrases").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    size++;

                obs.onResponse(size);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void getTimeRemaining(String studyID, String questionID, PhraseObserver obs) {
        database.getReference("/users/").child(getUserID()).child("completedTasks").child(studyID).child(questionID).child("timeRemaining").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Long time = dataSnapshot.getValue(Long.class);

                if(time != null){
                    obs.onResponse(time);
                }else{
                    obs.onResponse(0);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void getCurrentQuestionnaireQuestion(String studyID, String questionnaireID, QuestionnaireObserver obs) {

        ArrayList<String> questionsDone = new ArrayList<>();
        database.getReference("/users/").child(getUserID()).child("completedTasks").child(studyID).child(questionnaireID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    obs.onResponse(questionnaireID);
                }else{
                    obs.onResponse("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void cleanTasks(){
        database.getReference("/users/").child(getUserID()).child("completedTasks").removeValue();
    }

    public void anonymousLogin(OnCompleteListener<AuthResult> listener) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            mAuth.signInAnonymously().addOnCompleteListener(listener);
        }
    }

    public void deleteConfig() {

        for(Prompt prompt: ScheduleController.getInstance().getPrompts().values()){
            database.getReference("/users")
                    .child(getUserID())
                    .child("completedTasks")
                    .child(ScheduleController.getInstance().getConfig().getStudyId())
                    .child(prompt.getTimeFrame().getTimeFrameID()+"_"+prompt.getPromptId())
                    .removeValue();

            database.getReference("/users")
                    .child(getUserID())
                    .child("completedTasks")
                    .child(ScheduleController.getInstance().getConfig().getStudyId())
                    .child(prompt.getTimeFrame().getTimeFrameID()+"_"+prompt.getPromptId()+"-generated-target-phrase")
                    .removeValue();
        }

    }

    public void deleteConfig(int phrase) {
        phrase = phrase - 1;
        if(ScheduleController.getInstance().getQueue().size() > 0 &&
                phrase < ScheduleController.getInstance().getPrompt(ScheduleController.getInstance().getQueue().get(0)).getPhrases().size()){
            for (int i = 0; i < ScheduleController.getInstance().getPrompt(ScheduleController.getInstance().getQueue().get(0)).getPhrases().size(); i++) {
                if(i > phrase){
                    database.getReference("/users")
                            .child(getUserID())
                            .child("completedTasks")
                            .child(ScheduleController.getInstance().getConfig().getStudyId())
                            .child(ScheduleController.getInstance().getQueue().get(0))
                            .child("phrases")
                            .child(String.valueOf(i))
                            .removeValue();
                }
            }
        }

//            database.getReference("/users")
//                    .child(getUserID())
//                    .child("completedTasks")
//                    .child(ScheduleController.getInstance().getConfig().getStudyId())
//                    .child(ScheduleController.getInstance().getQueue().get(0))
//                    .child("phrases")
//                    .child(String.valueOf(phrase))
//                    .removeValue();

    }

    public interface ConfigCallback{
        void onResponse(boolean result);
    }


}
