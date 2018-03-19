package io.github.iamvaliyev.warble;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.orm.util.NamingHelper;

import java.io.File;
import java.util.List;

import io.github.iamvaliyev.warble.adapter.SingleSelectionAdapter;
import io.github.iamvaliyev.warble.model.Book;
import io.github.iamvaliyev.warble.model.ItemModel;
import io.github.iamvaliyev.warble.model.Page;
import io.github.iamvaliyev.warble.pager.PDFAdapter;

public class MainActivity extends AppCompatActivity implements SingleSelectionAdapter.OnItemSelectedListener {

    RecyclerView recyclerView;
    ViewPager viewpager;
    Toolbar myToolbar;

    LinearLayoutManager linearLayoutManager;

    SingleSelectionAdapter adapter;
    Book book;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.selection_list);
        viewpager = findViewById(R.id.viewpager);
        myToolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        book = Select.from(Book.class).where(Condition.prop(NamingHelper.toSQLNameDefault("filePath")).eq(getIntent().getStringExtra(PagesActivity.FILE_PATH))).list().get(0);

        PDFAdapter PDFAdapter = new PDFAdapter(getApplicationContext(), new File(getCacheDir(), "demo.pdf"));

        viewpager.setAdapter(PDFAdapter);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adapter.selectPage(position);
                linearLayoutManager.scrollToPositionWithOffset(position, 0);

                book.setRecentPage(position);
                book.update();

                try {
                    List<Page> pages = Select.from(Page.class).where(Condition.prop(NamingHelper.toSQLNameDefault("bookId")).eq(book.getId()), Condition.prop(NamingHelper.toSQLNameDefault("page")).eq(position)).list();
                    if (pages.isEmpty())
                        menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark);
                    else
                        menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_red);
                } catch (Exception e) {
                    Log.e("Exc", e + "");
                    try {
                        menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark);
                    } catch (Exception e1) {

                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        MuPDFCore muPDFCore = new MuPDFCore(new File(getCacheDir(), "demo.pdf").getAbsolutePath());
        Cookie cookie = new Cookie();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SingleSelectionAdapter(this, this, muPDFCore.countPages(), "demo");
        recyclerView.setAdapter(adapter);

        viewpager.setCurrentItem(getIntent().getIntExtra("page", 0));
    }

    @Override
    public void onItemSelected(ItemModel item) {
        viewpager.setCurrentItem(item.getIndex());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bookmark) {
            try {
                List<Page> pages = Select.from(Page.class).where(Condition.prop(NamingHelper.toSQLNameDefault("bookId")).eq(book.getId()), Condition.prop(NamingHelper.toSQLNameDefault("page")).eq(viewpager.getCurrentItem())).list();
                if (pages.isEmpty()) {
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_red);
                    Page page = new Page();
                    page.setBookId(book.getId());
                    page.setPage(viewpager.getCurrentItem());
                    page.save();
                } else {
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark);
                    for (Page pg : pages) {
                        pg.delete();
                    }
                }
            } catch (Exception e) {
                Log.e("Exc2", e + "");
                menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_red);
                Page page = new Page();
                page.setBookId(book.getId());
                page.setPage(viewpager.getCurrentItem());
                page.save();
            }
        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_menu, menu);
        this.menu = menu;
        try {
            List<Page> pages = Select.from(Page.class).where(Condition.prop(NamingHelper.toSQLNameDefault("bookId")).eq(book.getId()), Condition.prop(NamingHelper.toSQLNameDefault("page")).eq(getIntent().getIntExtra("page", 0))).list();
            if (pages.isEmpty())
                menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark);
            else
                menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_red);
        } catch (Exception e) {
            Log.e("Exc3", e + "");
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
