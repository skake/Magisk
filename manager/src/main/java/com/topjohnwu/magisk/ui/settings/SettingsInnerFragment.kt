package com.topjohnwu.magisk.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.topjohnwu.magisk.R


class SettingsInnerFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_settings)
    }
}