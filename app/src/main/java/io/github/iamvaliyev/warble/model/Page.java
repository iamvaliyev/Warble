package io.github.iamvaliyev.warble.model;

import com.orm.SugarRecord;

public class Page extends SugarRecord {

    long bookId;
    int page;

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
