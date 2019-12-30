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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
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
        if("".equals(listS)) listS = "X";
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
        lAdapter = new listAdapter();
        PicListActivity.URLConnector task = new PicListActivity.URLConnector();
        task.execute(sidoCode, "", "umd");


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
        umdSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                umdCode = umdSp.getItemAtPosition(position).toString().substring(0,3);
                PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                task.execute(sidoCode, umdCode, "ri");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        riSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                riCode = riSp.getItemAtPosition(position).toString().substring(0,2);
                PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                task.execute(sidoCode, listS, "list", umdCode, riCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            listViewer lViewer = new listViewer(getApplicationContext());
            lViewer.setItem(items.get(i));

            if("미완료".equals(items.get(i).getPic())) lViewer.setColorItem(R.color.rrrred);
            else if("완료".equals(items.get(i).getPic())) lViewer.setColorItem(R.color.bbbblue);

            return lViewer;
        }
    }

    @Override
    public void onBackPressed() {
        //MainActivity 로 돌아감

        super.onBackPressed();
    }

    //아이디 비밀번호 확인 및 mainActivity 이동
    public class URLConnector extends AsyncTask<String, Void, String> {
        String res;
        int whatwhat;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            res = result;
            lAdapter.items.clear();
            if(res != null) {
                if("".equals(res)) {
                    StyleableToast.makeText(getApplicationContext(), "자료가 없습니다.", Toast.LENGTH_LONG,R.style.mytoast);
                }
                else if(whatwhat == 0) {
                    String[] splitText = res.split(",");
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
                else if(whatwhat == 1) {
                    ArrayList arrList = new ArrayList<>();
                    String[] splitText = res.split(",");
                    for(int i=2;i<splitText.length-2;i= i+2) {
                        String spinnerString = splitText[i]+"("+splitText[i+1]+")";
                        arrList.add(spinnerString);
                    }
                    ArrayAdapter arrAdapter= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrList);
                    umdSp.setAdapter(arrAdapter);
                    umdCode = umdSp.getSelectedItem().toString().substring(0,3);
                    PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                    task.execute(sidoCode, umdCode, "ri");
                }
                else if(whatwhat == 2) {
                    ArrayList arrList = new ArrayList<>();
                    String[] splitText = res.split(",");
                    for(int i=2;i<splitText.length-2;i= i+2) {
                        String spinnerString = splitText[i]+"("+splitText[i+1]+")";
                        arrList.add(spinnerString);
                    }
                    ArrayAdapter arrAdapter= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrList);
                    riSp.setAdapter(arrAdapter);
                    if(!arrList.isEmpty()) riCode = riSp.getSelectedItem().toString().substring(0,2);
                    else riCode = "00";
                    PicListActivity.URLConnector task = new PicListActivity.URLConnector();
                    task.execute(sidoCode, listS, "list", umdCode, riCode);
                }
            }
            else {
                StyleableToast.makeText(getApplicationContext(), "인터넷 상태를 확인해주세요.", Toast.LENGTH_LONG,R.style.mytoast);
            }
            gridView.setAdapter(lAdapter);
        }

        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];
            String taskWhat = params[2];
            String serverURL = "";
            String postParameters = "";
            if("list".equals(taskWhat)) {
                String searchKeyword3 = params[3];
                String searchKeyword4 = params[4];
                serverURL = "http://115.95.67.133:5088/real_investigate/jsp/list.jsp";
                postParameters = "sido=" + searchKeyword1 + "&switch=" + searchKeyword2 + "&umd=" + searchKeyword3 + "&ri=" + searchKeyword4;
                whatwhat = 0;
            }
            else if("umd".equals(taskWhat)) {
                serverURL = "http://115.95.67.133:5088/real_investigate/jsp/umdri.jsp";
                postParameters = "sido=" + searchKeyword1 + "&umd=" + searchKeyword2;
                whatwhat = 1;
            }
            else if("ri".equals(taskWhat)) {
                serverURL = "http://115.95.67.133:5088/real_investigate/jsp/umdri.jsp";
                postParameters = "sido=" + searchKeyword1 + "&umd=" + searchKeyword2;
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
                outputStream.write(postParameters.getBytes("UTF-8"));
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


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    if(line.equals("")) continue;
                        String cline = line;
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
