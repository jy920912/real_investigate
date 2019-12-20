package itk.jy.real_investigate.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import itk.jy.real_investigate.CameraActivity;
import itk.jy.real_investigate.ImageViewerActivity;
import itk.jy.real_investigate.Internet.FtpManager;
import itk.jy.real_investigate.MainActivity;
import itk.jy.real_investigate.MapService.ftpSendDialogFragment;
import itk.jy.real_investigate.MoreContentActivity;
import itk.jy.real_investigate.PathList.CustomAdapter;
import itk.jy.real_investigate.PathList.ListGetSet;
import itk.jy.real_investigate.Preference.PreferenceManager;
import itk.jy.real_investigate.R;

import static itk.jy.real_investigate.Preference.ConfigPreference.*;


public class FragmentContent extends Fragment implements CustomAdapter.OnListItemSelectedInterface{
    private Handler handler = new Handler();
    private CheckBox captureSwitch;
    private CheckBox sendSwitch;
    private CheckBox dronSwitch;
    private Button sendFileButton;
    private RecyclerView imageListView;
    private ArrayList<ListGetSet> mArrayList;
    private CustomAdapter mAdapter;
    private TextView addressText;
    private TextView pnuText;

    static private boolean sendFileButtonEnable = true;
    public static Context mContext;
    private FtpManager ftpManager;
    private ProgressBar ftpGrassBar;
    private final int takePicture_OK = 1110;
    private final int selectPicture_OK = 1112;
    private int LIST_DRAG_MOTION = 0;
    private int isMoveAction = 0;
    @Override
    public void onItemSelected(View v, int position) {
        String filePath = mArrayList.get(position).getFilePath();
        Intent imageApp = new Intent(getActivity().getApplication(), ImageViewerActivity.class);

        imageApp.putExtra("filePath", filePath);

        getActivity().startActivity(imageApp);
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        mContext = container.getContext();

        ImageButton takePicture     = rootView.findViewById(R.id.take_picture);
        ImageButton takeImages      = rootView.findViewById(R.id.take_image);
        Button moreContentB = rootView.findViewById(R.id.Btn_moreProperty);
        captureSwitch   = rootView.findViewById(R.id.capture_switch);
        sendSwitch      = rootView.findViewById(R.id.send_switch);
        dronSwitch      = rootView.findViewById(R.id.dr_switch);
        sendFileButton = rootView.findViewById(R.id.sendButton);
        sendFileButton.setEnabled(sendFileButtonEnable);
        ftpGrassBar     = rootView.findViewById(R.id.progressBar);
        addressText     = rootView.findViewById(R.id.address);
        pnuText          = rootView.findViewById(R.id.pnu);

        //recyclerView 생성 및 설정
        imageListView  = rootView.findViewById(R.id.image_listView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        imageListView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        mAdapter = new CustomAdapter(mArrayList, this);
        imageListView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(imageListView.getContext(),
                mLinearLayoutManager.getOrientation());
        imageListView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(imageListView);

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
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,"Select Picture"), selectPicture_OK);
            }
        });

        moreContentB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent configIntent = new Intent(getActivity(), MoreContentActivity.class);
                startActivity(configIntent);
                getActivity().overridePendingTransition(R.anim.leftin,R.anim.leftout);
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
                            imageCount = imageListView.getAdapter().getItemCount();
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
                            String upFilePath = mArrayList.get(i).getFilePath();
                            String FileName = mArrayList.get(i).getFileName()+".jpg";
                            Map<String, String> fff = null;
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
                                        ftpManager.ftpDisconnect();
                                        try {
                                            StyleableToast.makeText(getActivity().getApplicationContext(), "전송 취소", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                        }catch (Exception e){e.getStackTrace();}
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
                                mArrayList.clear();
                                imageListView.getAdapter().notifyDataSetChanged();
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
                            dronSwitch.setText(R.string.dr_on);
                            dronSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_on_color));

                        }
                    });
                    ((MainActivity)getActivity()).onOff[2] = "O";
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dronSwitch.setText(R.string.dr_off);
                            dronSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.capture_off_color));
                        }
                    });
                    ((MainActivity)getActivity()).onOff[2] = "X";
                }
            }
        });

        return rootView;
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            // 삭제되는 아이템의 포지션을 가져온다
            final int position = viewHolder.getAdapterPosition();
            // 데이터의 해당 포지션을 삭제한다
            mArrayList.remove(position);
            // 어댑터에게 알린다
            imageListView.getAdapter().notifyItemRemoved(position);
        }
    };

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

                for(String _key:extras.keySet()) {
                    String imageName = extras.get(_key).toString();
                    int Idx = imageName.lastIndexOf("/");
                    imageName = imageName.substring(Idx+1);
                    Idx = imageName.lastIndexOf(".");
                    imageName = imageName.substring(0,Idx);
                    String imagePath = extras.get(_key).toString();
                    ListGetSet imageData = new ListGetSet(imageName, imagePath);
                    mArrayList.add(imageData);
                }
                mAdapter.notifyDataSetChanged();
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
                    int itemCount = imageListView.getAdapter().getItemCount();
                    ClipData clipData = data.getClipData();
                    if(clipData.getItemCount() > 9) {
                        StyleableToast.makeText(mContext, "사진은 9장까지 선택 가능합니다.", Toast.LENGTH_LONG, R.style.mytoast).show();
                    }
                    else if(clipData.getItemCount() >= 1 && clipData.getItemCount() <= 9) {
                        for(int i=0; i<clipData.getItemCount() ; i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            String path = getRealPath(uri);
                            String imageName = addressText.getText().toString() + "_"+ (itemCount+i+1);
                            ListGetSet imageData = new ListGetSet(imageName, path);
                            mArrayList.add(imageData);
                        }
                        mAdapter.notifyDataSetChanged();
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
