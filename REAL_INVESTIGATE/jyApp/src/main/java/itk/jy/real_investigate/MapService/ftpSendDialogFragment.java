package itk.jy.real_investigate.MapService;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import itk.jy.real_investigate.R;

public class ftpSendDialogFragment extends DialogFragment {

    public static final String DIALOGNAME = "dialog_event";
    private ProgressBar ftpBar;
    private Button cancelB;
    private TextView ftp_percent;
    private TextView ftp_count;
    private TextView file_path;
    private boolean dismissDialog = false;
    public ftpSendDialogFragment() {}
    public static ftpSendDialogFragment getInstance() {
        ftpSendDialogFragment scSCDialog = new ftpSendDialogFragment();
        return scSCDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_ftpdialog,container);

        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancelB = v.findViewById(R.id.ftp_cancel);
        ftpBar = v.findViewById(R.id.ftpBar);
        file_path = v.findViewById(R.id.file_path);
        ftp_percent = v.findViewById(R.id.ftp_percent);
        ftp_count = v.findViewById(R.id.ftp_count);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog = true;
                dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        return v;
    }

    public void setProgress(int progress) {
        ftpBar.setProgress(progress);
        final String prgs = Integer.toString(progress);
        final String s_max = Integer.toString(ftpBar.getMax());
        final String s_percent = Integer.toString((ftpBar.getProgress() *100) / ftpBar.getMax());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftp_count.setText(prgs+"/"+s_max);
                ftp_percent.setText(s_percent+"%");
            }
        });
    }
    public void setMax(int max) {
        ftpBar.setMax(max);
    }
    public boolean getDismiss() {
        return dismissDialog;
    }
    public void setFilePath(String filePath) {
        final String s_filePath = filePath;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                file_path.setText(s_filePath);
            }
        });

    }
}
