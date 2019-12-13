package itk.jy.real_investigate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

import itk.jy.real_investigate.Preference.PreferenceManager;

public class MoreContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morecontent);
        TextView addressText = findViewById(R.id.more_address);
        TextView pnuText = findViewById(R.id.more_pnu);
        TextView jimokText = findViewById(R.id.ji_mok);
        TextView jigaText = findViewById(R.id.ji_ga);
        TextView areaText = findViewById(R.id.area);
        ImageButton backB = findViewById(R.id.more_backButton);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        DecimalFormat jigaFormat = new DecimalFormat("###,###");
        DecimalFormat areaFormat = new DecimalFormat("###,###.#");
        int i_jiga;
        try {
            i_jiga = Integer.parseInt(PreferenceManager.getString(getApplicationContext(), "jiga"));
        }catch(Exception e) {
            i_jiga = 0;
        }
        String jiga = jigaFormat.format(i_jiga) +" (원)";
        double i_area;
        try {
            i_area = Double.parseDouble(PreferenceManager.getString(getApplicationContext(), "area"));
        }catch(Exception e) {
            i_area = 0;
        }
        String area = areaFormat.format(i_area)+" (㎡)";
        addressText.setText(PreferenceManager.getString(getApplicationContext(),"address"));
        pnuText.setText(PreferenceManager.getString(getApplicationContext(),"pnu"));
        jimokText.setText(PreferenceManager.getString(getApplicationContext(),"jimok"));
        jigaText.setText(jiga);
        areaText.setText(area);
    }

    @Override
    public void onBackPressed() {
        //MainActivity 로 돌아감

        super.onBackPressed();
    }

}
