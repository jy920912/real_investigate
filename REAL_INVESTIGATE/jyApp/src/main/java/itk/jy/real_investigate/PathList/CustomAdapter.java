package itk.jy.real_investigate.PathList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import itk.jy.real_investigate.R;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<ListGetSet> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private TextView filePath;


        public CustomViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.filename_listitem);
            filePath = view.findViewById(R.id.filepath_listitem);
        }
    }


    public CustomAdapter(ArrayList<ListGetSet> list) {
        this.mList = list;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.fileName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        viewholder.filePath.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        viewholder.fileName.setGravity(Gravity.START);
        viewholder.fileName.setGravity(Gravity.START);



        viewholder.fileName.setText(mList.get(position).getFileName());
        viewholder.filePath.setText(mList.get(position).getFilePath());


    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public Object getItem(int num) {
        return mList.get(num);
    }

}
