package pt.lasige.inputmethod.study.questionnaire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.study.adapters.RadioAdapter;

public class QuestionnaireScaleRadioBtActivity extends Activity {

    RadioAdapter adapter ;
    String questionID, questionnaireID, studyID;
//    SeekBar sb;
    String options[] ;
    long startTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_seekbar);
        if(getActionBar() != null)
            getActionBar().hide();

        Intent intent = getIntent();
        studyID = intent.getStringExtra("study-id");
        questionnaireID = intent.getStringExtra("questionnaire-id");
        questionID = intent.getStringExtra("question-id");
        startTS = System.currentTimeMillis();

        ((TextView) findViewById(R.id.tv_question)).setText(intent.getStringExtra("question"));
        ((TextView) findViewById(R.id.tv_scale_lower_value)).setText(intent.getStringExtra("lower_bound"));
        ((TextView) findViewById(R.id.tv_scale_higher_value)).setText(intent.getStringExtra("higher_bound"));
//        ((TextView) findViewById(R.id.tv_current_value)).setText(getString(R.string.quest_selected) + " " +String.valueOf(0));

//        sb = findViewById(R.id.seekBar);
//        sb.setMax(intent.getIntExtra("max_scale", 0));
//        sb.setProgress(0);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        final RadioButton[] rb = new RadioButton[intent.getIntExtra("max_scale", 1)];
        RadioGroup.LayoutParams layoutParams =
                new RadioGroup.LayoutParams(
                       0,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

        for(int i = 1; i <= intent.getIntExtra("max_scale", 1); i++){
            rb[i-1] = new RadioButton(this);
            rb[i-1].setText(String.valueOf(i));
            rb[i-1].setId(i);
            rb[i-1].setLayoutParams(layoutParams);
            rb[i-1].setButtonDrawable(null);
            Drawable drawable = getResources().getDrawable(R.drawable.my_radio);
            rb[i-1].setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            rb[i-1].setGravity(Gravity.CENTER | Gravity.BOTTOM);
            radioGroup.addView(rb[i-1]);
        }

//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                Log.d("READIODEBUG", "id - > " + radioGroup.getCheckedRadioButtonId());
//                Log.d("READIODEBUG", "i  - > " + i);
//            }
//        });

//        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                ((TextView) findViewById(R.id.tv_current_value)).setText(getString(R.string.quest_selected) + " " +String.valueOf(i));
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });

        if(intent.getBooleanExtra("isLast", false))
            ((Button) findViewById(R.id.bt_next)).setText(R.string.send);

        findViewById(R.id.bt_next).setOnClickListener(view1 -> {
            finishTask(radioGroup.getCheckedRadioButtonId());
        });
    }


    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.close_window)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(R.string.no, null).create();

        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.study_accent_color));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.study_accent_color));
        });

        dialog.show();
    }

    private void finishTask(int scale){

//        setContentView(R.layout.finish_screen);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("scale", scale);
        returnIntent.putExtra("studyID", studyID);
        returnIntent.putExtra("questionID", questionID);
        returnIntent.putExtra("questionnaireID", questionnaireID);
        returnIntent.putExtra("type", "questionnaire_slider_scale");
        returnIntent.putExtra("time", System.currentTimeMillis() - startTS);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }
}