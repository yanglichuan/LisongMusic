package music.lisong.com.lisongmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.bean.Song;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String TABLE_LATEST_HISTORY = "songlatest";

    public static DatabaseHelper instance;


    private DatabaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }


    private DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    /**
     * 在SQLiteOpenHelper的子类当中，必须有该构造函数
     *
     * @param context 上下文对象
     * @param name    数据库名称
     * @param factory
     * @param version 当前数据库的版本，值必须是整数并且是递增的状态
     */
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        //必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }


//
//    protected int id;
//    protected String name;
//    protected String author;
//    protected String coverImg;
//    protected String publishTime;
//    protected String belongAblum;
//    protected String mp3url;

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(Ap.application, "lisongdb");
                }
            }
        }
        return instance;
    }

    //该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub  
        System.out.println("create a database");
        //execSQL用于执行SQL语句  
        db.execSQL("create table songlatest(" +
                "name varchar," +
                "author varchar," +
                "coverImg varchar," +
                "publishTime varchar," +
                "belongAblum varchar," +
                "mp3url varchar, " +
                "inserttime varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub  
        System.out.println("upgrade a database");
    }


    public List<Song> getAllSong() {
        try {
            SQLiteDatabase writedb = getWritableDatabase();
            while (writedb.isDbLockedByOtherThreads() || writedb.isDbLockedByCurrentThread()) {
                return new ArrayList<>();
            }
            if (writedb != null && writedb.isOpen()) {
                List<Song> list = new ArrayList<>();
                Cursor c = writedb.rawQuery("SELECT * FROM " + TABLE_LATEST_HISTORY + " ORDER BY inserttime DESC", null);
                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex("name"));
                    String author = c.getString(c.getColumnIndex("author"));
                    String coverImg = c.getString(c.getColumnIndex("coverImg"));
                    String publishTime = c.getString(c.getColumnIndex("publishTime"));
                    String belongAblum = c.getString(c.getColumnIndex("belongAblum"));
                    String mp3url = c.getString(c.getColumnIndex("mp3url"));
//                    String  inserttime = c.getString(c.getColumnIndex("inserttime"));
                    Song st = new Song(name, 0);
                    st.setAuthor(author);
                    st.setCoverImg(coverImg);
                    st.setPublishTime(publishTime);
                    st.setBelongAblum(belongAblum);
                    st.setMp3url(mp3url);
                    list.add(st);
                }
                c.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("insert  error");
        }
        return new ArrayList<>();
    }

    public void saveItem(Song song) {
        try {
            SQLiteDatabase writedb = getWritableDatabase();
            while (writedb.isDbLockedByOtherThreads() || writedb.isDbLockedByCurrentThread()) {
                return;
            }
            if (writedb != null && writedb.isOpen()) {
                writedb.delete(TABLE_LATEST_HISTORY, "name=?", new String[]{song.getName()});
            }

            writedb.execSQL("INSERT INTO " + TABLE_LATEST_HISTORY + " VALUES (?,?,?,?,?,?,?)",
                    new Object[]{song.getName(),
                            song.getAuthor(),
                            song.getCoverImg(),
                            song.getPublishTime(),
                            song.getBelongAblum(),
                            song.getMp3url(),
                            System.currentTimeMillis() + ""
                    });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("insert  error");
        }
    }

    public boolean deleteItem(String mp3url, String name) {
        try {
            SQLiteDatabase writedb = getWritableDatabase();
            while (writedb.isDbLockedByOtherThreads() || writedb.isDbLockedByCurrentThread()) {
                return false;
            }
            if (writedb != null && writedb.isOpen()) {
                if (!TextUtils.isEmpty(mp3url)) {
                    int i = writedb.delete(TABLE_LATEST_HISTORY, "mp3url=?", new String[]{mp3url});
                    return i >= 1;
                } else {
                    int i = writedb.delete(TABLE_LATEST_HISTORY, "name=?", new String[]{name});
                    return i >= 1;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("insert  error");
        }
        return false;
    }
}