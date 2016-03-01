package com.tanarro.iconlistpreference.demo;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.tanarro.iconlistpreference.IconListPreference;

public class IconListPreferenceTestActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    /** Called when the activity is first created. */

    IconListPreference test3, test4;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        test3 = (IconListPreference) findPreference("test3");
        test3.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        test3.setEntries(R.array.android_versions);
        test3.setEntryValues(R.array.android_version_values);
        test3.setEntryIcons(R.array.android_version_icons);

        test4 = (IconListPreference) findPreference("test4");
        test4.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        test4.setEntries(new CharSequence[] { "c", "d", "e", "f", "g", "h", "i" });
        test4.setEntryValues(new CharSequence[] { "C", "D", "E", "F", "G", "H", "I" });

        test4.setEntryIcons(new int[] { R.drawable.cupcake, R.drawable.donut, R.drawable.eclair, R.drawable.froyo, R.drawable.gingerbread,
                R.drawable.honeycomb, R.drawable.icecreamsandwich });

        test4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(IconListPreferenceTestActivity.this, (CharSequence) newValue, Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Toast.makeText(IconListPreferenceTestActivity.this,
        // (CharSequence) sharedPreferences.getString(key, ""),
        // Toast.LENGTH_LONG).show();
    }
}