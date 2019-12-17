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
        String filePath = addressIntent.getStringExtra("filePath");
        File imgFile = new File(filePath);
        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            Bitmap rtBitmap = Bitmap.createBitmap(myBitmap,0,0,myBitmap.getWidth(),myBitmap.getHeight(),mtx,true);
            ImageView myImage = (ImageView) findViewById(R.id.picture_view);

            myImage.setImageBitmap(rtBitmap);
        }
    }
}
