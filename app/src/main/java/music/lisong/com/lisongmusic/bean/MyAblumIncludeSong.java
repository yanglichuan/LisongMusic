package music.lisong.com.lisongmusic.bean;


public class MyAblumIncludeSong extends Song {

    public MyAblumIncludeSong(Song s) {
        super();
        id = s.id;
        name = s.name;
        author = s.author;
        coverImg = s.coverImg;
        publishTime = s.publishTime;
        belongAblum = s.belongAblum;
        mp3url = s.mp3url;
    }

    public Song toSong() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof MyAblumIncludeSong)) {
            return false;
        }
        MyAblumIncludeSong other = (MyAblumIncludeSong) o;
        if (other.mp3url.equals(this.mp3url)) {
            return true;
        } else { // 下载使用
            return false;
        }
    }


}
