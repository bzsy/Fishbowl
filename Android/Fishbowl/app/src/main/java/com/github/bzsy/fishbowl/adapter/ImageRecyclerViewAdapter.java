package com.github.bzsy.fishbowl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.bzsy.fishbowl.R;
import com.github.bzsy.fishbowl.sqlite.greendao.ImageEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by bzsy on 16/1/12.
 */
public class ImageRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ImageEntity> datas;

    public ImageRecyclerViewAdapter(Context context, List<ImageEntity> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        ImageEntity imageEntity = datas.get(position);
        ImageLoader.getInstance().displayImage(imageEntity.getImage_url(), viewHolder.ivImage);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setDatas(List<ImageEntity> datas) {
        this.datas = datas;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }
}
