/*
 * Copyright (C) 2014 The Android Open Source Project
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

package pt.lasige.cns.study.inputmethod.latin.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import pt.lasige.cns.study.latin.R;
import pt.lasige.cns.study.inputmethod.logger.DataBaseFacade;

public final class StudySettingsFragment extends SubScreenFragment {
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_study);

        final Resources res = getResources();
        final Context context = getActivity();


        final Preference resetAll = findPreference("reset_all");
        resetAll.setSummary(getString(R.string.reset_all_summary, DataBaseFacade.getInstance().getUserID()));
        resetAll.setOnPreferenceClickListener(preference -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(res.getString(R.string.user_id));
            editor.remove(res.getString(R.string.config_id));
            editor.apply();
            Toast.makeText(context, res.getString(R.string.user_config_deleted), Toast.LENGTH_SHORT).show();
            return false;
        });

        final EditTextPreference changeConfigID = (EditTextPreference) findPreference("change_config_id");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String configID = prefs.getString(getString(R.string.config_id), null);
        changeConfigID.setSummary(configID);
        changeConfigID.setDefaultValue(configID);
        changeConfigID.setOnPreferenceChangeListener((preference, newValue) -> {
            String newConfigID = (String) newValue;
            DataBaseFacade.getInstance().setConfigID(context, newConfigID, result -> {
                if (result){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.config_id), newConfigID);
                    editor.apply();
                    editor.commit();
                    changeConfigID.setSummary(newConfigID);
                    changeConfigID.setDefaultValue(newConfigID);
                    Toast.makeText(context, getString(R.string.config_is_valid), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,getString(R.string.config_is_invalid), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });
    }
}
