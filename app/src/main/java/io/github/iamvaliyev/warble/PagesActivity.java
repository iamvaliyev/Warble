package io.github.iamvaliyev.warble;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.orm.util.NamingHelper;

import java.io.File;
import java.io.FileOutputStream;

import io.github.iamvaliyev.warble.adapter.PagesAdapter;
import io.github.iamvaliyev.warble.adapter.PagesLoadAdapter;
import io.github.iamvaliyev.warble.model.Book;
import io.github.iamvaliyev.warble.widget.ItemClickSupport;

public class PagesActivity extends AppCompatActivity {

    Toolbar myToolbar;
    RecyclerView recyclerView;

    PagesAdapter adapter;

    MuPDFCore muPDFCore;
    Cookie cookie;

    String fileName = "demo";

    File filePath;

    Book book;


    public static String FILE_PATH = "filePath";
    public static String TITLE = "title";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        recyclerView = findViewById(R.id.selection_list);
        myToolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        try {
            getSupportActionBar().setTitle(getIntent().getStringExtra(TITLE));
        } catch (Exception e) {
            getSupportActionBar().setTitle(R.string.pages);
        }
        ;

        filePath = new File(getCacheDir(), getIntent().getStringExtra(FILE_PATH));

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(FILE_PATH, filePath.getAbsolutePath());
                if (book.getRecentPage() != -1 && position == 0)
                    intent.putExtra("page", book.getRecentPage());
                else if (book.getRecentPage() != -1 && position != 0)
                    intent.putExtra("page", position - 1);
                else
                    intent.putExtra("page", position);
                startActivity(intent);
            }
        });

//        if (!filePath.exists()) {
//            CopyAsset copyAsset = new CopyAssetThreadImpl(getApplicationContext(), new Handler());
//            copyAsset.copy("demo.pdf", filePath.getAbsolutePath());
//        }

        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, fileName + "0");

        Log.e("FileExist", dest.exists() + "");
        if (dest.exists()) {
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            book = Select.from(Book.class).where(Condition.prop(NamingHelper.toSQLNameDefault("filePath")).eq(filePath.getAbsolutePath())).list().get(0);

            adapter = new PagesAdapter(getApplicationContext(), getCacheDir(), "demo.pdf", fileName, book.getId(), book.getRecentPage());
            recyclerView.setAdapter(adapter);
        } else {
            Book newBook = new Book();
            newBook.setFileName(fileName);
            newBook.setFilePath(filePath.getAbsolutePath());
            newBook.setRecentPage(-1);
            newBook.save();

            book = Select.from(Book.class).where(Condition.prop(NamingHelper.toSQLNameDefault("filePath")).eq(filePath.getAbsolutePath())).list().get(0);
            new PagesRenderer().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, fileName + "0");
        Log.e("FileExist", dest.exists() + "");
        if (dest.exists()) {
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            book = Select.from(Book.class).where(Condition.prop(NamingHelper.toSQLNameDefault("filePath")).eq(filePath.getAbsolutePath())).list().get(0);

            adapter = new PagesAdapter(getApplicationContext(), getCacheDir(), "demo.pdf", fileName, book.getId(), book.getRecentPage());
            recyclerView.setAdapter(adapter);
        } else {
            Book newBook = new Book();
            newBook.setFileName(fileName);
            newBook.setFilePath(filePath.getAbsolutePath());
            newBook.setRecentPage(-1);
            newBook.save();

            book = Select.from(Book.class).where(Condition.prop(NamingHelper.toSQLNameDefault("filePath")).eq(filePath.getAbsolutePath())).list().get(0);
            new PagesRenderer().execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class PagesRenderer extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            muPDFCore = new MuPDFCore(filePath.getAbsolutePath());
            cookie = new Cookie();

            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            PagesLoadAdapter loadAdapter = new PagesLoadAdapter(getApplicationContext());
            recyclerView.setAdapter(loadAdapter);
        }

        protected String doInBackground(Integer... positions) {
            for (int i = 0; i < muPDFCore.countPages(); i++) {
                Bitmap bitmap = null;
                try {
                    bitmap = Bitmap.createBitmap(152, 216, Bitmap.Config.ARGB_8888);
                    muPDFCore.drawPage(bitmap, i, 152, 216, 0, 0, 152, 216, cookie);

                    File sd = Environment.getExternalStorageDirectory();
                    File dest = new File(sd, fileName + i);
                    try {
                        FileOutputStream out = new FileOutputStream(dest);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {

                    }
                } catch (Exception e) {

                }
            }
            return "";
        }

        protected void onPostExecute(String result) {
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PagesAdapter(getApplicationContext(), getCacheDir(), "demo.pdf", fileName, book.getId(), book.getRecentPage());
            recyclerView.setAdapter(adapter);
        }
    }
}
