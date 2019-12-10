package itk.jy.real_investigate.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import itk.jy.real_investigate.MainActivity;
import itk.jy.real_investigate.Preference.PreferenceManager;
import itk.jy.real_investigate.R;
import itk.jy.real_investigate.library.SlidingUpPanelLayout;

import static itk.jy.real_investigate.Preference.ConfigPreference.*;


public class FragmentMap extends Fragment {
    public static Context mContext;
    public WebView mWebView;
    private ImageButton GPSButton;
    private ImageButton PointButton;
    private WebSettings mWebSettings;
    private static boolean pointTF = false;
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        double lon = 0;
        double lat = 0;
        Location location;
        mContext = container.getContext();

        //preference에 저장된 자료 불러오기
        String sido = PreferenceManager.getString(mContext, "sidoCode");
        String selMap = getSelectMap(mContext);
        String selGps = getGpsFix(mContext);
        boolean selJjk = getJijuk(mContext);
        boolean selJbn = getJibun(mContext);

        //GPS좌표 불러오기
        try {
            location = ((MainActivity) getActivity()).func_init_location();
        }catch(Exception e){location = null;}
        if(location != null) {
            lon = location.getLongitude();
            lat = location.getLatitude();
        }

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //WebView 세팅
        mWebView = (WebView) rootView.findViewById(R.id.mWebView);

        mWebView.enableSlowWholeDocumentDraw();
        mWebView.addJavascriptInterface(new AndroidBridge(), "android"); //Web에서 Android 함수 사용
        mWebView.setBackgroundColor(0x00000000);
        mWebView.setLayerType(WebView.LAYER_TYPE_HARDWARE,null);
        //mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true); //javascript 실행
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //window.open()을 사용할 수 있도록 설정
        mWebSettings.setSupportMultipleWindows(false); //여러개 윈도우 사용 제한
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true); //html 컨텐츠가 webview에 맞게 나타나게 함
        mWebSettings.setSupportZoom(false); //확대 축소 기능 제한
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //캐시 미사용
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setGeolocationEnabled(true);
        mWebSettings.setAllowFileAccessFromFileURLs(true);
        mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        //javascript 실행
        mWebView.loadUrl("http://115.95.67.133:5088/real_investigate/main.html?" +
                           "lon="+lon+"&lat="+lat+"&sido="+sido+"&map="+selMap+"&jjk="+selJjk+"&jbn="+selJbn);

        //위치고정버튼 변경
        GPSButton = rootView.findViewById(R.id.GPSSelect);
        if("FIX".equals(selGps)) {
            ((MainActivity) getActivity()).tackerOnOff = true;
            GPSButton.setImageResource(R.drawable.gps_fix);
        }
        else {
            ((MainActivity) getActivity()).tackerOnOff = false;
            GPSButton.setImageResource(R.drawable.gps_nonfix);
        }
        //위치고정 버튼 클릭 시
        GPSButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Location location = ((MainActivity)getActivity()).func_init_location();
                if(!((MainActivity)getActivity()).tackerOnOff) {
                    //위치고정 O
                    ((MainActivity) getActivity()).tackerOnOff = true;
                    mWebView.loadUrl("javascript:android_receiveMSGWithCenter("+location.getLongitude()+","+location.getLatitude()+")");
                    StyleableToast.makeText(mContext,"내위치 찾기 시작", Toast.LENGTH_LONG, R.style.gpsFixToast).show();
                    GPSButton.setImageResource(R.drawable.gps_fix);
                }
                else {
                    //위치고정 X
                    ((MainActivity) getActivity()).tackerOnOff = false;
                    mWebView.loadUrl("javascript:android_receiveMSGExceptCenter("+location.getLongitude()+","+location.getLatitude()+")");
                    StyleableToast.makeText(mContext,"내위치 찾기 해제", Toast.LENGTH_LONG,R.style.gpsNonfixToast).show();
                    GPSButton.setImageResource(R.drawable.gps_nonfix);
                }
            }
        });

        //촬영유무 표시 버튼 클릭 시
        PointButton = rootView.findViewById(R.id.PointVisible);
        PointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:android_receiveMSGPointVisible("+pointTF+")");
                if(pointTF) {
                    PointButton.setImageResource(R.drawable.point_visible);
                }
                else {
                    PointButton.setImageResource(R.drawable.point_invisible);
                }
                pointTF = !pointTF;
            }
        });

        return rootView;
    }

    //webView 현재 화면 캡쳐
    public void webViewCapture(String FileName) {
        final String fName = FileName;
        /*View v1 = getActivity().getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        v1.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);*/


        mWebView.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mWebView.layout(0, 0, mWebView.getMeasuredWidth(), mWebView.getMeasuredHeight());
        mWebView.setDrawingCacheEnabled(true);
        mWebView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(mWebView.getMeasuredWidth(),
                mWebView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int iHeight = bitmap.getHeight();
        canvas.drawBitmap(bitmap, 0, iHeight, paint);
        mWebView.draw(canvas);

        FileOutputStream fos = null;
        File pictureStorage = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM), "MyCameraView/");
        // 만약 장소가 존재하지 않는다면 폴더를 새롭게 만든다.
        if (!pictureStorage.exists()) {
            pictureStorage.mkdirs();
        }

        String strFilePath = pictureStorage + "/" + fName + ".png";
        File fileCacheItem = new File(strFilePath);

        try {
            fos = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(fileCacheItem));
        getActivity().sendBroadcast(mediaScanIntent);

    }

    //웹에서 지적정보 가져오기
    public class AndroidBridge {
        @JavascriptInterface
        public void android_sendMSG(String address, String pnu, String cpnf,String sdnf, String drnf) {
            final String addressText = address;
            final String pnuText = pnu;
            final String cpnfT = cpnf;
            final String sdnfT = sdnf;
            final String drnfT = drnf;
            webViewCapture(address);
            ((MainActivity)getActivity()).onOff[0] = cpnf;
            ((MainActivity)getActivity()).onOff[1] = sdnf;
            ((MainActivity)getActivity()).onOff[2] = drnf;

            //FragmentContent 내 TextView, Switch 가져오기
            final TextView addressT = ((Activity)FragmentContent.mContext).findViewById(R.id.address);
            final TextView pnuT     = ((Activity)FragmentContent.mContext).findViewById(R.id.pnu);
            final Switch captureOnOffSw   = ((Activity)FragmentContent.mContext).findViewById(R.id.capture_switch);
            final Switch sendOnOffSw   = ((Activity)FragmentContent.mContext).findViewById(R.id.send_switch);
            final Switch dronOnOffSw   = ((Activity)FragmentContent.mContext).findViewById(R.id.dron_switch);

            //FragmentConent에 내용 추가
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addressT.setText(addressText);
                    pnuT.setText(pnuText);
                    if ("O".equals(cpnfT)) {
                        captureOnOffSw.setChecked(true);
                    } else if ("X".equals(cpnfT)) {
                        captureOnOffSw.setChecked(false);
                    }
                    if ("O".equals(sdnfT)) {
                        sendOnOffSw.setChecked(true);
                    } else if ("X".equals(sdnfT)) {
                        sendOnOffSw.setChecked(false);
                    }
                    if ("O".equals(drnfT)) {
                        dronOnOffSw.setChecked(true);
                    } else if ("X".equals(drnfT)) {
                        dronOnOffSw.setChecked(false);
                    }
                }
            });
            //slideUp
            SlidingUpPanelLayout mLayout = getActivity().findViewById(R.id.sliding_layout);
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        @JavascriptInterface
        public void android_sendNoAddress(String address) {
            StyleableToast.makeText(mContext,address+" - 해당 주소를 찾을 수 없습니다.",Toast.LENGTH_LONG,R.style.mytoast).show();
        }
    }
}


