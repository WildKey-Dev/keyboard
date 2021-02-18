package pt.lasige.inputmethod.study.questionnaire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import pt.lasige.inputmethod.latin.R;

public class QuestionnaireScaleActivity extends Activity implements View.OnClickListener{

    int index = 0, scale = -1;
    String currentTargetPhrase, question;
    String[] phrases, steps;
    String questionID, questionnaireID, studyID;
    TextView tv_selected = null, desc;
    int selected = -1;
    long startTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_scale);
        if(getActionBar() != null)
            getActionBar().hide();
        Intent intent = getIntent();
        studyID = intent.getStringExtra("study-id");
        questionnaireID = intent.getStringExtra("questionnaire-id");
        questionID = intent.getStringExtra("question-id");
        phrases =  intent.getStringArrayExtra("questions");

        scale = intent.getIntExtra("scale", -1);
        question = intent.getStringExtra("question");
        steps =  intent.getStringArrayExtra("scale-steps");
        desc = findViewById(R.id.tv_scale_desc);
        startTS = System.currentTimeMillis();

        LinearLayout ll = findViewById(R.id.ll_steps);
        for (int i = 0; i < steps.length; i++){
            TextView myText = (TextView)getLayoutInflater().inflate(R.layout.scale_item_layout, null);
            myText.setText(String.valueOf(i+1));
            myText.setBackground(getResources().getDrawable(R.drawable.border));
            myText.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_focused));
            myText.setOnClickListener(this);
            myText.setId(i+1);
            ll.addView(myText);
        }
        ((TextView) findViewById(R.id.tv_scale_lower_value)).setText(steps[0]);
        ((TextView) findViewById(R.id.tv_scale_higher_value)).setText(steps[steps.length-1]);
        ((TextView) findViewById(R.id.tv_question)).setText(question);

        if(intent.getBooleanExtra("isLast", false))
            ((Button) findViewById(R.id.bt_next)).setText(R.string.send);

        findViewById(R.id.bt_next).setOnClickListener(view1 -> {
            if (selected == -1){
                Toast.makeText(getApplicationContext(), R.string.your_response_is_empty, Toast.LENGTH_SHORT).show();
            }else {
                finishTask(selected, desc.getText().toString());
            }
        });
    }

    private void finishTask(int scale, String desc){

//        setContentView(R.layout.finish_screen);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("scale", scale);
        returnIntent.putExtra("desc", desc);
        returnIntent.putExtra("studyID", studyID);
        returnIntent.putExtra("questionID", questionID);
        returnIntent.putExtra("questionnaireID", questionnaireID);
        returnIntent.putExtra("time", System.currentTimeMillis() - startTS);
        returnIntent.putExtra("type", "questionnaire_select_scale");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }

    private void selectTV(TextView tv){

        if(tv_selected != null) {
            tv_selected.setBackground(getResources().getDrawable(R.drawable.border));
            tv_selected.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_focused));
        }
        tv.setBackgroundColor(getResources().getColor(R.color.study_accent_color));
        tv.setTextColor(Color.WHITE);
        tv_selected = tv;

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        selectTV((TextView) view);
        selected = id;
        desc.setText(steps[id-1]);
    }
}