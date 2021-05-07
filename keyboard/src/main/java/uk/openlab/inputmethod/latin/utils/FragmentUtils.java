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

package uk.openlab.inputmethod.latin.utils;

import uk.openlab.inputmethod.dictionarypack.DictionarySettingsFragment;
import uk.openlab.inputmethod.latin.about.AboutPreferences;
import uk.openlab.inputmethod.latin.settings.AccountsSettingsFragment;
import uk.openlab.inputmethod.latin.settings.AdvancedSettingsFragment;
import uk.openlab.inputmethod.latin.settings.AppearanceSettingsFragment;
import uk.openlab.inputmethod.latin.settings.CorrectionSettingsFragment;
import uk.openlab.inputmethod.latin.settings.CustomInputStyleSettingsFragment;
import uk.openlab.inputmethod.latin.settings.DebugSettingsFragment;
import uk.openlab.inputmethod.latin.settings.GestureSettingsFragment;
import uk.openlab.inputmethod.latin.settings.PreferencesSettingsFragment;
import uk.openlab.inputmethod.latin.settings.SettingsFragment;
import uk.openlab.inputmethod.latin.settings.StudySettings;
import uk.openlab.inputmethod.latin.settings.ThemeSettingsFragment;
import uk.openlab.inputmethod.latin.spellcheck.SpellCheckerSettingsFragment;
import uk.openlab.inputmethod.latin.userdictionary.UserDictionaryAddWordFragment;
import uk.openlab.inputmethod.latin.userdictionary.UserDictionaryList;
import uk.openlab.inputmethod.latin.userdictionary.UserDictionaryLocalePicker;
import uk.openlab.inputmethod.latin.userdictionary.UserDictionarySettings;

import java.util.HashSet;

public class FragmentUtils {
    private static final HashSet<String> sLatinImeFragments = new HashSet<>();
    static {
        sLatinImeFragments.add(DictionarySettingsFragment.class.getName());
        sLatinImeFragments.add(AboutPreferences.class.getName());
        sLatinImeFragments.add(PreferencesSettingsFragment.class.getName());
        sLatinImeFragments.add(AccountsSettingsFragment.class.getName());
        sLatinImeFragments.add(AppearanceSettingsFragment.class.getName());
        sLatinImeFragments.add(ThemeSettingsFragment.class.getName());
        sLatinImeFragments.add(CustomInputStyleSettingsFragment.class.getName());
        sLatinImeFragments.add(GestureSettingsFragment.class.getName());
        sLatinImeFragments.add(CorrectionSettingsFragment.class.getName());
        sLatinImeFragments.add(AdvancedSettingsFragment.class.getName());
        sLatinImeFragments.add(DebugSettingsFragment.class.getName());
        sLatinImeFragments.add(SettingsFragment.class.getName());
        sLatinImeFragments.add(SpellCheckerSettingsFragment.class.getName());
        sLatinImeFragments.add(UserDictionaryAddWordFragment.class.getName());
        sLatinImeFragments.add(UserDictionaryList.class.getName());
        sLatinImeFragments.add(UserDictionaryLocalePicker.class.getName());
        sLatinImeFragments.add(UserDictionarySettings.class.getName());
        sLatinImeFragments.add(StudySettings.class.getName());
    }

    public static boolean isValidFragment(String fragmentName) {
        return sLatinImeFragments.contains(fragmentName);
    }
}
