package music.lisong.com.lisongmusic.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

public class BeanParseUtil {
    public static <T> T parse(String respond, Class<T> clazz) {
        if (TextUtils.isEmpty(respond)) {
            return null;
        }
        try {
            T t = JSON.parseObject(respond, clazz);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
