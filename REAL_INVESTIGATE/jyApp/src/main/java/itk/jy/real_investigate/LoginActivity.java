package itk.jy.real_investigate;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import itk.jy.real_investigate.Internet.InternetManager;
import itk.jy.real_investigate.Preference.PreferenceManager;

import static android.content.ContentValues.TAG;


public class LoginActivity extends AppCompatActivity {
    EditText loginText;
    EditText passwordText;
    CheckBox loginSave;
    Spinner sidoSpinner;
    String id; String pw;
    ArrayList<String> permissions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppBaseThemeBlu);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
            AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
            alBuilder.setMessage("이 어플리케이션은 안드로이드8.0(Oreo) 이상에서만 구동가능합니다.");

            alBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); // 현재 액티비티를 종료한다. (MainActivity 에서 작동하기 때문에 애플리케이션을 종료한다.)
                }
            });
            alBuilder.setTitle("안드로이드 버전 충돌");
            alBuilder.setCancelable(false);
            alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
        }
        //시도코드 콤보박스
        sidoSpinner = findViewById(R.id.sidocode);
        ArrayAdapter sidoAdapter = ArrayAdapter.createFromResource(this, R.array.sidocode_list,
                android.R.layout.simple_spinner_dropdown_item);
        sidoSpinner.setAdapter(sidoAdapter);

        //저번에 선택했던 시군 position 불러오기
        int selSido = PreferenceManager.getInt(getApplicationContext(),"sidoNum");
        if(selSido != -1) sidoSpinner.setSelection(selSido);

        Button loginButton  = findViewById(R.id.sign_in);
        Button exitButton = findViewById(R.id.exit);
        loginText = findViewById(R.id.app_id);
        passwordText = findViewById(R.id.password);
        loginSave = findViewById(R.id.loginSave);

        /*
         * 아이디 저장 체크 시 저장된 아이디 불러오기
         * 없을 시 빈칸 및 아이디 저장 체크박스 미체크
         */
        String savedId = PreferenceManager.getString(getApplication(),"idSave");
        loginText.setText(savedId);
        boolean saveIdCheck = PreferenceManager.getBoolean(getApplication(),"idSaveCheck");
        loginSave.setChecked(saveIdCheck);
        if(saveIdCheck) {
            passwordText.requestFocus();
        }

        //로그인버튼 클릭 시
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = null;
                id = loginText.getText().toString();
                try {
                    data = id.getBytes(StandardCharsets.UTF_8);
                }catch(Exception e){
                    e.getStackTrace();
                }
                String encId = Base64.encodeToString(data, Base64.NO_WRAP);
                pw = passwordText.getText().toString();
                try {
                    data = pw.getBytes(StandardCharsets.UTF_8);
                }catch(Exception e){
                    e.getStackTrace();
                }
                String encPw = Base64.encodeToString(data, Base64.NO_WRAP);
                URLConnector task = new URLConnector();
                task.execute(encId, encPw);
            }
        });
        //종료버튼 클릭 시
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //권한 부여
        //GPS 권한
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //모바일데이터 GPS 권한
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        //카메라 권한
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        //네트워크 접근 권한
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        //인터넷 권한
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }
        //메모리(디스크) 쓰기 권한
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //메모리(디스크) 읽기 권한
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(permissions.size() > 0) {
            String[] reqPermissionArray = new String[permissions.size()];
            reqPermissionArray = permissions.toArray(reqPermissionArray);
            ActivityCompat.requestPermissions(this, reqPermissionArray, 0);
        }

        //인터넷 연결 여부 확인
        internetCon();

    }
    public void internetCon() {
        int InternetConnection = InternetManager.getConnectivityStatus(this);
        if(InternetConnection == 3) {
            // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
            AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
            alBuilder.setMessage("인터넷이 연결되지 않았습니다. 인터넷을 연결하십시오.");

            alBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); // 현재 액티비티를 종료한다. (MainActivity 에서 작동하기 때문에 애플리케이션을 종료한다.)
                }
            });
            alBuilder.setTitle("인터넷 연결상태 확인");
            alBuilder.setCancelable(false);
            alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
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

    //아이디 비밀번호 확인 및 mainActivity 이동
    public class URLConnector extends AsyncTask<String, Void, String> {
        String res;

        //서버 접근 전
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            internetCon();
        }

        //서버 접근 후
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            res = result;
            if("1".equals(res)) {
                //시도코드 저장
                String sidocode = sidoSpinner.getSelectedItem().toString().substring(0,5);
                PreferenceManager.setInt(getApplication(),"sidoNum",sidoSpinner.getSelectedItemPosition());
                PreferenceManager.setString(getApplication(),"sidoCode",sidocode);
                //아이디 저장여부
                if(loginSave.isChecked()) {
                    PreferenceManager.setBoolean(getApplication(),"idSaveCheck",true);
                    PreferenceManager.setString(getApplication(),"idSave", id);
                }
                else {
                    PreferenceManager.setBoolean(getApplication(),"idSaveCheck",false);
                    PreferenceManager.setString(getApplication(),"idSave", "");
                }
                //MainActivity 이동
                StyleableToast.makeText(getApplication(),"로그인되었습니다.",Toast.LENGTH_LONG,R.style.mytoast).show();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fadeinlogin, R.anim.fadeoutlogin);
                finish();
            }
            else {
                StyleableToast.makeText(getApplication(),"아이디 혹은 비밀번호가 틀립니다.",Toast.LENGTH_LONG,R.style.mytoast).show();
            }
        }

        //서버 접근
        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://115.95.67.133:5088/real_investigate/jsp/idpw.jsp";
            String postParameters = "id=" + searchKeyword1 + "&pw=" + searchKeyword2;

            try {

                //서버 Connection
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                //데이터 가져오기
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                //일치 시 1, 불일치 시 0;
                res = sb.toString().trim();
                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return null;
            }
        }
    }
}
