package music.lisong.com.lisongmusic.bean;


public class MyAblum extends Ablum {

    public MyAblum(String name, int count) {
        super(name, count);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof MyAblum)) {
            return false;
        }
        MyAblum other = (MyAblum) o;
        if (this.getName().equals(other.getName())) {
            return true;
        } else { // 下载使用
            return false;
        }
    }
}
