package com.moxtra.moxiechat;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;

/**
 * Created by blade on 3/26/15.
 */
public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettingsFragment()).commit();
        getSupportActionBar().setTitle(R.string.action_settings);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        miSearch.setVisible(false);
        miSettings.setVisible(false);
        return true;
    }

    public static class MainSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
