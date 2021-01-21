package pt.lasige.inputmethod.study.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.study.scheduler.ScheduleController;

public class PromptAdapter extends ArrayAdapter<String>{

    RadioButton selected;
    Activity context;
    int layoutResourceId;
    ArrayList<String> data;
    Button bt;
    TextView emptyWarning;

    public PromptAdapter(Activity context, int layoutResourceId, ArrayList<String> data, Button bt, TextView emptyWarning){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.bt = bt;
        this.emptyWarning = emptyWarning;
        if(this.data.isEmpty()) {
            bt.setText(R.string.exit);
            emptyWarning.setVisibility(View.VISIBLE);
        }else {
            bt.setText(R.string.start_next_task);
            emptyWarning.setVisibility(View.GONE);
        }
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data.clear();
        this.data.addAll(data);
        if(this.data.isEmpty()) {
            bt.setText(R.string.exit);
            emptyWarning.setVisibility(View.VISIBLE);
        }else {
            bt.setText(R.string.start_next_task);
            emptyWarning.setVisibility(View.GONE);
        }
        context.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                }
        );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        AppInfoHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new AppInfoHolder();
            holder.tv = (TextView) row.findViewById(R.id.tv_prompt);
            row.setTag(holder);

        }
        else{
            holder = (AppInfoHolder)row.getTag();
        }

        //the data can be changed by other thread
        //if the data is changed in the middle of an UI update we trigger another update
        try{
            String str = ScheduleController.getInstance().getType(data.get(position));
            String composite = str.substring(0, 1).toUpperCase() + str.substring(1);
            holder.tv.setText(composite);
            holder.tv.setTag(position);
            return row;
        }catch (Exception e){
            notifyDataSetChanged();
            return row;
        }

    }

    static class AppInfoHolder {
        TextView tv;
    }
}