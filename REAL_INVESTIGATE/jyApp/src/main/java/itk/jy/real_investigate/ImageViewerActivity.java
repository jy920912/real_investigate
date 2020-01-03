package itk.jy.real_investigate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ImageViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        Intent addressIntent = getIntent();

        //ContentFragment 에서 선택한 filePath 가져옴
        String filePath = addressIntent.getStringExtra("filePath");
        if(filePath == null) return;

        //사진 출력
        File imgFile = new File(filePath);
        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            double myWidth = myBitmap.getWidth();
            double myHeight = myBitmap.getHeight();
            Matrix mtx = new Matrix();
            if(myWidth > myHeight) mtx.postRotate(90);
            else mtx.postRotate(0);
            Bitmap rtBitmap = Bitmap.createBitmap(myBitmap,0,0,myBitmap.getWidth(),myBitmap.getHeight(),mtx,true);
            ImageView myImage = findViewById(R.id.picture_view);
            myImage.setImageBitmap(rtBitmap);
        }
    }
}
