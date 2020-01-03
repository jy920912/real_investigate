package itk.jy.real_investigate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import itk.jy.real_investigate.Internet.InternetManager;
import itk.jy.real_investigate.PathList.CustomAdapter;
import itk.jy.real_investigate.PathList.ListGetSet;
import itk.jy.real_investigate.Preference.PreferenceManager;
import itk.jy.real_investigate.library.SlidingUpPanelLayout;
import itk.jy.real_investigate.library.SlidingUpPanelLayout.PanelState;
import itk.jy.real_investigate.Fragment.FragmentContent;
import itk.jy.real_investigate.Fragment.FragmentMap;
import itk.jy.real_investigate.MapService.GPS;

public class MainActivity extends AppCompatActivity {
    public static String[] onOff = new String[3];
    EditText searchText;
    ImageButton searchButton;
    ImageButton configButton;
    ImageButton picListButton;
    public SlidingUpPanelLayout mLayout;

    GPS gps_Ckeck;
    Location location;
    public boolean tackerOnOff = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        //gps 준비
        gps_Ckeck  = new GPS(MainActivity.this);


        //slide 화면에 컨텐츠 정보 출력 위한 content fragment 출력
        FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
        contentTransaction.replace(R.id.slide_contents, new FragmentContent()).commit();

        //main 화면에 map 출력 위한 map view fragment 출력
        FragmentTransaction mapTransaction = getSupportFragmentManager().beginTransaction();
        mapTransaction.replace(R.id.mainMap, new FragmentMap()).commit();

        //slide panel
        mLayout = findViewById(R.id.sliding_layout);
        //초기 아래에서 삐져나오는 높이 설정
        mLayout.setPanelHeight(10);

        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

        searchText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_glass);
        //지번검색
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchT = searchText.getText().toString();
                if("".equals(searchT)) {
                    StyleableToast.makeText(getApplication(),"내용이 없습니다.", Toast.LENGTH_SHORT,R.style.mytoast).show();
                }
                else {
                    //키보드
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    if(imm == null) return;
                     FragmentMap map = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mainMap);
                     if(map == null) return;
                    try {
                        //검색 시 키보드 숨기기
                        imm.hideSoftInputFromWindow(searchButton.getWindowToken(),0);
                        //대상지 검색 스크립트 실행
                        map.mWebView.loadUrl("javascript:android_receiveSearch('"+searchT+"')");
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        //설정Activity 이동
        configButton = findViewById(R.id.configButton);
        configButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent configIntent = new Intent(MainActivity.this, ConfigActivity.class);
                finish();
                startActivity(configIntent);
                overridePendingTransition(R.anim.rightin,R.anim.rightout);
            }
        });
        //대상지 목록 Activity 이동
        picListButton = findViewById(R.id.listButton);
        picListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent configIntent = new Intent(MainActivity.this, PicListActivity.class);
                startActivity(configIntent);
            }
        });

    }

    //좌표 가져오기
    public Location func_init_location() {
        location = gps_Ckeck.getLocation();
        return location;
    }

    boolean downApp = false;
    //웹에 좌표 보내기(javascript 이용)
    public void func_output_lon_lat(double lon, double lat) {
        if(downApp) return;
        //인터넷 미연결 시 종료박스 출력
        if(InternetManager.getConnectivityStatus(getApplicationContext()) == 3) {
            gps_Ckeck.stopUsingGPS();
            // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
            AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
            alBuilder.setMessage("인터넷이 연결되지 않았습니다. 인터넷을 연결하십시오.");

            alBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alBuilder.setTitle("인터넷 연결상태 확인");
            alBuilder.setCancelable(false);
            try {
                alBuilder.show();
            }catch(Exception e) {
                return;
            }

            //인터넷 미연결로 인한 Down 시 예외처리
            downApp = !downApp;
            return;
        }

        //좌표 이동하는 스크립트 실행
        try {
            FragmentMap map = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mainMap);
            if(map == null) return;
            //내 위치 고정 시
            if (tackerOnOff) {
                try {
                    map.mWebView.loadUrl("javascript:android_receiveMSGWithCenter(" + lon + "," + lat + ")");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //내 위치 고정 해제 시
            else {
                try {
                    map.mWebView.loadUrl("javascript:android_receiveMSGExceptCenter(" + lon + "," + lat + ")");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //사진 찍은 후 FragmentContent 에 경로 보내기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //카메라 어플(1110), 갤러리 어플(1112)
        if(requestCode == 1110 || requestCode == 1112) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.slide_contents);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        //스크린샷
        else if(requestCode == SCREENSHOT_REQUEST_CODE) {
            //스크린샷 취소 시 대상지 표시만 보이기
            if(resultCode != RESULT_OK) {
                FragmentMap map = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mainMap);
                if(map == null) return;
                map.mWebView.loadUrl("javascript:android_receiveMSGPointVisible(true)");
                return;
            }

            //스크린샷 전 폴더 생성 및 확인
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            if (sMediaProjection != null) {
                String sidoCode = PreferenceManager.getString(getApplicationContext(),"sidoCode")+"/";
                File pictureStorage = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM), sidoCode);
                // 만약 장소가 존재하지 않는다면 폴더를 새롭게 만든다.
                if (!pictureStorage.exists()) {
                    boolean dirCreate = pictureStorage.mkdirs();
                    if(!dirCreate) {
                        Toast.makeText(getApplicationContext(),"오류 - 폴더 생성 실패", Toast.LENGTH_SHORT).show();
                    }
                }
                //스크린샷 파일 생성
                String STORE_DIRECTORY = sidoCode+FILENAME+"_screenShot.png";
                storeDirectory = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM), STORE_DIRECTORY);
                // display metrics
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                mDisplay = getWindowManager().getDefaultDisplay();
                //0.2초 대기 후 스크린샷
                try {
                    Thread.sleep(200);
                }catch(InterruptedException ex){ ex.getStackTrace();}

                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), null);

            }
        }
    }

    /*
     * 현재 화면 스크린샷 캡쳐 부분
     * 한번 실행 시 2~3번 스크린샷을 찍으나
     * 저장 파일 이름을 같게 하여 하나로 저장 됨
     * 스크린샷 평균 약 2.5초 소요
    */
    private static final int SCREENSHOT_REQUEST_CODE = 4532;
    private static String FILENAME;
    private static boolean spotTF;
    File storeDirectory;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;

    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private boolean screenTF = false;

    //스크린샷 캡쳐
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener{
        @Override
        public void onImageAvailable(ImageReader reader) {
            if(!screenTF) {
                Image image = null;
                FileOutputStream fos = null;
                Bitmap bitmap = null;

                try {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * mWidth;

                        // create bitmap
                        bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);

                        // write bitmap to a file
                        fos = new FileOutputStream(storeDirectory);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }

                stopProjection();
                //찍은 사진 BroadCast
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(storeDirectory));
                sendBroadcast(mediaScanIntent);

                //대상지 제거 체크 시 제거되었던 대상지 다시 보이는 스크립트 실행
                if (spotTF) {
                    FragmentMap map = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mainMap);
                    if(map == null) return;
                    map.mWebView.loadUrl("javascript:android_receiveMSGPointVisible(true)");
                }
                StyleableToast.makeText(getApplicationContext(), "스크린샷이 저장되었습니다.", Toast.LENGTH_LONG, R.style.mytoast).show();
                screenTF = !screenTF;
            }
        }
    }

    //종료 후 초기화
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            if (mVirtualDisplay != null) mVirtualDisplay.release();
            if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
            sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
        }
    }

    //스크린샷 시작
    public void startProjection(String fileName, boolean tf) {
        screenTF = false;
        FILENAME = fileName;
        spotTF = tf;

        //대상지 표시 제거 선택 시 웹서버의 표시 제거 스크립트 실행
        if(spotTF) {
            FragmentMap map = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mainMap);
            if(map == null) return;
            map.mWebView.loadUrl("javascript:android_receiveMSGPointVisible(false)");
        }
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), SCREENSHOT_REQUEST_CODE);
    }

    //스크린샷 종료
    private void stopProjection() {
        if (sMediaProjection != null) {
            sMediaProjection.stop();
        }
    }

    //스크린샷을 찍을 VirtualDisplay 생성
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, null);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), null);
    }

    @Override
    public void onBackPressed() {
        SlidingUpPanelLayout mLayout = findViewById(R.id.sliding_layout);
        if(mLayout.getPanelState() == PanelState.EXPANDED) {
            mLayout.setPanelState(PanelState.COLLAPSED);
            return;
        }
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("프로그램을 종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.

    }

    /*
     * 하단 사진 ListView 영역
     * FragmentContent 에 있는 리스트의 Adapter 와 ArrayList 를 MainActivity 에서 불러와
     * FragmentMap 에서 사용 할 수 있도록 함
    */

    public ArrayList<ListGetSet> getArrayList() {
        FragmentContent content = (FragmentContent) getSupportFragmentManager().findFragmentById(R.id.slide_contents);
        if(content == null) {
            return null;
        }
        else {
            return content.getArrayList();
        }
    }

    public CustomAdapter getAdapter() {
        FragmentContent content = (FragmentContent) getSupportFragmentManager().findFragmentById(R.id.slide_contents);
        if(content == null) {
            return null;
        }
        else {
            return content.getAdapter();
        }
    }
}
