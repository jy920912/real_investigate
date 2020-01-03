package itk.jy.real_investigate.Fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import itk.jy.real_investigate.MainActivity;
import itk.jy.real_investigate.MapService.screenShotContentDialogFragment;
import itk.jy.real_investigate.PathList.CustomAdapter;
import itk.jy.real_investigate.PathList.ListGetSet;
import itk.jy.real_investigate.Preference.PreferenceManager;
import itk.jy.real_investigate.R;

import static itk.jy.real_investigate.Preference.ConfigPreference.*;


public class FragmentMap extends Fragment {
    public static Context mContext;
    public WebView mWebView;
    private ImageButton GPSButton;
    private ImageButton PointButton;
    private ImageButton JibunButton;
    private static boolean pointTF = false;
    private static boolean jibunTF = false;
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
        jibunTF = selJbn;

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
        mWebView = rootView.findViewById(R.id.mWebView);

        mWebView.addJavascriptInterface(new AndroidBridge(), "android"); //Web에서 Android 함수 사용
        mWebView.setBackgroundColor(0x00000000);
        mWebView.setLayerType(WebView.LAYER_TYPE_HARDWARE,null);
        //mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings mWebSettings = mWebView.getSettings();
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
                    try {
                        ((MainActivity) getActivity()).tackerOnOff = true;
                        mWebView.loadUrl("javascript:android_receiveMSGWithCenter(" + location.getLongitude() + "," + location.getLatitude() + ")");
                        StyleableToast.makeText(mContext, "내위치 찾기 시작", Toast.LENGTH_LONG, R.style.gpsFixToast).show();
                        GPSButton.setImageResource(R.drawable.gps_fix);
                    }catch(Exception e) {
                        e.getStackTrace();
                    }
                }
                else {
                    //위치고정 X
                    try {
                        ((MainActivity) getActivity()).tackerOnOff = false;
                        mWebView.loadUrl("javascript:android_receiveMSGExceptCenter(" + location.getLongitude() + "," + location.getLatitude() + ")");
                        StyleableToast.makeText(mContext, "내위치 찾기 해제", Toast.LENGTH_LONG, R.style.gpsNonfixToast).show();
                        GPSButton.setImageResource(R.drawable.gps_nonfix);
                    }catch (Exception e) {
                        e.getStackTrace();
                    }
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

        //초기 지번 출력 상태
        JibunButton = rootView.findViewById(R.id.JibunVisible);
        if(jibunTF) {
            JibunButton.setImageResource(R.drawable.jibun_icon_b);
            jibunTF = !jibunTF;
        }
        else {
            JibunButton.setImageResource(R.drawable.jibun_icon_g);
            jibunTF = !jibunTF;
        }
        //지번 출력 유무 버튼 클릭 시
        JibunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:android_receiveMSGJibunVisible("+jibunTF+")");
                if(jibunTF) {
                    JibunButton.setImageResource(R.drawable.jibun_icon_b);
                }
                else {
                    JibunButton.setImageResource(R.drawable.jibun_icon_g);
                }
                setJibun(mContext, jibunTF);
                jibunTF = !jibunTF;

            }
        });

        return rootView;
    }

    /*
     * 웹에서 지적정보 가져오기
     * address = 지번주소
     * pnu     = 지번 pnu
     * cpnf    = 촬영 여부
     * sdnf    = 전송 여부
     * drnf    = 드론 촬영 여부
     * jimok   = 지목
     * jiga    = 지가
     * area    = 면적
     */
    public class AndroidBridge {
        @JavascriptInterface
        public void android_sendMSG(String address, String pnu, String cpnf,String sdnf, String drnf,String jimok, String jiga, String area) {
            final String addressText = address;
            final String pnuText = pnu;
            final String cpnfT = cpnf;
            final String sdnfT = sdnf;
            final String drnfT = drnf;
            if(pnuText.equals("undefined")) {
                StyleableToast.makeText(mContext,"선택한 대상지의 정보가 없습니다.", Toast.LENGTH_SHORT,R.style.mytoast).show();
                return;
            }
            ((MainActivity)getActivity()).onOff[0] = cpnf;
            ((MainActivity)getActivity()).onOff[1] = sdnf;
            ((MainActivity)getActivity()).onOff[2] = drnf;

            PreferenceManager.setString(mContext,"address",address);
            PreferenceManager.setString(mContext,"pnu",pnu);
            PreferenceManager.setString(mContext,"jimok",jimok);
            PreferenceManager.setString(mContext,"jiga",jiga);
            PreferenceManager.setString(mContext,"area",area);

            //FragmentContent 내 TextView, Switch 가져오기
            final TextView addressT = ((Activity)FragmentContent.mContext).findViewById(R.id.address);
            final TextView pnuT     = ((Activity)FragmentContent.mContext).findViewById(R.id.pnu);
            final CheckBox captureOnOffSw   = ((Activity)FragmentContent.mContext).findViewById(R.id.capture_switch);
            final CheckBox sendOnOffSw   = ((Activity)FragmentContent.mContext).findViewById(R.id.send_switch);
            final CheckBox dronOnOffSw   = ((Activity)FragmentContent.mContext).findViewById(R.id.dr_switch);

            //FragmentContent 에 내용 추가
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
                    //FragmentContent mArrayList 가져오기
                    ArrayList<ListGetSet> mArrayList = ((MainActivity)getActivity()).getArrayList();
                    mArrayList.clear();

                    //FragmentContent mAdapter 가져오기
                    CustomAdapter mAdapter = ((MainActivity)getActivity()).getAdapter();
                    String sidoCode = PreferenceManager.getString(getContext(),"sidoCode");

                    //해당 시군의 폴더 내부 탐색하여 전송리스트에 출력
                    File pictureStorage = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM), sidoCode);
                    myFilenameFilter filt = new myFilenameFilter(addressText);
                    File fList[] = pictureStorage.listFiles(filt);
                    for(int i=0;i<fList.length;i++) {
                        ListGetSet imageData = new ListGetSet(fList[i].getName(), fList[i].getPath(), -1);
                        mArrayList.add(imageData);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });

            //정보보기, 스크린샷 다이얼로그 열기
            screenShotContentDialogFragment frfr = screenShotContentDialogFragment.getInstance(addressText);
            frfr.show(getFragmentManager(),frfr.DIALOGNAME);
        }

        private class myFilenameFilter implements FilenameFilter {
            private String s;
            public myFilenameFilter(String ss) {
                s = ss;
            }
            public boolean accept(File dir, String name) {
                return (name.indexOf(s) != -1);
            }
        }

        //대상지 검색 결과 없는 경우
        @JavascriptInterface
        public void android_sendNoAddress(String address) {
            StyleableToast.makeText(mContext,address+" - 해당 주소를 찾을 수 없습니다.",Toast.LENGTH_LONG,R.style.mytoast).show();
        }
    }
}


