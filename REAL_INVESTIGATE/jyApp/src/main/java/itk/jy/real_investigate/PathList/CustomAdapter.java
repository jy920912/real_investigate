package itk.jy.real_investigate.PathList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import itk.jy.real_investigate.R;

//사진전송 리스트
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    public interface OnListItemSelectedInterface{
        //FragmentContent 에 위치한 onItemSelected
        void onItemSelected(View v, int position);
    }
    private OnListItemSelectedInterface mListener;

    private ArrayList<ListGetSet> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private TextView filePath;
        private CheckBox cvA;
        private CheckBox cvB;


        public CustomViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.filename_listitem);
            filePath = view.findViewById(R.id.filepath_listitem);
            cvA = view.findViewById(R.id.checkboxA);
            cvB = view.findViewById(R.id.checkboxB);

            //클릭 시 해당 Path 의 사진을 보여줌
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.onItemSelected(v, position);
                }
            });

        }
    }


    public CustomAdapter(ArrayList<ListGetSet> list, OnListItemSelectedInterface listener) {
        this.mList = list;
        this.mListener = listener;
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
    public void onBindViewHolder(@NonNull final CustomViewHolder viewholder, final int position) {

        viewholder.fileName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        viewholder.filePath.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        viewholder.fileName.setGravity(Gravity.START);
        viewholder.filePath.setGravity(Gravity.START);

        viewholder.fileName.setText(mList.get(position).getFileName());
        viewholder.filePath.setText(mList.get(position).getFilePath());

        //원경 근경 체크박스 체크
        if(mList.get(position).getAorB() == 0){
            viewholder.cvA.setChecked(true);
            viewholder.cvB.setChecked(false);
        }
        else if(mList.get(position).getAorB() == 1) {
            viewholder.cvA.setChecked(false);
            viewholder.cvB.setChecked(true);
        }
        else if(mList.get(position).getAorB() == -1) {
            viewholder.cvA.setChecked(false);
            viewholder.cvB.setChecked(false);
        }

        //근경 체크박스 터치 시
        viewholder.cvA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewholder.cvB.isChecked()) {
                    viewholder.cvA.setChecked(!viewholder.cvA.isChecked());
                    return;
                }
                if(viewholder.cvA.isChecked()) {
                    mList.get(position).setAorB(0);
                }
                else {
                    mList.get(position).setAorB(-1);
                }
            }
        });

        //원경 체크박스 터치 시
        viewholder.cvB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewholder.cvA.isChecked()) {
                    viewholder.cvB.setChecked(!viewholder.cvB.isChecked());
                    return;
                }
                if(viewholder.cvB.isChecked()) {
                    mList.get(position).setAorB(1);
                }
                else {
                    mList.get(position).setAorB(-1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public Object getItem(int num) {
        return mList.get(num);
    }

}
