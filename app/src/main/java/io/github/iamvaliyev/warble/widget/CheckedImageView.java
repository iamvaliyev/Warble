package io.github.iamvaliyev.warble.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Checkable;

import io.github.iamvaliyev.warble.R;

public class CheckedImageView extends AppCompatImageView implements Checkable {

    private boolean mChecked = false;

    private static final int[] STATE_CHECKED = new int[]{
            android.R.attr.state_checked
    };

    public CheckedImageView(Context context) {
        super(context);
    }

    public CheckedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean b) {
        if (mChecked != b) {
            mChecked = b;
            refreshDrawableState();
        }

        if (b) {
            setBackgroundResource(R.drawable.ic_view_background);
            setImageResource(R.drawable.ic_view);
        } else {
            setBackgroundDrawable(null);
            setImageDrawable(null);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        int[] additionalStates = mChecked ? STATE_CHECKED : null;
        if (additionalStates != null)
            mergeDrawableStates(drawableState, additionalStates);

        return drawableState;
    }
}