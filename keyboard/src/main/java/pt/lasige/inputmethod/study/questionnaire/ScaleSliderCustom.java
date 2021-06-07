package pt.lasige.inputmethod.study.questionnaire;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
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

import java.util.Calendar;
import java.util.Date;

import pt.lasige.inputmethod.latin.R;

public class ScaleSliderCustom implements Step {

    public enum ORIENTATION {HORIZONTAL, VERTICAL}
    boolean optional;
    int scale = 0;
    StepIdentifier id;
    String question, higherBound, lowerBound, continueText;
    ORIENTATION orientation;

    public ScaleSliderCustom(String question, String continueText, String higherBound, String lowerBound, ORIENTATION orientation, boolean optional, StepIdentifier id) {
        this.optional = optional;
        this.id = id;
        this.question = question;
        this.higherBound = higherBound;
        this.lowerBound = lowerBound;
        this.orientation = orientation;
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
            SeekBar sb;
            EditText et;
            
            @Override
            public void setupViews() {
                this.start = Calendar.getInstance().getTime();

                switch (orientation){
                    case VERTICAL:
                        root = inflate(context, R.layout.custom_questionnaire_seekbar_vertical, this);
                        break;
                    case HORIZONTAL:
                        root = inflate(context, R.layout.custom_questionnaire_seekbar_horizontal, this);
                        break;
                }
                ((TextView) root.findViewById(R.id.tv_scale_higher_value)).setText(higherBound);
                ((TextView) root.findViewById(R.id.tv_scale_lower_value)).setText(lowerBound);
                ((TextView) root.findViewById(R.id.tv_question)).setText(question);
                sb = root.findViewById(R.id.seekBar);
                et = root.findViewById(R.id.tv_selected);
                
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
                sb.setProgress(scale);
                et.setText(String.valueOf(scale));
                if(scale > 0)
                    nextEnabled = true;

                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().isEmpty() || Integer.parseInt(s.toString()) < 0 ||
                                Integer.parseInt(s.toString()) > sb.getMax()){
                            et.setError(String.format(getContext().getString(R.string.number_must_be_between_x_and_y), "0", String.valueOf(sb.getMax())));
                        }else {
                            sb.setProgress(Integer.parseInt(s.toString()));
                        }
                    }
                });
                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if(Integer.parseInt(et.getText().toString()) != i)
                            et.setText(String.valueOf(i));
                        nextEnabled = true;
                        colorMainButtonEnabledState();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                root.findViewById(R.id.button_skip_question).setOnClickListener(view -> {
                    this.getOnSkipListener();
                });
                root.findViewById(R.id.button_skip_question).setVisibility(GONE);
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
                scale = sb.getProgress();
                return new ScaleSliderCustomResult(scale, id, start, end);
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
        };
    }

    static class ScaleSliderCustomResult implements QuestionResult, Parcelable {

        int answer;
        Identifier id;
        Date start, end;

        public ScaleSliderCustomResult(int answer, Identifier id, Date start, Date end) {
            this.answer = answer;
            this.id = id;
            this.start = start;
            this.end = end;
        }

        protected ScaleSliderCustomResult(Parcel in) { }

        public final Creator<ScaleSliderCustomResult> CREATOR = new Creator<ScaleSliderCustomResult>() {
            @Override
            public ScaleSliderCustomResult createFromParcel(Parcel in) {
                return new ScaleSliderCustomResult(in);
            }

            @Override
            public ScaleSliderCustomResult[] newArray(int size) {
                return new ScaleSliderCustomResult[size];
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
