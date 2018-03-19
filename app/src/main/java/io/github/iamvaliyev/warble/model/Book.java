package io.github.iamvaliyev.warble.model;

import com.orm.SugarRecord;

public class Book extends SugarRecord {

    String fileName;
    String filePath;
    int recentPage;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getRecentPage() {
        return recentPage;
    }

    public void setRecentPage(int recentPage) {
        this.recentPage = recentPage;
    }
}
