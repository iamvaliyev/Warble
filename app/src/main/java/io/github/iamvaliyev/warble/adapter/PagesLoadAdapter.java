package io.github.iamvaliyev.warble.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.iamvaliyev.warble.R;
import io.github.iamvaliyev.warble.model.ItemModel;

public class PagesLoadAdapter extends RecyclerView.Adapter {

    Context context;

    public PagesLoadAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 100;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_pages, viewGroup, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {}

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgPage)
        public ImageView imageView;

        @BindView(R.id.lnLoad)
        public LinearLayout lnLoad;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}