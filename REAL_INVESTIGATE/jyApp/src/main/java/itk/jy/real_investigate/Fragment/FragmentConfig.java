package itk.jy.real_investigate.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import itk.jy.real_investigate.R;


public class FragmentConfig extends PreferenceFragmentCompat {
    ImageButton backButton;
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
        backButton = getActivity().findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

}