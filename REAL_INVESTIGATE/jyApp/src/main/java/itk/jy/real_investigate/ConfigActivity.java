package itk.jy.real_investigate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import itk.jy.real_investigate.Fragment.FragmentConfig;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //설정 fragment 등록
        FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
        contentTransaction.replace(R.id.configLayout, new FragmentConfig()).commit();
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //MainActicity로 돌아감
        Intent mainIntent = new Intent(getApplication(), MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.leftin,R.anim.leftout);
        super.onBackPressed();
    }

}
