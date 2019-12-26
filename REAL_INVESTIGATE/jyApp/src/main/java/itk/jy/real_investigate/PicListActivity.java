package itk.jy.real_investigate;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import itk.jy.real_investigate.PicList.listItem;
import itk.jy.real_investigate.PicList.listViewer;
import itk.jy.real_investigate.Preference.PreferenceManager;

public class PicListActivity extends AppCompatActivity {
    GridView gridView;
    listAdapter lAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piclist);

        gridView = findViewById(R.id.list_gridView);
        lAdapter = new listAdapter();
        lAdapter.addItem(new listItem("새롬동 2201-1","촬영"));
        lAdapter.addItem(new listItem("새롬동 2202-1","미촬영"));
        gridView.setAdapter(lAdapter);
        ImageButton backButton = findViewById(R.id.list_Back_Button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
            return lViewer;
        }
    }

    @Override
    public void onBackPressed() {
        //MainActivity 로 돌아감

        super.onBackPressed();
    }

}
