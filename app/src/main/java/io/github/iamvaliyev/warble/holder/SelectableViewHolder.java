package io.github.iamvaliyev.warble.holder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import io.github.iamvaliyev.warble.R;
import io.github.iamvaliyev.warble.model.SelectableItem;
import io.github.iamvaliyev.warble.widget.CheckedImageView;

public class SelectableViewHolder extends RecyclerView.ViewHolder {

    public static final int MULTI_SELECTION = 2;
    public static final int SINGLE_SELECTION = 1;
    public CheckedImageView imgCheck;
    public ImageView imageView;
    public SelectableItem mItem;
    OnItemSelectedListener itemSelectedListener;


    public SelectableViewHolder(View view, OnItemSelectedListener listener) {
        super(view);
        itemSelectedListener = listener;
        imgCheck = view.findViewById(R.id.imgCheck);
        imageView = view.findViewById(R.id.imgPage);

        imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItem.isSelected() && getItemViewType() == MULTI_SELECTION) {
                    setChecked(false);
                } else {
                    setChecked(true);
                }
                itemSelectedListener.onItemSelected(mItem);

            }
        });
    }

    public void setChecked(boolean value) {
        if (value) {
            imgCheck.setBackgroundResource(R.drawable.ic_view_background);
            imgCheck.setImageResource(R.drawable.ic_view);
        } else {
            imgCheck.setBackgroundDrawable(null);
            imgCheck.setImageDrawable(null);
        }
        mItem.setSelected(value);
        imgCheck.setChecked(value);
    }

    public interface OnItemSelectedListener {

        void onItemSelected(SelectableItem item);
    }

}