/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.lasige.ideafast.study.inputmethod.latin.setup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.view.ContextThemeWrapper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pt.lasige.ideafast.study.inputmethod.compat.TextViewCompatUtils;
import pt.lasige.ideafast.study.inputmethod.compat.ViewCompatUtils;
import pt.lasige.ideafast.study.inputmethod.logger.Implicit;
import pt.lasige.ideafast.study.latin.R;
import pt.lasige.ideafast.study.inputmethod.latin.settings.SettingsActivity;
import pt.lasige.ideafast.study.inputmethod.latin.settings.SettingsLauncherActivity;
import pt.lasige.ideafast.study.inputmethod.latin.utils.LeakGuardHandlerWrapper;
import pt.lasige.ideafast.study.inputmethod.latin.utils.UncachedInputMethodManagerUtils;
import pt.lasige.ideafast.study.inputmethod.logger.DataBaseFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Nonnull;

// TODO: Use Fragment to implement welcome screen and setup steps.
public final class SetupWizardActivity extends Activity implements View.OnClickListener {
    static final String TAG = SetupWizardActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

    // For debugging purpose.
    private static final boolean FORCE_TO_SHOW_WELCOME_SCREEN = false;
    private static final boolean ENABLE_WELCOME_VIDEO = false;
    private static final boolean SHOW_EMAIL_DIALOG = false;

    private InputMethodManager mImm;

    private static final int RC_SIGN_IN = 1904;
    private View mSetupWizard;
    private View mWelcomeScreen;
    private View mSetupScreen;
    private Uri mWelcomeVideoUri;
    private VideoView mWelcomeVideoView;
    private ImageView mWelcomeImageView;
//    private View mActionStart;
    private View mActionNext;
    private TextView mStep1Bullet;
    private TextView mActionFinish;
    private Button mPrivacyPolicy;
    private Button mProceed;
    private CheckBox mPrivacyCheckbox;
    private SetupStepGroup mSetupStepGroup;
    private static final String STATE_STEP = "step";
    private int mStepNumber;
    private boolean mNeedsToAdjustStepNumberToSystemState;
    private static final int STEP_WELCOME = 0;
    private static final int STEP_0 = 1;
    private static final int STEP_1 = 2;
    private static final int STEP_2 = 3;
    private static final int STEP_3 = 4;
    private static final int STEP_LAUNCHING_IME_SETTINGS = 5;
    private static final int STEP_BACK_FROM_IME_SETTINGS = 6;

    private boolean validConfig = false;

    private SettingsPoolingHandler mHandler;

    private static final class SettingsPoolingHandler
            extends LeakGuardHandlerWrapper<SetupWizardActivity> {
        private static final int MSG_POLLING_IME_SETTINGS = 0;
        private static final long IME_SETTINGS_POLLING_INTERVAL = 200;

        private final InputMethodManager mImmInHandler;

        public SettingsPoolingHandler(@Nonnull final SetupWizardActivity ownerInstance,
                                      final InputMethodManager imm) {
            super(ownerInstance);
            mImmInHandler = imm;
        }

        @Override
        public void handleMessage(final Message msg) {
            final SetupWizardActivity setupWizardActivity = getOwnerInstance();
            if (setupWizardActivity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_POLLING_IME_SETTINGS:
                    if (UncachedInputMethodManagerUtils.isThisImeEnabled(setupWizardActivity,
                            mImmInHandler)) {
                        setupWizardActivity.invokeSetupWizardOfThisIme();
                        return;
                    }
                    startPollingImeSettings();
                    break;
            }
        }

        public void startPollingImeSettings() {
            sendMessageDelayed(obtainMessage(MSG_POLLING_IME_SETTINGS),
                    IME_SETTINGS_POLLING_INTERVAL);
        }

        public void cancelPollingImeSettings() {
            removeMessages(MSG_POLLING_IME_SETTINGS);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mImm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mHandler = new SettingsPoolingHandler(this, mImm);

        setContentView(R.layout.setup_wizard);
        mSetupWizard = findViewById(R.id.setup_wizard);

        if (savedInstanceState == null) {
            mStepNumber = determineSetupStepNumberFromLauncher();
        } else {
            mStepNumber = savedInstanceState.getInt(STATE_STEP);
        }

        final String applicationName = getResources().getString(getApplicationInfo().labelRes);
        mWelcomeScreen = findViewById(R.id.setup_welcome_screen);
        mPrivacyPolicy = mWelcomeScreen.findViewById(R.id.bt_read_privacy_policy);
        mPrivacyPolicy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://techandpeople.github.io/wildkey/privacy/"));
            startActivity(browserIntent);
        });
        mProceed = mWelcomeScreen.findViewById(R.id.bt_proceed);
        mProceed.setOnClickListener(this);
        mPrivacyCheckbox = mWelcomeScreen.findViewById(R.id.bt_privacy_policy_confirmation);
        mPrivacyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mProceed.setAlpha(1f);
                    mProceed.setEnabled(true);
                } else {
                    mProceed.setAlpha(0.9f);
                    mProceed.setEnabled(false);
                }
            }
        });
        final TextView welcomeTitle = (TextView) findViewById(R.id.setup_welcome_title);
        welcomeTitle.setText(getString(R.string.setup_welcome_title, applicationName));

        mSetupScreen = findViewById(R.id.setup_steps_screen);
        final TextView stepsTitle = (TextView)findViewById(R.id.setup_title);
        stepsTitle.setText(getString(R.string.setup_steps_title, applicationName));

        final SetupStepIndicatorView indicatorView =
                (SetupStepIndicatorView)findViewById(R.id.setup_step_indicator);
        mSetupStepGroup = new SetupStepGroup(indicatorView);

        final SetupStep step0 = new SetupStep(STEP_0, applicationName,
                (TextView)findViewById(R.id.setup_step0_bullet), findViewById(R.id.setup_step0),
                R.string.setup_step0_title, R.string.setup_step0_instruction,
                0 /* finishedInstruction */, 0,
                R.string.setup_step0_action);
        step0.setLogInAction(this::logIn);
        mSetupStepGroup.addStep(step0);

        mStep1Bullet = (TextView)findViewById(R.id.setup_step1_bullet);
        mStep1Bullet.setOnClickListener(this);
        final SetupStep step1 = new SetupStep(STEP_1, applicationName,
                mStep1Bullet, findViewById(R.id.setup_step1),
                R.string.setup_step1_title, R.string.setup_step1_instruction,
                R.string.setup_step1_finished_instruction, R.drawable.ic_setup_step1,
                R.string.setup_step1_action);
        final SettingsPoolingHandler handler = mHandler;
        step1.setAction(new Runnable() {
            @Override
            public void run() {
                invokeLanguageAndInputSettings();
                handler.startPollingImeSettings();
            }
        });
        mSetupStepGroup.addStep(step1);

        final SetupStep step2 = new SetupStep(STEP_2, applicationName,
                (TextView)findViewById(R.id.setup_step2_bullet), findViewById(R.id.setup_step2),
                R.string.setup_step2_title, R.string.setup_step2_instruction,
                0 /* finishedInstruction */, R.drawable.ic_setup_step2,
                R.string.setup_step2_action);
        step2.setAction(new Runnable() {
            @Override
            public void run() {
                invokeInputMethodPicker();
            }
        });
        mSetupStepGroup.addStep(step2);

        final SetupStep step3 = new SetupStep(STEP_3, applicationName,
                (TextView)findViewById(R.id.setup_step3_bullet), findViewById(R.id.setup_step3),
                R.string.setup_step3_title, R.string.setup_step3_instruction,
                0 /* finishedInstruction */, R.drawable.ic_setup_step3,
                R.string.setup_step3_action);
        step3.setAction(new Runnable() {
            @Override
            public void run() {
                invokeSubtypeEnablerOfThisIme();
            }
        });
        mSetupStepGroup.addStep(step3);

        mWelcomeVideoUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getPackageName())
                .path(Integer.toString(R.raw.setup_welcome_video))
                .build();
        final VideoView welcomeVideoView = (VideoView)findViewById(R.id.setup_welcome_video);
        welcomeVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                // Now VideoView has been laid-out and ready to play, remove background of it to
                // reveal the video.
                welcomeVideoView.setBackgroundResource(0);
                mp.setLooping(true);
            }
        });
        welcomeVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                Log.e(TAG, "Playing welcome video causes error: what=" + what + " extra=" + extra);
                hideWelcomeVideoAndShowWelcomeImage();
                return true;
            }
        });
        mWelcomeVideoView = welcomeVideoView;
        mWelcomeImageView = (ImageView)findViewById(R.id.setup_welcome_image);

//        mActionStart = findViewById(R.id.setup_start_label);
//        mActionStart.setOnClickListener(this);
        mActionNext = findViewById(R.id.setup_next);
        mActionNext.setOnClickListener(this);
        mActionFinish = (TextView)findViewById(R.id.setup_finish);
        TextViewCompatUtils.setCompoundDrawablesRelativeWithIntrinsicBounds(mActionFinish,
                getResources().getDrawable(R.drawable.ic_setup_finish), null, null, null);
        mActionFinish.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        if (v == mActionFinish) {

            final Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(SettingsActivity.EXTRA_ENTRY_KEY,
                    SettingsActivity.EXTRA_ENTRY_VALUE_APP_ICON);
            startActivity(intent);
            finish();
            return;
        }
        final int currentStep = determineSetupStepNumber();
        final int nextStep;
        if (v == mProceed) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getString(R.string.accepted_privacy_policy), true);
            editor.apply();
            nextStep = STEP_0;
        } else if (v == mActionNext) {
            nextStep = mStepNumber + 1;
        } else if (v == mStep1Bullet && currentStep == STEP_2) {
            nextStep = STEP_1;
        } else {
            nextStep = mStepNumber;
        }
        if (mStepNumber != nextStep) {
            mStepNumber = nextStep;
            updateSetupStepView();
        }
    }

    void invokeSetupWizardOfThisIme() {
        final Intent intent = new Intent();
        intent.setClass(this, SetupWizardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        mNeedsToAdjustStepNumberToSystemState = true;
    }

    private void invokeSettingsOfThisIme() {
        final Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
//        intent.setClass(this, SettingsLauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(SettingsActivity.EXTRA_ENTRY_KEY,
                SettingsActivity.EXTRA_ENTRY_VALUE_APP_ICON);
        startActivity(intent);
    }

    void invokeLanguageAndInputSettings() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);
        mNeedsToAdjustStepNumberToSystemState = true;
    }

    void invokeInputMethodPicker() {
        // Invoke input method picker.
        mImm.showInputMethodPicker();
        mNeedsToAdjustStepNumberToSystemState = true;
    }

    void invokeSubtypeEnablerOfThisIme() {
        final InputMethodInfo imi =
                UncachedInputMethodManagerUtils.getInputMethodInfoOf(getPackageName(), mImm);
        if (imi == null) {
            return;
        }
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Settings.EXTRA_INPUT_METHOD_ID, imi.getId());
        startActivity(intent);
    }

    void logIn(String userID) {
        checkUID(userID);
    }

    boolean isLogIn(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userID = prefs.getString(getString(R.string.user_id), null);
        return userID != null;
//        String configID = prefs.getString(getString(R.string.config_id), null);
//        if(userID != null && configID != null){
//        if(userID != null){
//            DataBaseFacade.getInstance().setConfigID(getApplicationContext(), configID, result -> {
//                if (!result) {
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.remove(getString(R.string.user_id));
//                    editor.remove(getString(R.string.config_id));
//                    editor.apply();
////                    editor.commit();
//                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_config_2), Toast.LENGTH_SHORT).show();
//                }else {
//                    DataBaseFacade.getInstance().setLocalUserID(userID);
//                }
//            });
//            return true;
//        }else {
//            return false;
//        }
    }

    void checkIfConfigExists(String userID, String configID){

        ProgressDialog pd = new ProgressDialog(new ContextThemeWrapper(SetupWizardActivity.this, R.style.MyAlertDialogStyle));
        pd.setMessage(getString(R.string.checking_user));
        pd.show();

        DataBaseFacade.getInstance().setConfigID(getApplicationContext(), configID, result -> {
            validConfig = result;
            pd.dismiss();
            if (validConfig){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.user_id), userID);
                editor.putString(getString(R.string.config_id), configID);
                editor.apply();
                editor.commit();
                Toast.makeText(getApplicationContext(), getString(R.string.user_is_valid), Toast.LENGTH_SHORT).show();

                WindowManager w = getWindowManager();
                Display d = w.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                d.getMetrics(metrics);
                int densityDpi = (int)(metrics.density * 160f);
                int height = metrics.heightPixels;
                int width = metrics.widthPixels;

                DataBaseFacade.getInstance().write("brand", Build.BRAND, "/users/"+userID+"/device/");
                DataBaseFacade.getInstance().write("device", Build.DEVICE, "/users/"+userID+"/device/");
                DataBaseFacade.getInstance().write("densityDpi", densityDpi, "/users/"+userID+"/device/display/");
                DataBaseFacade.getInstance().write("display", Build.DISPLAY, "/users/"+userID+"/device/display/");
                DataBaseFacade.getInstance().write("height", height, "/users/"+userID+"/device/display/");
                DataBaseFacade.getInstance().write("width", width, "/users/"+userID+"/device/display/");
                DataBaseFacade.getInstance().write("model", Build.MODEL, "/users/"+userID+"/device/");
                DataBaseFacade.getInstance().write("release", Build.VERSION.RELEASE, "/users/"+userID+"/device/");
                DataBaseFacade.getInstance().write("sdk", Build.VERSION.SDK_INT, "/users/"+userID+"/device/");

                DataBaseFacade.getInstance().write("configId", configID, "/user-list/"+userID+"/");

                mStepNumber = determineSetupStepNumber();
                updateSetupStepView();
            }else {
                Toast.makeText(getApplicationContext(),getString(R.string.user_is_invalid), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void checkUID(String userID){

        ProgressDialog pd = new ProgressDialog(new ContextThemeWrapper(SetupWizardActivity.this, R.style.MyAlertDialogStyle));
        pd.setMessage(getString(R.string.checking_user));
        pd.show();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if( currentUser == null ){
            String uid = userID.length()==7?userID.substring(1):userID.substring(2);
            mAuth.signInWithEmailAndPassword(uid + "@ideafast.com", uid)
                .addOnCompleteListener(this, task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.user_id), userID);
                        editor.apply();
                        DataBaseFacade.getInstance().setLocalUserID(userID);
                        Toast.makeText(getApplicationContext(), getString(R.string.user_is_valid), Toast.LENGTH_SHORT).show();

                        Implicit.getInstance().setImplicitListener(getApplicationContext());
//                Explicit.getInstance().setListener(); no need to this one for now

                        DataBaseFacade.getInstance().forceWrite("brand", Build.BRAND, "/users/"+userID+"/device/");
                        DataBaseFacade.getInstance().forceWrite("device", Build.DEVICE, "/users/"+userID+"/device/");
                        DataBaseFacade.getInstance().forceWrite("model", Build.MODEL, "/users/"+userID+"/device/");
                        DataBaseFacade.getInstance().forceWrite("release", Build.VERSION.RELEASE, "/users/"+userID+"/device/");
                        DataBaseFacade.getInstance().forceWrite("sdk", Build.VERSION.SDK_INT, "/users/"+userID+"/device/");

                        DataBaseFacade.getInstance().forceWrite(userID, userID, "/user-list/");

                        mStepNumber = determineSetupStepNumber();
                        updateSetupStepView();
                    } else {
                        Toast.makeText(getApplicationContext(),getString(R.string.user_is_invalid), Toast.LENGTH_SHORT).show();
                    }
                });
        } else {
            pd.dismiss();
            Toast.makeText(getApplicationContext(),getString(R.string.user_is_already_logged_in), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "checkUID: " + currentUser.getEmail());
        }
    }

    private int determineSetupStepNumberFromLauncher() {
        final int stepNumber = determineSetupStepNumber();
        if (stepNumber == STEP_0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(prefs.getBoolean(getString(R.string.accepted_privacy_policy), false))
                return STEP_0;
            else
                return STEP_WELCOME;
        }
        if (stepNumber == STEP_3) {
            return STEP_LAUNCHING_IME_SETTINGS;
        }
        return stepNumber;
    }

    private int determineSetupStepNumber() {
        mHandler.cancelPollingImeSettings();
        if (FORCE_TO_SHOW_WELCOME_SCREEN) {
            return STEP_1;
        }

        if (!isLogIn()) {
            return STEP_0;
        }
        if (!UncachedInputMethodManagerUtils.isThisImeEnabled(this, mImm)) {
            return STEP_1;
        }
        if (!UncachedInputMethodManagerUtils.isThisImeCurrent(this, mImm)) {
            return STEP_2;
        }
        return STEP_3;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_STEP, mStepNumber);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStepNumber = savedInstanceState.getInt(STATE_STEP);
        Log.d(TAG, "onRestoreInstanceState: " + mStepNumber);
    }

    private static boolean isInSetupSteps(final int stepNumber) {
        return stepNumber >= STEP_0 && stepNumber <= STEP_3;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Probably the setup wizard has been invoked from "Recent" menu. The setup step number
        // needs to be adjusted to system state, because the state (IME is enabled and/or current)
        // may have been changed.
        if (isInSetupSteps(mStepNumber)) {
            mStepNumber = determineSetupStepNumber();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStepNumber == STEP_LAUNCHING_IME_SETTINGS) {
            // Prevent white screen flashing while launching settings activity.
            mSetupWizard.setVisibility(View.INVISIBLE);
            invokeSettingsOfThisIme();
            mStepNumber = STEP_BACK_FROM_IME_SETTINGS;
            return;
        }
        if (mStepNumber == STEP_BACK_FROM_IME_SETTINGS) {
            finish();
            return;
        }

        mStepNumber = determineSetupStepNumberFromLauncher();

        updateSetupStepView();
    }

    @Override
    public void onBackPressed() {
        if (mStepNumber == STEP_0 && !acceptedPrivacyPolicy()) {
            mStepNumber = STEP_WELCOME;
            updateSetupStepView();
            return;
        }
        super.onBackPressed();
    }

    void hideWelcomeVideoAndShowWelcomeImage() {
        mWelcomeVideoView.setVisibility(View.GONE);
        mWelcomeImageView.setImageResource(R.raw.setup_welcome_image);
        mWelcomeImageView.setVisibility(View.GONE);
//        mWelcomeImageView.setVisibility(View.VISIBLE);
    }

    private void showAndStartWelcomeVideo() {
        mWelcomeVideoView.setVisibility(View.VISIBLE);
        mWelcomeVideoView.setVideoURI(mWelcomeVideoUri);
        mWelcomeVideoView.start();
    }

    private void hideAndStopWelcomeVideo() {
        mWelcomeVideoView.stopPlayback();
        mWelcomeVideoView.setVisibility(View.GONE);
    }

    private boolean acceptedPrivacyPolicy(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(getString(R.string.accepted_privacy_policy), false);
    }

    @Override
    protected void onPause() {
        hideAndStopWelcomeVideo();
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mNeedsToAdjustStepNumberToSystemState) {
            mNeedsToAdjustStepNumberToSystemState = false;
            mStepNumber = determineSetupStepNumber();
            updateSetupStepView();
        }
    }

    private void updateSetupStepView() {
        mSetupWizard.setVisibility(View.VISIBLE);
        final boolean welcomeScreen = (mStepNumber == STEP_WELCOME);
        mWelcomeScreen.setVisibility(welcomeScreen ? View.VISIBLE : View.GONE);
        mSetupScreen.setVisibility(welcomeScreen ? View.GONE : View.VISIBLE);
        if (welcomeScreen) {
            if (ENABLE_WELCOME_VIDEO) {
                showAndStartWelcomeVideo();
            } else {
                hideWelcomeVideoAndShowWelcomeImage();
            }
            return;
        }
        hideAndStopWelcomeVideo();
        final boolean isStepActionAlreadyDone = mStepNumber < determineSetupStepNumber();
        mSetupStepGroup.enableStep(mStepNumber, isStepActionAlreadyDone);
        mActionNext.setVisibility(isStepActionAlreadyDone ? View.VISIBLE : View.GONE);
        mActionFinish.setVisibility((mStepNumber == STEP_3) ? View.VISIBLE : View.GONE);
    }

    final class SetupStep implements View.OnClickListener {
        public final int mStepNo;
        private final View mStepView;
        private final TextView mBulletView;
        private final int mActivatedColor;
        private final int mDeactivatedColor;
        private final String mInstruction;
        private final String mFinishedInstruction;
        private final TextView mActionLabel;
        private Runnable mAction;
        private LogInAction mLogInAction;

        public SetupStep(final int stepNo, final String applicationName, final TextView bulletView,
                         final View stepView, final int title, final int instruction,
                         final int finishedInstruction, final int actionIcon, final int actionLabel) {
            mStepNo = stepNo;
            mStepView = stepView;
            mBulletView = bulletView;
            final Resources res = stepView.getResources();
            mActivatedColor = res.getColor(R.color.setup_text_action);
            mDeactivatedColor = res.getColor(R.color.setup_text_dark);

            final TextView titleView = (TextView)mStepView.findViewById(R.id.setup_step_title);
            titleView.setText(res.getString(title, applicationName));
            mInstruction = (instruction == 0) ? null
                    : res.getString(instruction, applicationName);
            mFinishedInstruction = (finishedInstruction == 0) ? null
                    : res.getString(finishedInstruction, applicationName);

            mActionLabel = (TextView)mStepView.findViewById(R.id.setup_step_action_label);
            mActionLabel.setText(res.getString(actionLabel));
            if (actionIcon == 0) {
                final int paddingEnd = ViewCompatUtils.getPaddingEnd(mActionLabel);
                ViewCompatUtils.setPaddingRelative(mActionLabel, paddingEnd, 0, paddingEnd, 0);
            } else {
                TextViewCompatUtils.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        mActionLabel, res.getDrawable(actionIcon), null, null, null);
            }
        }

        public void setEnabled(final boolean enabled, final boolean isStepActionAlreadyDone) {
            mStepView.setVisibility(enabled ? View.VISIBLE : View.GONE);
            mBulletView.setTextColor(enabled ? mActivatedColor : mDeactivatedColor);
            final TextView instructionView = (TextView)mStepView.findViewById(
                    R.id.setup_step_instruction);
            instructionView.setText(isStepActionAlreadyDone ? mFinishedInstruction : mInstruction);
            mActionLabel.setVisibility(isStepActionAlreadyDone ? View.GONE : View.VISIBLE);
        }

        public void setAction(final Runnable action) {
            mActionLabel.setOnClickListener(this);
            mAction = action;
        }

        public void setLogInAction(LogInAction action){
            mActionLabel.setOnClickListener(this);
            mLogInAction = action;
        }

        private boolean checkValidSiteLetter(char c){
            ArrayList<Character> sites = new ArrayList<>(Arrays.asList('K', 'G', 'N', 'E', 'H', 'Y', 'D', 'O', 'C', 'M', 'Q', 'I', 'L', 'B', 'P', 'A', 'W', 'S', 'R', 'T', 'F'));
            return sites.contains(c);
        }

        @Override
        public void onClick(final View v) {
            if(v == mActionLabel && mLogInAction != null){
                if(mStepView.findViewById(R.id.user_id) != null){
                    if(((EditText) mStepView.findViewById(R.id.user_id)).getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please provide a valid ID", Toast.LENGTH_SHORT).show();
                    } else {
                        String uid = ((EditText) mStepView.findViewById(R.id.user_id)).getText().toString().toUpperCase();
                        if(!checkValidSiteLetter(uid.charAt(0))){
                            Toast.makeText(getApplicationContext(), "Please provide a valid ID", Toast.LENGTH_SHORT).show();
                        } else {
                            if(uid.length() == 8) {
                                if (uid.charAt(1) != '-')
                                    Toast.makeText(getApplicationContext(), "Please provide a valid ID", Toast.LENGTH_SHORT).show();
                                else
                                    mLogInAction.run(uid);
                            } else if(uid.length() == 7) {
                                mLogInAction.run(uid);
                            } else {
                                Toast.makeText(getApplicationContext(), "Please provide a valid ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                return;
            }else if (v == mActionLabel && mAction != null) {
                mAction.run();
                return;
            }
        }
    }

    static final class SetupStepGroup {
        private final SetupStepIndicatorView mIndicatorView;
        private final ArrayList<SetupStep> mGroup = new ArrayList<>();

        public SetupStepGroup(final SetupStepIndicatorView indicatorView) {
            mIndicatorView = indicatorView;
        }

        public void addStep(final SetupStep step) {
            mGroup.add(step);
        }

        public void enableStep(final int enableStepNo, final boolean isStepActionAlreadyDone) {
            for (final SetupStep step : mGroup) {
                step.setEnabled(step.mStepNo == enableStepNo, isStepActionAlreadyDone);
            }
            mIndicatorView.setIndicatorPosition(enableStepNo - STEP_0, mGroup.size());
        }
    }

    interface LogInAction{
        void run(String userID);
    }
}
