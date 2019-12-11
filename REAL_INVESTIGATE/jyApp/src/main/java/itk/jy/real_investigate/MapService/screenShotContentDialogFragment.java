package itk.jy.real_investigate.MapService;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import itk.jy.real_investigate.MainActivity;
import itk.jy.real_investigate.R;
import itk.jy.real_investigate.library.SlidingUpPanelLayout;

public class screenShotContentDialogFragment extends DialogFragment {

    public static final String DIALOGNAME = "dialog_event";
    private static final String[] ListMenu = {"스크린샷","정보보기"};
    private static String addressT;
    public screenShotContentDialogFragment() {}
    public static screenShotContentDialogFragment getInstance(String address) {
        addressT = address;
        screenShotContentDialogFragment scSCDialog = new screenShotContentDialogFragment();
        return scSCDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_spotclick,container);

        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView TV = v.findViewById(R.id.jibun_text);
        TV.setText(addressT);
        ListView LV = v.findViewById(R.id.dialog_listview);
        ArrayAdapter adpater = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,ListMenu);
        LV.setAdapter(adpater);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                if(position == 0) {
                    ((MainActivity)getActivity()).startProjection(addressT);
                }
                else {
                    //slideUp
                    SlidingUpPanelLayout mLayout = getActivity().findViewById(R.id.sliding_layout);
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }


            }
        });
        //dialog.setCanceledOnTouchOutside(false);
        return v;
    }
}
