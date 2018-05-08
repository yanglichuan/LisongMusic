package music.lisong.com.lisongmusic.bean;

import cn.bmob.v3.BmobObject;

public class KeyWord extends BmobObject {


    public KeyWord(String kyword) {
        this.kyword = kyword;
    }


    public String kyword;

    public String getKyword() {
        return kyword;
    }

    public void setKyword(String kyword) {
        this.kyword = kyword;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyWord)) {
            return false;
        }
        KeyWord other = (KeyWord) o;
        if (other.getKyword().equals(this.getKyword())) {
            return true;
        } else { // 下载使用
            return false;
        }
    }
}
