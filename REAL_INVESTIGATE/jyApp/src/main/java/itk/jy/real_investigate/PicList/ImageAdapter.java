package itk.jy.real_investigate.PicList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import itk.jy.real_investigate.R;

public class ImageAdapter extends BaseAdapter {

    private ArrayList<String> photoList;
    private LayoutInflater mInflater;
    private Context mContext;

    /**
     * RecyclerAdapter 생성자
     * */
    public ImageAdapter(ArrayList<String> photoList, Context context) {
        mInflater = LayoutInflater.from(context);
        this.photoList = photoList;
        this.mContext = context;
    }

    /**
     * Layout 을 ViewHolder 에 저장
     * */
    @Override
    public int getCount() {
        return photoList.size();
    }
    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.picture_row,parent,false);
        }
        ImageView image = convertView.findViewById(R.id.show_picture_view);
        String photo = photoList.get(position);
        Glide.with(mContext)
                .load(photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .centerCrop()
                .placeholder(R.drawable.jijuk_icon)
                .error(R.drawable.jibun_icon)
                .into(image);
        return convertView;
    }
}
