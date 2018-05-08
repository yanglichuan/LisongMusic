package music.lisong.com.lisongmusic.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Song extends BmobObject implements Serializable {

    protected int id;
    protected String name;
    protected String author;
    protected String coverImg;
    protected String publishTime;
    protected String belongAblum;
    protected String mp3url;


    public Song() {
    }

    public Song(String name, int count) {
        this.name = name;
    }

    public String getBelongAblum() {
        return belongAblum;
    }

    public void setBelongAblum(String belongAblum) {
        this.belongAblum = belongAblum;
    }

    public String getMp3url() {
        return mp3url;
    }

    public void setMp3url(String mp3url) {
        this.mp3url = mp3url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Song)) {
            return false;
        }
        Song other = (Song) o;
        if (other.getObjectId() == this.getObjectId()) {
            return true;
        } else { // 下载使用
            if (other.getMp3url().equals(this.getMp3url())) {
                return true;
            }
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Ablum{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", publishTime='" + publishTime + '\'' +
                '}';
    }


}
