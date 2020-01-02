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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import itk.jy.real_investigate.MainActivity;
import itk.jy.real_investigate.R;
import itk.jy.real_investigate.library.SlidingUpPanelLayout;

public class screenShotContentDialogFragment extends DialogFragment {

    public static final String DIALOGNAME = "dialog_event";
    private static final String[] ListMenu = {"정보보기","스크린샷"};
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

        final CheckBox spotTFBox = v.findViewById(R.id.spot_check);
        TextView TV = v.findViewById(R.id.jibun_text);
        TV.setText(addressT);
        ListView LV = v.findViewById(R.id.dialog_listview);

        ArrayAdapter adpater = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,ListMenu);
        LV.setAdapter(adpater);

        //dialog 선택
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                //정보보기 선택 시
                if(position == 0){
                    //slideUp
                    SlidingUpPanelLayout mLayout = getActivity().findViewById(R.id.sliding_layout);
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                //스크린샷 선택 시
                else if(position == 1) {
                    boolean tftf;
                    if(spotTFBox.isChecked()) tftf = true;
                    else tftf = false;
                    //스크린샷 시작
                    ((MainActivity)getActivity()).startProjection(addressT, tftf);
                }
            }
        });
        return v;
    }
}
