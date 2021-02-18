package pt.lasige.inputmethod.study.questionnaire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.lasige.inputmethod.latin.R;
import pt.lasige.inputmethod.study.adapters.RadioAdapter;

public class QuestionnaireRadioActivity extends Activity {

    RadioAdapter adapter ;
    String options[] ;
    String questionID, questionnaireID, studyID, question;
    long startTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_radio);
        if(getActionBar() != null)
            getActionBar().hide();
        final ListView listApplication = (ListView)findViewById(R.id.lv_options);
        Intent intent = getIntent();
        studyID = intent.getStringExtra("study-id");
        questionnaireID = intent.getStringExtra("questionnaire-id");
        questionID = intent.getStringExtra("question-id");
        options = intent.getStringArrayExtra("options");
        startTS = System.currentTimeMillis();

        ((TextView) findViewById(R.id.tv_question)).setText(intent.getStringExtra("question"));

        adapter = new RadioAdapter(this, R.layout.listview_item_row_radio, options);
        listApplication.setAdapter(adapter);

        if(intent.getBooleanExtra("isLast", false))
            ((Button) findViewById(R.id.bt_next)).setText(R.string.send);

        findViewById(R.id.bt_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selected = adapter.getSelected();
                if (selected == null){
                    Toast.makeText(getApplicationContext(), R.string.your_response_is_empty, Toast.LENGTH_SHORT).show();
                }else {
                    finishTask(selected);
                }
            }
        });
    }

    private void finishTask(String selected){

//        setContentView(R.layout.finish_screen);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("response", selected);
        returnIntent.putExtra("studyID", studyID);
        returnIntent.putExtra("questionID", questionID);
        returnIntent.putExtra("questionnaireID", questionnaireID);
        returnIntent.putExtra("time", System.currentTimeMillis() - startTS);
        returnIntent.putExtra("type", "questionnaire_one_choice");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

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

}