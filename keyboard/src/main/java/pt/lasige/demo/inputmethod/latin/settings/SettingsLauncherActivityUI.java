package pt.lasige.demo.inputmethod.latin.settings;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import pt.lasige.demo.inputmethod.latin.R;

public class SettingsLauncherActivityUI {

    TextView tasksBadgeTV;
    ConstraintLayout tasksBadge;

    public SettingsLauncherActivityUI(TextView tasksBadgeTV, ConstraintLayout tasksBadge) {
        this.tasksBadgeTV = tasksBadgeTV;
        this.tasksBadge = tasksBadge;
    }

    public void refresh(ArrayList<String> data){

        if(data.isEmpty()) {
            tasksBadge.setVisibility(View.GONE);
        }else {
            tasksBadge.setVisibility(View.VISIBLE);
            tasksBadgeTV.setText(String.valueOf(data.size()));
        }
    }
}
