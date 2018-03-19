package io.github.iamvaliyev.warble.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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

import io.github.iamvaliyev.warble.R;
import io.github.iamvaliyev.warble.holder.SelectableViewHolder;
import io.github.iamvaliyev.warble.model.Item;
import io.github.iamvaliyev.warble.model.SelectableItem;

public class SelectableAdapter extends RecyclerView.Adapter implements SelectableViewHolder.OnItemSelectedListener {

    private final List<SelectableItem> mValues;
    private boolean isMultiSelectionEnabled = false;
    SelectableViewHolder.OnItemSelectedListener listener;

    File filePath;
    String fileName;

    MuPDFCore muPDFCore;
    Cookie cookie;

    Context context;

    SelectableItem lastSelected = null;

    public SelectableAdapter(SelectableViewHolder.OnItemSelectedListener listener, boolean isMultiSelectionEnabled, File filePath, String fileName) {
        this.listener = listener;
        this.isMultiSelectionEnabled = isMultiSelectionEnabled;
        this.filePath = filePath;
        this.fileName = fileName;
        muPDFCore = new MuPDFCore(new File(filePath, fileName).getAbsolutePath());
        cookie = new Cookie();

        mValues = new ArrayList<>();
        for (int i = 0; i < muPDFCore.countPages(); i++) {
            mValues.add(new SelectableItem(i, false));
        }
    }

    @Override
    public SelectableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checked_item, parent, false);

        return new SelectableViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (context == null) {
            context = viewHolder.itemView.getContext();
        }

        SelectableViewHolder holder = (SelectableViewHolder) viewHolder;
        SelectableItem selectableItem = mValues.get(position);

        TypedValue value = new TypedValue();
        holder.imgCheck.getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorSingle, value, true);

        new PageRenderer(holder.imageView, position).execute(position);

        holder.mItem = selectableItem;
        holder.setChecked(holder.mItem.isSelected());

        if (selectableItem.isSelected()) {
            holder.imgCheck.setBackgroundResource(R.drawable.ic_view_background);
            holder.imgCheck.setImageResource(R.drawable.ic_view);
            lastSelected = selectableItem;
        } else {
            holder.imgCheck.setBackgroundDrawable(null);
            holder.imgCheck.setImageDrawable(null);
        }

        Log.e("Selectables", selectableItem.isSelected() + "");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<Item> getSelectedItems() {

        List<Item> selectedItems = new ArrayList<>();
        for (SelectableItem item : mValues) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (isMultiSelectionEnabled) {
            return SelectableViewHolder.MULTI_SELECTION;
        } else {
            return SelectableViewHolder.SINGLE_SELECTION;
        }
    }

    @Override
    public void onItemSelected(SelectableItem item) {
        if (!isMultiSelectionEnabled) {

            for (SelectableItem selectableItem : mValues) {
                if (!selectableItem.equals(item)
                        && selectableItem.isSelected()) {
                    selectableItem.setSelected(false);
                } else if (selectableItem.equals(item)
                        && item.isSelected()) {
                    selectableItem.setSelected(true);
                }
            }
            try {
                notifyItemChanged(lastSelected.getIndex());
            } catch (Exception e) {

            }
            notifyItemChanged(item.getIndex());
        }
        listener.onItemSelected(item);
    }

    private class PageRenderer extends AsyncTask<Integer, Integer, Bitmap> {

        ImageView imageView;
        //        LinearLayout lnLoaad;
        int position;

        public PageRenderer(ImageView imageView, int position) {
            this.imageView = imageView;
//            this.lnLoaad = lnLoaad;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.INVISIBLE);
        }

        protected Bitmap doInBackground(Integer... positions) {
            Bitmap bitmap = null;
            try {
                PointF size = muPDFCore.getPageSize(position);

                bitmap = Bitmap.createBitmap(76, 108, Bitmap.Config.ARGB_8888);
                muPDFCore.drawPage(bitmap, position, 76, 108, 0, 0, 76, 108, cookie);
            } catch (Exception e) {

            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
//            lnLoaad.setVisibility(View.GONE);
//            imageView.setImageBitmap(result);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Glide.with(context).load(byteArray).placeholder(null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}