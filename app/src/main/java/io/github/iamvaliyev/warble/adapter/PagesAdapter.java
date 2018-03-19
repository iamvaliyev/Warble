package io.github.iamvaliyev.warble.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.orm.util.NamingHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.iamvaliyev.warble.R;
import io.github.iamvaliyev.warble.model.ItemModel;
import io.github.iamvaliyev.warble.model.Page;
import io.github.iamvaliyev.warble.widget.CheckedImageView;

public class PagesAdapter extends RecyclerView.Adapter {

    private List<ItemModel> itemModels;

    MuPDFCore muPDFCore;
    Cookie cookie;

    Context context;

    String name;

    long id;
    int recent;

    public PagesAdapter(Context context, File filePath, String fileName, String name, long id, int recent) {
        this.context = context;
        this.name = name;
        this.id = id;
        this.recent = recent;

        muPDFCore = new MuPDFCore(new File(filePath, fileName).getAbsolutePath());
        cookie = new Cookie();

        itemModels = new ArrayList<>();
        for (int i = 0; i < muPDFCore.countPages(); i++) {
            itemModels.add(new ItemModel(i, false));
        }
    }

    @Override
    public int getItemCount() {
        return recent != -1 ? itemModels.size() + 1 : itemModels.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_pages, viewGroup, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        itemViewHolder.lnLoad.setVisibility(View.VISIBLE);

        try {
            List<Page> pages = Select.from(Page.class).where(Condition.prop(NamingHelper.toSQLNameDefault("bookId")).eq(id), Condition.prop(NamingHelper.toSQLNameDefault("page")).eq(position)).list();

            if (pages.isEmpty())
                itemViewHolder.imgBookmark.setImageDrawable(null);
            else
                itemViewHolder.imgBookmark.setImageResource(R.drawable.ic_bookmark_page);
        } catch (Exception e) {
            itemViewHolder.imgBookmark.setImageDrawable(null);
        }


        File sd = Environment.getExternalStorageDirectory();
        File dest = null;
        if (recent != -1 && position == 0) {
            itemViewHolder.txtHover.setVisibility(View.VISIBLE);
            dest = new File(sd, name + recent);
        } else if (recent != -1 && position != 0) {
            itemViewHolder.txtHover.setVisibility(View.GONE);
            dest = new File(sd, name + (position - 1));
        } else {

            itemViewHolder.txtHover.setVisibility(View.GONE);
            dest = new File(sd, name + position);
        }

        Glide.with(context).load(dest.getAbsolutePath()).placeholder(null).diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                itemViewHolder.lnLoad.setVisibility(View.GONE);
                return false;
            }
        }).into(itemViewHolder.imageView);

//        new PageRenderer(itemViewHolder.imageView, itemViewHolder.lnLoad, position).execute(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgPage)
        public ImageView imageView;

        @BindView(R.id.imgBookmark)
        public ImageView imgBookmark;

        @BindView(R.id.txtHover)
        public TextView txtHover;

        @BindView(R.id.lnLoad)
        public LinearLayout lnLoad;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class PageRenderer extends AsyncTask<Integer, Integer, Bitmap> {

        ImageView imageView;
        LinearLayout lnLoaad;
        int position;

        public PageRenderer(ImageView imageView, LinearLayout lnLoaad, int position) {
            this.imageView = imageView;
            this.lnLoaad = lnLoaad;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lnLoaad.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(null);
        }

        protected Bitmap doInBackground(Integer... positions) {
            Bitmap bitmap = null;
            try {
                bitmap = Bitmap.createBitmap(152, 216, Bitmap.Config.ARGB_8888);
                muPDFCore.drawPage(bitmap, position, 152, 216, 0, 0, 152, 216, cookie);
            } catch (Exception e) {

            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            lnLoaad.setVisibility(View.GONE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Glide.with(context).load(byteArray).placeholder(null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}