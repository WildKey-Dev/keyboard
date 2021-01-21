package pt.lasige.inputmethod.study.questionnaire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import pt.lasige.inputmethod.latin.R;

public class QuestionnaireSeekBarActivity extends Activity {

    String questionID, questionnaireID, studyID;
    SeekBar sb;
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
        ((TextView) findViewById(R.id.tv_current_value)).setText(getString(R.string.quest_selected) + " " +String.valueOf(0));

        sb = findViewById(R.id.seekBar);
        sb.setMax(intent.getIntExtra("max_scale", 0));
        sb.setProgress(0);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((TextView) findViewById(R.id.tv_current_value)).setText(getString(R.string.quest_selected) + " " +String.valueOf(i));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if(intent.getBooleanExtra("isLast", false))
            ((Button) findViewById(R.id.bt_next)).setText(R.string.send);

        findViewById(R.id.bt_next).setOnClickListener(view1 -> {
            finishTask(sb.getProgress());
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