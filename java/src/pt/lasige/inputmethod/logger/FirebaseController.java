package pt.lasige.inputmethod.logger;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
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
import java.util.Arrays;
import java.util.HashMap;

import pt.lasige.inputmethod.logger.data.CompletedTask;
import pt.lasige.inputmethod.logger.data.Config;
import pt.lasige.inputmethod.logger.data.Prompt;
import pt.lasige.inputmethod.logger.data.TimeFrame;
import pt.lasige.inputmethod.study.scheduler.ScheduleController;

public class FirebaseController {
    private static final String TAG = "FirebaseController";
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    public FirebaseController() {
        this.database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
//        this.database.setPersistenceEnabled(true);
    }

    public void setConfigIDListener(Context context){
        getConfig(context);
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
                                ref = "/users/"+DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+timeFrame.getTimeFrameID()+"_"+parent+"/"+p.getPromptId()+"/";
                            else //this is probably never reached
                                ref = "/users/"+DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+timeFrame.getTimeFrameID()+"_"+parent+"_"+p.getPromptId()+"/";
                        else
                            ref = "/users/"+DataBaseFacade.getInstance().getFbUserID()+"/completedTasks/"+studyID+"/"+timeFrame.getTimeFrameID()+"_"+p.getPromptId()+"/";

                        database.getReference(ref).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                int tasksToDo = 0;
                                if(p.getQuestions() != null)
                                    tasksToDo = p.getQuestions().size();
                                if(p.getPhrases() != null)
                                    tasksToDo = p.getPhrases().size();

                                CompletedTask ct = snapshot.getValue(CompletedTask.class);

                                if(ct != null && ct.getPhrases() != null && tasksToDo > ct.getPhrases().size()){
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

    public FirebaseUser getUser(){
        return mAuth.getCurrentUser();
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

                                        if(saveEmail){
                                            write("email", user.getEmail(), "/users/"+user.getUid()+"/");
                                            write("name", user.getDisplayName(), "/users/"+user.getUid()+"/");
                                        }

                                        write("fcmToken", token, "/users/"+user.getUid()+"/");
                                        setConfigIDListener(context);
                                    });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
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
        database.getReference("/users/").child(getUser().getUid()).child("completedTasks").child(studyID).child(questionID).child("phrases").addListenerForSingleValueEvent(new ValueEventListener() {
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
        database.getReference("/users/").child(getUser().getUid()).child("completedTasks").child(studyID).child(questionID).child("timeRemaining").addListenerForSingleValueEvent(new ValueEventListener() {
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
        database.getReference("/users/").child(getUser().getUid()).child("completedTasks").child(studyID).child(questionnaireID).addListenerForSingleValueEvent(new ValueEventListener() {
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
        database.getReference("/users/").child(getUser().getUid()).child("completedTasks").removeValue();
    }
}
