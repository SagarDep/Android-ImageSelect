package me.zsj.rxwechatimageselect.model;

import java.io.File;

/**
 * Created by zsj on 2015/12/20 0020.
 */
public class Picture {

    private String firstPath;
    private int pictureCount;

    public File getPictureDir() {
        return pictureDir;
    }

    public void setPictureDir(File pictureDir) {
        this.pictureDir = pictureDir;
    }

    private File pictureDir;
    private String fileName;


    public String getFirstPath() {
        return firstPath;
    }

    public void setFirstPath(String firstPath) {
        this.firstPath = firstPath;
    }

    public int getPictureCount() {
        return pictureCount;
    }

    public void setPictureCount(int pictureCount) {
        this.pictureCount = pictureCount;
    }

   /* public String getPictureDir() {
        return pictureDir;
    }

    public void setPictureDir(String pictureDir) {
        this.pictureDir = pictureDir;
        int lastIndexOf = this.pictureDir.lastIndexOf("/");
        this.fileName = this.pictureDir.substring(lastIndexOf);
    }*/

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
