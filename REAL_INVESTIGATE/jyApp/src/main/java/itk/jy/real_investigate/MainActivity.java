package itk.jy.real_investigate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;

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
    public SlidingUpPanelLayout mLayout;

    GPS gpsCkeck;
    Location location;
    public boolean tackerOnOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        //gps 준비
        gpsCkeck  = new GPS(MainActivity.this);


        //slide 화면에 컨텐츠 정보 출력 위한 content fragment 출력
        FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
        contentTransaction.replace(R.id.slide_contents, new FragmentContent()).commit();

        //main화면에 map 출력 위한 mapview fragment 출력
        FragmentTransaction mapTransaction = getSupportFragmentManager().beginTransaction();
        mapTransaction.replace(R.id.mainMap, new FragmentMap()).commit();

        //slide panel
        mLayout = findViewById(R.id.sliding_layout);
        //초기 아래에서 삐져나오는 높이 설정
        mLayout.setPanelHeight(30);

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
                     FragmentMap map = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mainMap);
                    try {
                        map.mWebView.loadUrl("javascript:android_receiveSearch('"+searchT+"')");
                    }catch(Exception e){
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
                overridePendingTransition(R.anim.leftin,R.anim.leftout);
            }
        });

    }

    //화면전환 시 새로고침 막기
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    //좌표 가져오기
    public Location func_init_location() {
        location = gpsCkeck.getLocation();
        return location;
    }

    //웹에 좌표 보내기(javascript 이용)
    public void func_output_lonlat(double lon, double lat) {
        try {
            FragmentMap map = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mainMap);
            if (tackerOnOff) {
                try {
                    map.mWebView.loadUrl("javascript:android_receiveMSGWithCenter(" + lon + "," + lat + ")");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.slide_contents);
        if(fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
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
}
