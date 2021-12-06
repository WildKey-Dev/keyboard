package pt.lasige.cns.study.inputmethod.study.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import pt.lasige.cns.study.latin.R;

public class RadioAdapter extends ArrayAdapter<String> implements CompoundButton.OnCheckedChangeListener {

    RadioButton selected;
    Context context;
    int layoutResourceId;
    String data[] = null;

    public RadioAdapter(Context context, int layoutResourceId, String[] data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        AppInfoHolder holder = null;

        if (row == null){

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new AppInfoHolder();
            holder.chkSelect = (RadioButton) row.findViewById(R.id.radioButton);
            row.setTag(holder);

        }
        else{
            holder = (AppInfoHolder)row.getTag();
        }

        // holder.chkSelect.setChecked(true);
        holder.chkSelect.setText(data[position]);
        holder.chkSelect.setTag(position);
        holder.chkSelect.setOnCheckedChangeListener(this);
        return row;

    }

    public String getSelected(){
        if (selected == null)
            return null;

        return selected.getText().toString();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

        if(selected != null)
            ((RadioButton) selected).setChecked(false);
        selected = (RadioButton) buttonView;

    }
    static class AppInfoHolder {
        RadioButton chkSelect;
    }
}