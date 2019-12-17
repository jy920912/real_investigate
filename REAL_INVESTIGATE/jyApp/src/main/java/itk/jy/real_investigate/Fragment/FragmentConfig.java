package itk.jy.real_investigate.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import itk.jy.real_investigate.R;


public class FragmentConfig extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
    }
}