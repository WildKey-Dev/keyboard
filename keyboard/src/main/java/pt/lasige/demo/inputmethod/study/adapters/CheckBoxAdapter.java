package pt.lasige.demo.inputmethod.study.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashMap;

import pt.lasige.demo.inputmethod.latin.R;

public class CheckBoxAdapter extends ArrayAdapter<String> implements CompoundButton.OnCheckedChangeListener {

    public HashMap<Integer, Boolean> mCheckStates;
    Context context;
    int layoutResourceId;
    public String[] data = null;

    public CheckBoxAdapter(Context context, int layoutResourceId, String[] data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        mCheckStates = new HashMap<>(data.length);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        AppInfoHolder holder= null;

        if (row == null){

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new AppInfoHolder();
//            holder.chkSelect = (CheckBox) row.findViewById(R.id.checkBox);
            row.setTag(holder);

        }
        else{
            holder = (AppInfoHolder)row.getTag();
        }

        // holder.chkSelect.setChecked(true);
        holder.chkSelect.setText(data[position]);
        holder.chkSelect.setTag(position);
        if(mCheckStates.get(position) != null)
            holder.chkSelect.setChecked(mCheckStates.get(position));
        else
            holder.chkSelect.setChecked(false);
        holder.chkSelect.setOnCheckedChangeListener(this);
        return row;

    }
    public boolean isChecked(int position) {
        return mCheckStates.get(position) != null ? mCheckStates.get(position):false;
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

        mCheckStates.put((Integer) buttonView.getTag(), isChecked);

    }
    static class AppInfoHolder {
        CheckBox chkSelect;
    }
}