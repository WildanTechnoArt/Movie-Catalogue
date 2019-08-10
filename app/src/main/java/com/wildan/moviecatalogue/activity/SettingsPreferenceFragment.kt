package com.wildan.moviecatalogue.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.service.DailyAlarmReceiver
import com.wildan.moviecatalogue.service.UpcomingAlarmReceiver

class SettingsPreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

    private var switchDailyReminder: SwitchPreference? = null
    private var switchUpcomingReminder: SwitchPreference? = null

    private var alarmReceiver: UpcomingAlarmReceiver? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        savedInstanceState?.let { super.onSaveInstanceState(it) }
        setPreferencesFromResource(R.xml.preference, rootKey)

        alarmReceiver = UpcomingAlarmReceiver()

        switchDailyReminder = findPreference(resources.getString(R.string.key_daily_reminder))
        switchDailyReminder?.onPreferenceChangeListener = this
        switchUpcomingReminder = findPreference(resources.getString(R.string.key_upcoming_reminder))
        switchUpcomingReminder?.onPreferenceChangeListener = this
        findPreference<Preference>(resources.getString(R.string.key_setting_locale))?.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val key = preference.key

        if (key == resources.getString(R.string.key_setting_locale)) {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
            return true
        }

        return false
    }

    override fun onPreferenceChange(preference: Preference, o: Any): Boolean {
        val key = preference.key
        val isOn = o as Boolean

        val dailyAlarmReceiver = DailyAlarmReceiver()

        if (key == resources.getString(R.string.key_daily_reminder)) {
            if (isOn) {
                context?.let { dailyAlarmReceiver.setRepeatingAlarm(it) }
            } else {
                context?.let { dailyAlarmReceiver.cancelAlarm(it) }
            }
        } else {
            if (isOn) {
                context?.let { alarmReceiver?.setRepeatingAlarm(it) }
            } else {
                context?.let { alarmReceiver?.cancelAlarm(it) }
            }
        }

        return true

    }
}