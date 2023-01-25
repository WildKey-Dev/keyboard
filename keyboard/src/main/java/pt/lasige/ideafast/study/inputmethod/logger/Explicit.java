package pt.lasige.ideafast.study.inputmethod.logger;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Explicit {

    private static Explicit instance;
    private boolean on;
    private ValueEventListener listener;
    public static Explicit getInstance() {

        if(instance == null)
            instance = new Explicit();

        return instance;
    }

    public void setOn(boolean on) {
        Log.d("Explicit", "set: " + on);
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    private Explicit(){
        on = false;
    }

    public void setListener() {
        if (listener == null){
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean explicit = false;

                    if(snapshot.getValue() != null)
                        explicit = (boolean) snapshot.getValue();

                    setOn(explicit);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    setOn(false);
                }
            };
            DataBaseFacade.getInstance().setExplicitListener(listener);
        }
    }
}
