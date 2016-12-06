package co.kr.myseafood.erp.util;

import android.util.Log;

import co.kr.myseafood.erp.define.Properties;

public class LogUtil {

    public static final boolean writeLog = Properties.WRITE_LOG;


    /*
    * @param tag 로깅 태크
    * @param msg 로깅 메세지
    * */
    public static void d(String tag, String msg){
        if (writeLog && tag!=null && msg!=null)
            Log.d( tag, msg );
    }

    /*
    * @param tag 로깅 태크
    * @param msg 로깅 메세지
    * */
    public static void i(String tag, String msg){
        if (writeLog && tag!=null && msg!=null)
            Log.i( tag, msg );
    }



}
