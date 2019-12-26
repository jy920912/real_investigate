package itk.jy.real_investigate.PicList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import itk.jy.real_investigate.R;


public class listViewer extends LinearLayout {

    TextView textView;
    TextView textView2;
    public listViewer(Context context) {
        super(context);

        init(context);
    }

    public listViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null) return;
        inflater.inflate(R.layout.listitem,this,true);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
    }

    public void setItem(listItem singerItem){
        textView.setText(singerItem.getAddr());
        textView2.setText(singerItem.getPic());
    }

}
