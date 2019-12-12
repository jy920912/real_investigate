package itk.jy.real_investigate.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itk.jy.real_investigate.CameraActivity;
import itk.jy.real_investigate.Internet.FtpManager;
import itk.jy.real_investigate.MainActivity;
import itk.jy.real_investigate.MapService.ftpSendDialogFragment;
import itk.jy.real_investigate.Preference.PreferenceManager;
import itk.jy.real_investigate.R;

import static itk.jy.real_investigate.Preference.ConfigPreference.*;


public class FragmentContent extends Fragment {
    private Handler handler = new Handler();
    private ImageButton takePicture;
    private ImageButton takeImages;
    private CheckBox captureSwitch;
    private CheckBox sendSwitch;
    private CheckBox dronSwitch;
    private Button sendFileButton;
    private ListView imageListView;
    private TextView addressText;
    private TextView pnuText;

    static private boolean sendFileButtonEnable = true;
    public static Context mContext;
    private static ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> imageData;
    private SimpleAdapter adpater;
    private FtpManager ftpManager;
    private ProgressBar ftpGrassBar;
    private final int takePicture_OK = 1110;
    private final int selectPicture_OK = 1112;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        mContext = container.getContext();

        takePicture     = rootView.findViewById(R.id.take_picture);
        takeImages      = rootView.findViewById(R.id.take_image);
        captureSwitch   = rootView.findViewById(R.id.capture_switch);
        sendSwitch      = rootView.findViewById(R.id.send_switch);
        dronSwitch      = rootView.findViewById(R.id.dron_switch);
        sendFileButton = rootView.findViewById(R.id.sendButton);
        sendFileButton.setEnabled(sendFileButtonEnable);
        imageListView  = rootView.findViewById(R.id.image_listView);
        ftpGrassBar     = rootView.findViewById(R.id.progressBar);
        addressText     = rootView.findViewById(R.id.address);
        pnuText          = rootView.findViewById(R.id.pnu);

        //MAP Fragment에서 preference에 저장한 데이터 가져오기
        //카메라 버튼 클릭
        takePicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // MyCameraView Application이 있으면
                if (isExistCameraApplication()) {
                    Intent cameraApp = new Intent(getActivity().getApplication(), CameraActivity.class);
                    //주소 카메라 Activity로 보내기
                    cameraApp.putExtra("pnuName",addressText.getText().toString());

                    getActivity().startActivityForResult(cameraApp,takePicture_OK);
                    getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
                else {
                    StyleableToast.makeText(mContext, "실행 가능한 카메라가 없습니다.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }

            }
        });
        //갤러리 버튼 클릭
        takeImages.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                //intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,"Select Picture"), selectPicture_OK);
            }
        });
        //리스트 클릭 시 삭제
        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageList.remove(position);
                adpater.notifyDataSetChanged();
            }
        });

        //FTP로 서버에 사진 전송
        ftpManager = new FtpManager();
        sendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final ftpSendDialogFragment frfr = ftpSendDialogFragment.getInstance();
                frfr.show(getFragmentManager(),frfr.DIALOGNAME);
                sendFileButton.setEnabled(false);
                sendFileButtonEnable = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int imageCount;
                        try{
                            imageCount = imageListView.getAdapter().getCount();
                        }catch (Exception e) {
                            imageCount =  0;
                        }
                        //리스트가 없으면
                        if(imageCount == 0) {
                            frfr.dismiss();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sendFileButton.setEnabled(true);
                                    sendFileButtonEnable = true;
                                    StyleableToast.makeText(getActivity().getApplicationContext(),"전송할 파일목록이 없습니다.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                }
                            });
                            return;
                        }

                        boolean status;
                        String currentPath = null;
                        try {
                            //ftp 접속
                            status = ftpManager.ftpConnect("1.245.62.114", "ftp-man", "hot951753", 7203);
                        }catch(Exception e){
                            status = false;
                        }
                        if(status) {
                            currentPath = ftpManager.ftpGetDirectory();
                        }
                        //ftp 연결 실패 시
                        else {
                            frfr.dismiss();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sendFileButton.setEnabled(true);
                                    sendFileButtonEnable = true;
                                    StyleableToast.makeText(getActivity().getApplicationContext(),"ftp서버 연결 실패", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                }
                            });
                            return;
                        }
                        ftpGrassBar.setProgress(0);
                        frfr.setMax(imageCount);
                        frfr.setProgress(0);
                        ftpGrassBar.setMax(imageCount);
                        for(int i = 0; i<imageCount; i++) {
                            Object upFilePath1 = imageListView.getAdapter().getItem(i);
                            Map<String, String> fff = (Map<String, String>) upFilePath1;
                            String upFilePath = fff.get("Path");
                            String FileName = fff.get("Num") + ".jpg";
                            frfr.setFilePath(upFilePath);
                            //ftp서버에 파일 업로드
                            boolean upres = ftpManager.ftpUploadFile(upFilePath, FileName, "/");
                            boolean ftpDown = frfr.getDismiss();
                            if(ftpDown) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendFileButton.setEnabled(true);
                                        sendFileButtonEnable = true;
                                        ftpGrassBar.setProgress(0);
                                        try {
                                            StyleableToast.makeText(getActivity().getApplicationContext(), "전송 취소", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                        }catch (Exception e){}
                                    }
                                });
                                return;
                            }
                            if(upres) { //전송 성공 시
                                frfr.setProgress(i+1);
                                ftpGrassBar.setProgress(i+1);
                                //전송 시 파일 삭제
                                File f = new File(upFilePath);
                                f.delete();
                            }
                            else { //전송 실패 시
                            }
                        }
                        ftpManager.ftpDisconnect();
                        frfr.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendFileButton.setEnabled(true);
                                sendFileButtonEnable = true;
                                imageList.clear();
                                imageListView.setAdapter(null);
                                sendSwitch.setChecked(true);
                                ftpGrassBar.setProgress(0);
                                func_switchAndMapReload();
                                try {
                                    StyleableToast.makeText(getActivity().getApplicationContext(), "전송 종료", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                }catch (Exception e){}
                            }
                        });
                    }
                }).start();
            }
        });

        //사진촬영 스위치 클릭 시
        captureSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func_switchAndMapReload();
            }
        });
        captureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isCkecked) {
                if (isCkecked) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            captureSwitch.setText(R.string.capture_on);
                            captureSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_on_color));

                        }
                    });
                    ((MainActivity)getActivity()).onOff[0] = "O";
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            captureSwitch.setText(R.string.capture_off);
                            captureSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_off_color));
                        }
                    });
                    ((MainActivity)getActivity()).onOff[0] = "X";
                }
            }
        });
        //전송 스위치 클릭 시
        sendSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func_switchAndMapReload();
            }
        });
        sendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isCkecked) {
                if (isCkecked) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendSwitch.setText(R.string.send_on);
                            sendSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_on_color));

                        }
                    });
                    ((MainActivity)getActivity()).onOff[1] = "O";
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendSwitch.setText(R.string.send_off);
                            sendSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_off_color));
                        }
                    });
                    ((MainActivity)getActivity()).onOff[1] = "X";
                }
            }
        });
        //드론촬영 스위치 클릭 시
        dronSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func_switchAndMapReload();
            }
        });
        dronSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isCkecked) {
                if (isCkecked) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dronSwitch.setText(R.string.dron_on);
                            dronSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_on_color));

                        }
                    });
                    ((MainActivity)getActivity()).onOff[2] = "O";
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dronSwitch.setText(R.string.dron_off);
                            dronSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_off_color));
                        }
                    });
                    ((MainActivity)getActivity()).onOff[2] = "X";
                }
            }
        });

        //리스트가 있을 시 리스트에 추가
        if(!imageList.isEmpty()) {
            adpater = new SimpleAdapter(mContext, imageList,
                    android.R.layout.simple_list_item_2,new String[]{"Num","Path"},
                    new int[]{android.R.id.text1,android.R.id.text2});
            imageListView.setAdapter(adpater);
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 사진찍기 버튼을 누른 후 잘찍고 돌아왔다면
        if (requestCode == takePicture_OK && resultCode == Activity.RESULT_OK) {

        }
        else if (requestCode == takePicture_OK &&resultCode == Activity.RESULT_CANCELED) {

        }
        //찍은 사진들을 리스트에 추가
        else if (requestCode == takePicture_OK &&resultCode == 1213) {
            if(data != null) {
                Bundle extras = data.getExtras();
                if(extras == null) return;
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        //촬영 스위치 촬영 완료로 변경
                        captureSwitch.setChecked(true);
                        func_switchAndMapReload();
                    }
                });
                int itemCount = imageListView.getCount();
                int plusCount = 0;
                for(String _key:extras.keySet()) {
                    imageData = new HashMap<>();
                    //String imageName = addressText.getText().toString() + "_"+ (itemCount+plusCount+1);
                    String imageName = extras.get(_key).toString();
                    int Idx = imageName.lastIndexOf("/");
                    imageName = imageName.substring(Idx+1);
                    Idx = imageName.lastIndexOf(".");
                    imageName = imageName.substring(0,Idx);
                    imageData.put("Num",imageName);
                    imageData.put("Path",extras.get(_key).toString());
                    imageList.add(imageData);
                    plusCount++;
                }
                adpater = new SimpleAdapter(mContext, imageList,
                        android.R.layout.simple_list_item_2,new String[]{"Num","Path"},
                        new int[]{android.R.id.text1,android.R.id.text2});
                imageListView.setAdapter(adpater);
                adpater.notifyDataSetChanged();
            }
        }
        else if(requestCode == selectPicture_OK && resultCode == Activity.RESULT_OK) {
            if( data != null) {
                /*if(!imageList.isEmpty()) {
                    imageList.clear();
                }*/

                if(data.getClipData() == null) {
                    StyleableToast.makeText(mContext, "다중선택이 불가한 어플리케이션입니다.", Toast.LENGTH_LONG, R.style.mytoast).show();
                    Uri uri = data.getData();
                    String Path = getRealPath(uri);
                }
                else {
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            captureSwitch.setChecked(true);
                            func_switchAndMapReload();
                        }
                    });
                    int itemCount = imageListView.getCount();
                    ClipData clipData = data.getClipData();
                    if(clipData.getItemCount() > 9) {
                        StyleableToast.makeText(mContext, "사진은 9장까지 선택 가능합니다.", Toast.LENGTH_LONG, R.style.mytoast).show();
                    }
                    else if(clipData.getItemCount() >= 1 && clipData.getItemCount() <= 9) {
                        for(int i=0; i<clipData.getItemCount() ; i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            String path = getRealPath(uri);
                            String imageName = addressText.getText().toString() + "_"+ (itemCount+i+1);
                            imageData = new HashMap<>();
                            imageData.put("Num",imageName);
                            imageData.put("Path",path);
                            imageList.add(imageData);
                        }
                        adpater = new SimpleAdapter(mContext, imageList,
                                android.R.layout.simple_list_item_2,new String[]{"Num","Path"},
                                new int[]{android.R.id.text1,android.R.id.text2});
                        imageListView.setAdapter(adpater);
                        adpater.notifyDataSetChanged();
                    }
                }
            }
        }
    }
    //절대경로 받아오기
    private String getRealPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cur = mContext.getContentResolver().query(uri, proj, null, null, null);
        int index = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cur.moveToFirst();
        String Path = cur.getString(index);
        return Path;
    }

    private boolean isExistCameraApplication() {
        PackageManager packageManager = getActivity().getPackageManager();
        //MyCameraView Application
        Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //MediaStore.ACTION_IMAGE_CAPTURE을 처리할 수 있는 App 정보를 가져온다.
        List cameraApps = packageManager.queryIntentActivities( cameraApp, PackageManager.MATCH_DEFAULT_ONLY);
        // 카메라 App이 적어도 한개 이상 있는지 리턴
        return cameraApps.size() > 0;
    }

    private void func_switchAndMapReload() {
        double lon = 0;
        double lat = 0;
        String sido = PreferenceManager.getString(mContext, "sidoCode");
        String selMap = getSelectMap(mContext);
        boolean selJjk = getJijuk(mContext);
        boolean selJbn = getJibun(mContext);
        Location location = ((MainActivity) getActivity()).func_init_location();
        if(location != null) {
            lon = location.getLongitude();
            lat = location.getLatitude();
        }
        WebView webView = ((Activity) FragmentMap.mContext).findViewById(R.id.mWebView);
        webView.loadUrl("javascript:changeMarker('" + pnuText.getText() + "','" +
                ((MainActivity)getActivity()).onOff[0] + "','" + ((MainActivity)getActivity()).onOff[1] + "','" + ((MainActivity)getActivity()).onOff[2] +"')");
        webView.loadUrl("http://115.95.67.133:5088/real_investigate/main.html?" +
                "lon="+lon+"&lat="+lat+"&sido="+sido+"&map="+selMap+"&jjk="+selJjk+"&jbn="+selJbn);
    }
}
