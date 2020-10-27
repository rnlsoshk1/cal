package com.untie.daywal.activity;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.untie.daywal.R;
import com.untie.daywal.application.ApplicationController;
import com.untie.daywal.database.DbOpenHelper;
import com.untie.daywal.main.ItemData;

import java.util.ArrayList;

/**
 * Created by ichaeeun on 2016. 11. 20..
 */

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.PopupViewHolder>{
    public int mSelectedItem = -1;
    Context context;
    ArrayList<ItemData> itemDatas;
    View.OnClickListener mOnClickListener;

    public PopupAdapter(Context context, ArrayList<ItemData> itemDatas, View.OnClickListener mOnClickListener) {
        this.context = context;
        this.itemDatas = itemDatas;
        this.mOnClickListener = mOnClickListener;
    }

    public void setAdaper(ArrayList<ItemData> itemDatas){
        this.itemDatas = itemDatas;
        notifyDataSetChanged();
    }

    @Override
    public PopupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_rv_item, parent,false);
        PopupViewHolder viewHolder = new PopupViewHolder(itemView);

        itemView.setOnClickListener(mOnClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PopupViewHolder holder, final int position) {
        Glide.with(context)
                .load(itemDatas.get(position).getDate())
                .into(holder.imageView);
        holder.title.setText(itemDatas.get(position).getTitle());
        holder.radioButton.setChecked(itemDatas.get(position).getIsChecked());
        Uri uri = Uri.parse(itemDatas.get(position).getImage());
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        if (itemDatas != null){
            return itemDatas.size();
        }
        return 0;
    }

    public ArrayList<ItemData> getItemDatas() {
        return itemDatas;
    }


    public class PopupViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title;
        public RadioButton radioButton;


        public PopupViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            radioButton = (RadioButton) itemView.findViewById(R.id.radioButton);
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    DbOpenHelper dbOpenHelper = ApplicationController.getInstance().mDbOpenHelper;

                    for (int i=0; i<itemDatas.size(); i++) {
                        ItemData itemData = itemDatas.get(i);
                        if (i!= mSelectedItem) {
                            itemData.setIschecked(false);
                            dbOpenHelper.DbUpdateRadio(itemData.getId(),false);
                        }
                        if (i==mSelectedItem) {
                            itemData.setIschecked(true);
                            dbOpenHelper.DbUpdateRadio(itemData.getId(),true);
                        }

                    }
                    notifyItemRangeChanged(0, itemDatas.size());
                }
            };

            radioButton.setOnClickListener(clickListener);


        }



    }

    public void dismissItem(int position) {

        ItemData itemData = itemDatas.get(position);
        int id = itemData.getId();

        DbOpenHelper dbOpenHelper = ApplicationController.getInstance().mDbOpenHelper;
        if (dbOpenHelper.DbDelete(id)){
            itemDatas.remove(position);
        }

        this.notifyItemRemoved(position);

    }
}
