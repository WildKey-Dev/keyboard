package pt.lasige.inputmethod.study.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.quickbirdstudios.surveykit.SurveyTheme;

import java.util.ArrayList;
import java.util.HashMap;

import pt.lasige.inputmethod.latin.R;

public class SingleChoiceAdapter extends RecyclerView.Adapter<SingleChoiceAdapter.ViewHolder> {

    private final ArrayList<String> localDataSet;
    String checked;
    SurveyTheme surveyTheme;
    Button continueButton;
    boolean nextEnabled = false;
    Context context;

    public SingleChoiceAdapter(ArrayList<String> localDataSet, String checked, SurveyTheme surveyTheme, Button continueButton, Context context) {
        this.localDataSet = localDataSet;
        this.checked = checked;
        this.surveyTheme = surveyTheme;
        this.continueButton = continueButton;
        this.context = context;

        if(!this.checked.isEmpty())
            nextEnabled = true;

        colorMainButtonEnabledState();
    }

    @NonNull
    @Override
    public SingleChoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listview_item_row_checkbox, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.textView.setText(localDataSet.get(position));

        if(checked.equals(localDataSet.get(position))) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.textView.setTextColor(surveyTheme.getTextColor());
        }else {
            viewHolder.imageView.setVisibility(View.INVISIBLE);
            viewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        viewHolder.parent.setOnClickListener(view -> {
            checked = viewHolder.textView.getText().toString();
            nextEnabled = true;
            notifyDataSetChanged();
        });

        viewHolder.imageView.setOnClickListener(view -> {
            checked = viewHolder.textView.getText().toString();
            nextEnabled = true;
            notifyDataSetChanged();
        });

        colorMainButtonEnabledState();
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public String getChecked() {
        return checked;
    }

    private void colorMainButtonEnabledState() {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.main_button_background);

        if (nextEnabled) {
            drawable.setColorFilter(surveyTheme.getThemeColor(), PorterDuff.Mode.SRC_IN);
            continueButton.setTextColor(surveyTheme.getTextColor());
            continueButton.setEnabled(true);
        } else {
            continueButton.setTextColor(ContextCompat.getColor(context, R.color.disabled_grey));
            continueButton.setEnabled(false);
        }
        continueButton.setBackground(drawable);
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            parent = view;
            textView = (TextView) view.findViewById(R.id.tv_multi_choice_text);
            imageView = (ImageView) view.findViewById(R.id.ib_multi_choice_check);
        }
    }
}
