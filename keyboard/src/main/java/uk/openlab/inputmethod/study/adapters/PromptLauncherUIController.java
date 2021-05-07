package uk.openlab.inputmethod.study.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import uk.openlab.inputmethod.latin.R;
import uk.openlab.inputmethod.study.scheduler.ScheduleController;

public class PromptLauncherUIController {

    Activity context;
    ArrayList<String> data;
    Button bt;
    TextView title, message;

    public PromptLauncherUIController(Activity context, ArrayList<String> data, TextView title, TextView message, Button bt){

        this.bt = bt;
        this.data = data;
        this.title = title;
        this.message = message;
        if(this.data.isEmpty()) {
            bt.setText(R.string.exit);
            title.setText(String.format(context.getString(R.string.pending_tasks), "0"));
            message.setVisibility(View.VISIBLE);
            message.setText(String.format(context.getString(R.string.next_task), ScheduleController.getInstance().getNextQuestionDate()));
        }else {
            if (this.data.size() == 1)
                title.setText(String.format(context.getString(R.string.pending_task), String.valueOf(this.data.size())));
            else
                title.setText(String.format(context.getString(R.string.pending_tasks), String.valueOf(this.data.size())));

            message.setVisibility(View.INVISIBLE);
            bt.setText(R.string.start_next_task);
        }
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data.clear();
        this.data.addAll(data);
        try {
            if (this.data.isEmpty()) {
                bt.setText(R.string.exit);
                title.setText(String.format(context.getString(R.string.pending_tasks), "0"));
                message.setVisibility(View.VISIBLE);
                message.setText(String.format(context.getString(R.string.next_task), ScheduleController.getInstance().getNextQuestionDate()));
            } else {
                if (this.data.size() == 1)
                    title.setText(String.format(context.getString(R.string.pending_task), String.valueOf(this.data.size())));
                else
                    title.setText(String.format(context.getString(R.string.pending_tasks), String.valueOf(this.data.size())));

                message.setVisibility(View.INVISIBLE);
                bt.setText(R.string.start_next_task);
            }
        }catch (Exception e){

        }
    }

    public void refresh(Context context, ArrayList<String> data) {
        try {
            this.data.clear();
            this.data.addAll(data);
            if(this.data.isEmpty()) {
                bt.setText(R.string.exit);
                title.setText(String.format(context.getString(R.string.pending_tasks), "0"));
                message.setVisibility(View.VISIBLE);
                message.setText(String.format(context.getString(R.string.next_task), ScheduleController.getInstance().getNextQuestionDate()));
            }else {
                if (this.data.size() == 1)
                    title.setText(String.format(context.getString(R.string.pending_task), String.valueOf(this.data.size())));
                else
                    title.setText(String.format(context.getString(R.string.pending_tasks), String.valueOf(this.data.size())));

                message.setVisibility(View.INVISIBLE);
                bt.setText(R.string.start_next_task);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}