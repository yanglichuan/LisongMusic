package music.lisong.com.lisongmusic.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;


public class Author extends BmobObject implements Serializable {

    private String name;
    private String sex;
    private String headicon;

    public Author(String name, int count) {
        this.name = name;
    }

    public String getHeadicon() {
        return headicon;
    }

    public void setHeadicon(String headicon) {
        this.headicon = headicon;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Author)) {
            return false;
        }
        Author other = (Author) o;
        if (this.name.equals(other.name)) {
            return true;
        } else { // 下载使用
            return false;
        }
    }


}
