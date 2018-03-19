package io.github.iamvaliyev.warble.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import io.github.iamvaliyev.warble.R;

public class PDFAdapter extends PagerAdapter {

    private Context context;

    File file;

    MuPDFCore muPDFCore;
    Cookie cookie;

    public PDFAdapter(Context context, File file) {
        this.context = context;
        this.file = file;
        muPDFCore = new MuPDFCore(file.getAbsolutePath());
        cookie = new Cookie();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_page, null);

        PhotoView imageView = layout.findViewById(R.id.imgPage);
        LinearLayout lnLoad = layout.findViewById(R.id.lnLoad);

        new PageRenderer(imageView, lnLoad, position).execute(position);

        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return muPDFCore.countPages();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    private class PageRenderer extends AsyncTask<Integer, Integer, Bitmap> {

        PhotoView imageView;
        LinearLayout lnLoaad;
        int position;

        public PageRenderer(PhotoView imageView, LinearLayout lnLoaad, int position) {
            this.imageView = imageView;
            this.lnLoaad = lnLoaad;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(Integer... positions) {
            Bitmap bitmap = null;
            try {
                PointF size = muPDFCore.getPageSize(position);

                int pageWidth = (int) size.x;
                int pageHeight = (int) size.y;

                int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
                int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

                int width = screenWidth;
                float des = (float) screenWidth / (float) pageWidth;
                int height = (int) (pageHeight * des);

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                muPDFCore.drawPage(bitmap, position, width, height, 0, 0, width, width, cookie);

            } catch (Exception e) {

            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            lnLoaad.setVisibility(View.GONE);
            imageView.setImageBitmap(result);
        }
    }

}