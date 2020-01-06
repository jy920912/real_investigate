package itk.jy.real_investigate;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

import itk.jy.real_investigate.PicList.ImageAdapter;
import itk.jy.real_investigate.Preference.PreferenceManager;

public class PictureGridActivity extends AppCompatActivity {
    private GridView picture_list_view;
    private ImageButton back_button;
    private TextView dirText;

    ArrayList<String> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview);

        back_button = findViewById(R.id.pg_backButton);
        dirText = findViewById(R.id.pg_dirText);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //해당 시군의 폴더 내부 탐색
        String sidoCode = PreferenceManager.getString(this,"sidoCode");
        dirText.setText(sidoCode);
        File pictureStorage = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM), sidoCode);
        if (! pictureStorage.exists()){
            if (! pictureStorage.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
            }
        }

        File fList[] = pictureStorage.listFiles();
        for(int i=0;i<fList.length;i++) {
            photoList.add(fList[i].toString());
        }
        picture_list_view = findViewById(R.id.picture_recycle_view);
        ImageAdapter mCustomImageAdapter = new ImageAdapter(photoList, this);
        picture_list_view.setAdapter(mCustomImageAdapter);

    }

}
