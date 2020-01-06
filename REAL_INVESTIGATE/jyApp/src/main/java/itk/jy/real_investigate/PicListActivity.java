package itk.jy.real_investigate;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import itk.jy.real_investigate.PicList.listItem;
import itk.jy.real_investigate.PicList.listViewer;
import itk.jy.real_investigate.Preference.PreferenceManager;

import static android.content.ContentValues.TAG;

public class PicListActivity extends AppCompatActivity {
    GridView gridView;
    listAdapter lAdapter;
    RadioGroup picListGroup;
    Spinner umdSp;
    Spinner riSp;
    Context context;
    String sidoCode;
    String listS;
    String umdCode;
    String riCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piclist);
        context = this;
        picListGroup = findViewById(R.id.pic_listGroup);
        gridView = findViewById(R.id.list_gridView);
        umdSp = findViewById(R.id.umd_spinner);
        riSp = findViewById(R.id.ri_spinner);

        sidoCode = PreferenceManager.getString(this, "sidoCode");
        listS = PreferenceManager.getString(this,"listSEE");
        if("".equals(listS)) listS = "A";

        //전체, 촬영완료, 촬영미완료 선택
        int list_R_Btn = 0;
        switch (listS) {
            case "A" :
                list_R_Btn = R.id.pic_listGroup1;
                break;
            case "O":
                list_R_Btn = R.id.pic_listGroup2;
                break;
            case "X":
                list_R_Btn = R.id.pic_listGroup3;
                break;
        }
        picListGroup.check(list_R_Btn);

        //목록 불러오기
        lAdapter = new listAdapter();
        PicListActivity.URLConnector task = new PicListActivity.URLConnector();
        task.execute(sidoCode, "", "umd");

        //라디오그룹 선택 시
        picListGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.pic_listGroup1 :
                        listS = "A";
                        break;
                    case R.id.pic_listGroup2:
                        listS = "O";
                        break;
                    case R.id.pic_listGroup3:
                        listS = "X";
                        break;

                }
                PreferenceManager.setString(context, "listSEE", listS);
                PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                task.execute(sidoCode, listS, "list", umdCode, riCode);
            }
        });

        ImageButton backButton = findViewById(R.id.list_Back_Button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //읍면동 선택 시
        umdSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //읍면동 코드 추출 후 목록 불러오기
                umdCode = umdSp.getItemAtPosition(position).toString().substring(0,3);
                PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                task.execute(sidoCode, umdCode, "ri");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //리 선택 시
        riSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //리 코드 추출 후 목록 불러오기
                riCode = riSp.getItemAtPosition(position).toString().substring(0,2);
                PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                task.execute(sidoCode, listS, "list", umdCode, riCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //Adapter
    class listAdapter extends BaseAdapter {
        ArrayList<listItem> items = new ArrayList<listItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(listItem singerItem){
            items.add(singerItem);
        }

        @Override
        public listItem getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        //출력 시
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            listViewer lViewer = new listViewer(getApplicationContext());
            lViewer.setItem(items.get(i));

            if("미완료".equals(items.get(i).getPic())) lViewer.setColorItem(R.color.rrrred);
            else if("완료".equals(items.get(i).getPic())) lViewer.setColorItem(R.color.bbbblue);

            return lViewer;
        }
    }

    //읍면동리에 따라 리스트 출력
    public class URLConnector extends AsyncTask<String, Void, String> {
        String res;
        int whatwhat;

        //서버 접속 전
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //서버 접속 후
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            res = result;
            lAdapter.items.clear();
            if(res != null) {
                if("".equals(res)) {
                    StyleableToast.makeText(getApplicationContext(), "자료가 없습니다.", Toast.LENGTH_LONG,R.style.mytoast).show();
                }
                //대상지 데이터 가져올 때
                else if(whatwhat == 0) {
                    String[] splitText = res.split(",");
                    if("ERROR".equals(splitText[0])) {
                        Log.d(TAG,"Extract Sido ERROR");
                    }
                    else {
                        for (int i = 0; i < splitText.length; i = i + 3) {
                            String yesNo;
                            if ("X".equals(splitText[i + 1]) && "X".equals(splitText[i + 2])) {
                                yesNo = "미완료";
                            } else {
                                yesNo = "완료";
                            }
                            lAdapter.addItem(new listItem(splitText[i], yesNo));
                        }
                    }
                }
                //읍면동 검색 후
                else if(whatwhat == 1) {
                    ArrayList arrList = new ArrayList<>();
                    String[] splitText = res.split(",");
                    //읍면동 스피너에 입력
                    for(int i=2;i<splitText.length;i= i+2) {
                        String spinnerString = splitText[i]+"("+splitText[i+1]+")";
                        arrList.add(spinnerString);
                    }

                    ArrayAdapter arrAdapter= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrList);
                    umdSp.setAdapter(arrAdapter);

                    //선택 된 읍면동에 따른 리 검색
                    umdCode = umdSp.getSelectedItem().toString().substring(0,3);
                    PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                    task.execute(sidoCode, umdCode, "ri");
                }
                //리 검색 후
                else if(whatwhat == 2) {
                    ArrayList arrList = new ArrayList<>();
                    String[] splitText = res.split(",");
                    //리 스피너에 입력
                    for(int i=2;i<splitText.length-2;i= i+2) {
                        String spinnerString = splitText[i]+"("+splitText[i+1]+")";
                        arrList.add(spinnerString);
                    }
                    ArrayAdapter arrAdapter= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrList);
                    riSp.setAdapter(arrAdapter);

                    //선택 된 리에 따른 대상지 데이터 검색
                    if(!arrList.isEmpty()) riCode = riSp.getSelectedItem().toString().substring(0,2);
                    else riCode = "00";
                    PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                    task.execute(sidoCode, listS, "list", umdCode, riCode);
                }
            }
            else {
                StyleableToast.makeText(getApplicationContext(), "인터넷 상태를 확인해주세요.", Toast.LENGTH_LONG,R.style.mytoast).show();
            }
            gridView.setAdapter(lAdapter);
        }

        //서버 접속
        @Override
        protected String doInBackground(String... params) {
            //시도코드
            String searchKeyword1 = params[0];
            //스위치(전체, 촬영완료, 미완료) 혹은 UMD RI
            String searchKeyword2 = params[1];
            //리스트 출력, 읍면동, 리
            String taskWhat = params[2];
            String serverURL;
            String postParameters;

            //대상지 리스트 출력
            if("list".equals(taskWhat)) {
                //읍면동 코드
                String searchKeyword3 = params[3];
                //리 코드
                String searchKeyword4 = params[4];
                serverURL = "http://115.95.67.133:5088/real_investigate/jsp/list.jsp";
                postParameters = "sido=" + searchKeyword1 + "&switch=" + searchKeyword2 + "&umd=" + searchKeyword3 + "&ri=" + searchKeyword4;
                //서버 접속 후 실행할 스위치(리스트 출력)
                whatwhat = 0;
            }
            //읍면동 리스트 출력
            else if("umd".equals(taskWhat)) {
                serverURL = "http://115.95.67.133:5088/real_investigate/jsp/umdri.jsp";
                postParameters = "sido=" + searchKeyword1 + "&umd=" + searchKeyword2;
                //서버 접속 후 실행할 스위치(읍면동 스피너 입력)
                whatwhat = 1;
            }
            //리 리스트 출력
            else if("ri".equals(taskWhat)) {
                serverURL = "http://115.95.67.133:5088/real_investigate/jsp/umdri.jsp";
                postParameters = "sido=" + searchKeyword1 + "&umd=" + searchKeyword2;
                //서버 접속 후 실행할 스위치(리 스피너 입력)
                whatwhat = 2;
            }
            else {
                return null;
            }

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

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
                String cline;
                while((line = bufferedReader.readLine()) != null){
                    if(line.equals("")) continue;
                        cline = line;
                        sb.append(cline);
                }

                bufferedReader.close();

                res = sb.toString().trim();
                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return null;
            }
        }
    }
}
