package pt.lasige.cns.study.inputmethod.study.questionnaire;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.quickbirdstudios.surveykit.FinishReason;
import com.quickbirdstudios.surveykit.Identifier;
import com.quickbirdstudios.surveykit.StepIdentifier;
import com.quickbirdstudios.surveykit.SurveyTheme;
import com.quickbirdstudios.surveykit.backend.views.step.StepView;
import com.quickbirdstudios.surveykit.result.QuestionResult;
import com.quickbirdstudios.surveykit.result.StepResult;
import com.quickbirdstudios.surveykit.steps.Step;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pt.lasige.cns.study.latin.R;

public class ScaleButtonCustom implements Step {

    boolean optional;
    StepIdentifier id;
    String question, higherBound, lowerBound, continueText;
    int scale = -1;
    ArrayList<String> steps;
    TextView tv_selected = null;
    int selected = -1;

    public ScaleButtonCustom(String question, String continueText, String higherBound, String lowerBound, ArrayList<String> steps, boolean optional, StepIdentifier id) {
        this.optional = optional;
        this.id = id;
        this.question = question;
        this.higherBound = higherBound;
        this.lowerBound = lowerBound;
        this.steps = steps;
        this.continueText = continueText;
    }

    @NotNull
    @Override
    public StepIdentifier getId() {
        return this.id;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @NotNull
    @Override
    public StepView createView(@NotNull Context context, @Nullable StepResult stepResult) {
        return new StepView(context, id, optional) {
            View root;
            boolean nextEnabled = false;
            SurveyTheme surveyTheme;
            Date start, end;
            @Override
            public void setupViews() {
                this.start = Calendar.getInstance().getTime();

                root = inflate(context, R.layout.custom_questionnaire_button_scale, this);

                ((TextView) findViewById(R.id.tv_scale_lower_value)).setText(lowerBound);
                ((TextView) findViewById(R.id.tv_scale_higher_value)).setText(higherBound);
                ((TextView) findViewById(R.id.tv_question)).setText(question);

                root.findViewById(R.id.button_continue).setOnClickListener(view -> {
                    this.getOnNextListener().invoke(createResults());
                });
                root.findViewById(R.id.headerBackButtonImage).setOnClickListener(view -> {
                    this.getOnBackListener().invoke(createResults());
                });

                root.findViewById(R.id.headerCancelButton).setOnClickListener(view -> {
                    AlertDialog aDialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.leave)
                            .setMessage(R.string.leave_message)
                            .setNeutralButton(R.string.leave_neutral_button, (dialog, which) -> {
                            })
                            .setNegativeButton(R.string.leave_negative_button, (dialog, which) -> {
                                this.getOnCloseListener().invoke(createResults(), FinishReason.Completed);
                            })
                            .create();

                    aDialog.setOnShowListener(dialog -> aDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED));
                    aDialog.show();
                });
                root.findViewById(R.id.button_skip_question).setOnClickListener(view -> {
                    this.getOnSkipListener();
                });
                root.findViewById(R.id.button_skip_question).setVisibility(GONE);

                LinearLayout ll = findViewById(R.id.ll_steps);
                for (int i = 0; i < steps.size(); i++){
                    TextView myText = (TextView) inflate(context, R.layout.scale_item_layout, null);
                    myText.setText(String.valueOf(i+1));
                    myText.setBackground(getResources().getDrawable(R.drawable.border));
                    myText.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_focused));
                    myText.setOnClickListener(view -> {
                        int id = view.getId();
                        selectTV((TextView) view);
                        selected = id;
                        ((TextView) root.findViewById(R.id.tv_selected)).setText(String.valueOf(steps.get(id-1)));
                        nextEnabled = true;
                        colorMainButtonEnabledState();
                    });
                    myText.setId(i+1);

                    if(selected == i+1) {
                        selectTV(myText);
                        ((TextView) root.findViewById(R.id.tv_selected)).setText(String.valueOf(steps.get(selected-1)));
                        nextEnabled = true;
                    }
                    ll.addView(myText);
                }
            }

            @Override
            public void style(@NotNull SurveyTheme surveyTheme) {
                this.surveyTheme = surveyTheme;
                colorMainButtonEnabledState();
                ((Button) root.findViewById(R.id.headerCancelButton)).setTextColor(surveyTheme.getTextColor());
                ((ImageView) root.findViewById(R.id.headerBackButtonImage)).getBackground().setTint(surveyTheme.getTextColor());
            }

            @NotNull
            @Override
            public QuestionResult createResults() {
                this.end = Calendar.getInstance().getTime();
                scale = selected;
                return new ScaleButtonCustomResult(selected, id, start, end);
            }

            @Override
            public boolean isValidInput() {
                return false;
            }

            private void colorMainButtonEnabledState() {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.main_button_background);
                if (nextEnabled) {
                    drawable.setColorFilter(surveyTheme.getThemeColor(), PorterDuff.Mode.SRC_IN);
                    ((Button) root.findViewById(R.id.button_continue)).setTextColor(surveyTheme.getTextColor());
                    ((Button) root.findViewById(R.id.button_continue)).setEnabled(true);
                } else {
                    ((Button) root.findViewById(R.id.button_continue)).setTextColor(ContextCompat.getColor(context, R.color.disabled_grey));
                    ((Button) root.findViewById(R.id.button_continue)).setEnabled(false);
                }
                ((Button) root.findViewById(R.id.button_continue)).setBackground(drawable);
                ((Button) root.findViewById(R.id.button_continue)).setText(continueText);
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
        };
    }

    static class ScaleButtonCustomResult implements QuestionResult, Parcelable {

        int answer;
        Identifier id;
        Date start, end;

        public ScaleButtonCustomResult(int answer, Identifier id, Date start, Date end) {
            this.answer = answer;
            this.id = id;
            this.start = start;
            this.end = end;
        }

        protected ScaleButtonCustomResult(Parcel in) { }

        public final Creator<ScaleButtonCustomResult> CREATOR = new Creator<ScaleButtonCustomResult>() {
            @Override
            public ScaleButtonCustomResult createFromParcel(Parcel in) {
                return new ScaleButtonCustomResult(in);
            }

            @Override
            public ScaleButtonCustomResult[] newArray(int size) {
                return new ScaleButtonCustomResult[size];
            }
        };

        @NotNull
        @Override
        public String getStringIdentifier() {
            return id.getId();
        }

        @NotNull
        @Override
        public Date getEndDate() {
            return end;
        }

        @Override
        public void setEndDate(@NotNull Date date) {
        }

        @NotNull
        @Override
        public Identifier getId() {
            return id;
        }

        @NotNull
        @Override
        public Date getStartDate() {
            return start;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }

        public String getAnswer() {
            return String.valueOf(answer);
        }
    }
}
