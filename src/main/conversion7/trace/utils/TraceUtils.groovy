package conversion7.trace.utils

import com.google.gson.Gson

import java.text.SimpleDateFormat

class TraceUtils {

    static Gson GSON = new Gson()
    static final String SIMPLE_TIME_STAMP = "yyyy-MM-dd HH:mm:ss:SS";

    public static String getTimeStamp(final Date date) {
        return getTimeStamp(date, SIMPLE_TIME_STAMP);
    }

    public static String getTimeStamp(final Date date, final String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

}
