package music.lisong.com.lisongmusic.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Ablum extends BmobObject implements Serializable {

    private Integer id;
    private String name;
    private String author;
    private String coverImg;
    private String publishTime;
    private Integer count;


    public Ablum(String name, int count) {
        this.name = name;
        this.count = count;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Ablum)) {
            return false;
        }
        Ablum other = (Ablum) o;
        if (other.getName().equals(this.getName())) {
            return true;
        } else { // 下载使用
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
                ", count=" + count +
                '}';
    }


}
