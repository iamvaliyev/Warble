package io.github.iamvaliyev.warble.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.iamvaliyev.warble.R;
import io.github.iamvaliyev.warble.model.ItemModel;
import io.github.iamvaliyev.warble.widget.CheckedImageView;

public class SingleSelectionAdapter extends RecyclerView.Adapter {

    private List<ItemModel> itemModels;
    private int lastCheckedPosition = -1;

    Context context;

    String name;

    OnItemSelectedListener listener;

    public SingleSelectionAdapter(Context context, OnItemSelectedListener listener, int count, String name) {
        this.context = context;
        this.name = name;
        this.listener = listener;

        itemModels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                itemModels.add(new ItemModel(i, true));
                lastCheckedPosition = 0;
            } else {
                itemModels.add(new ItemModel(i, false));
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.checked_item, viewGroup, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, name + position);

        Glide.with(context).load(dest.getAbsolutePath()).placeholder(null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(itemViewHolder.imageView);

        if (itemModels.get(position).getIndex() == lastCheckedPosition) {
            itemViewHolder.imgCheck.setChecked(true);
        } else {
            itemViewHolder.imgCheck.setChecked(false);
        }

        itemViewHolder.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemSelected(itemModels.get(position));

                if (lastCheckedPosition != -1)
                    notifyItemChanged(lastCheckedPosition);
                lastCheckedPosition = itemModels.get(position).getIndex();
                notifyItemChanged(itemModels.get(position).getIndex());

            }
        });

    }

    public ItemModel getSelectedItem() {
        ItemModel model = itemModels.get(lastCheckedPosition);
        return model;
    }

    public int selectedPosition() {
        return lastCheckedPosition;
    }

    public void selectPage(int page) {
        if (lastCheckedPosition != -1)
            notifyItemChanged(lastCheckedPosition);
        lastCheckedPosition = itemModels.get(page).getIndex();
        notifyItemChanged(itemModels.get(page).getIndex());
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgCheck)
        public CheckedImageView imgCheck;

        @BindView(R.id.imgPage)
        public ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemSelectedListener {

        void onItemSelected(ItemModel item);
    }
}