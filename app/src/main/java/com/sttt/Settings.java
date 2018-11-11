package com.sttt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class Settings extends PreferenceActivity {

    private static EditTextPreference etp1, etp2;
    public static SharedPreferences prefs;
    public static final String PREFS_NAME = "TTT_PREFS";
    public static int turn = 0, marker = 0, games = 4;
    public static String sn1 = "Player1", sn2 = "Player2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private static ListPreference lp1, lp2, lp3;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            getPrefs();

            lp1 = (ListPreference) getPreferenceManager().findPreference("turn");
            lp2 = (ListPreference) getPreferenceManager().findPreference("marker");
            lp3 = (ListPreference) getPreferenceManager().findPreference("games");
            etp1 = (EditTextPreference) getPreferenceManager().findPreference("player1");
            etp2 = (EditTextPreference) getPreferenceManager().findPreference("player2");

            lp1.setValueIndex(turn);
            lp2.setValueIndex(marker);
            lp3.setValueIndex(games);
            etp1.setText(sn1);
            etp2.setText(sn2);

            lp1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    turn = lp1.findIndexOfValue(value.toString());
                    lp1.setValueIndex(turn);
                    setPrefs();
                    return true;
                }
            });

            lp2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    marker = lp2.findIndexOfValue(value.toString());
                    lp2.setValueIndex(marker);
                    setPrefs();
                    return true;
                }
            });

            lp3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    games = lp3.findIndexOfValue(value.toString());
                    lp3.setValueIndex(games);
                    setPrefs();
                    return true;
                }
            });

            etp1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    etp1.setText(sn1);
                    sn1 = value.toString();
                    setPrefs();
                    return true;
                }
            });

            etp2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    etp2.setText(sn2);
                    sn2 = value.toString();
                    setPrefs();
                    return true;
                }
            });
        }

        private void setPrefs() {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putInt("turn", turn);
            editor.putInt("marker", marker);
            editor.putInt("games", games);
            editor.putString("player1", sn1);
            editor.putString("player2", sn2);
            editor.apply();
        }

        private void getPrefs() {
            turn = prefs.getInt("turn", turn);
            marker = prefs.getInt("marker", marker);
            games = prefs.getInt("games", games);
            sn1 = prefs.getString("player1", sn1);
            sn2 = prefs.getString("player2", sn2);
        }
    }
}
