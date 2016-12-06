package co.kr.myseafood.erp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.kr.myseafood.erp.R;

/**
 * Created by minu on 2016-05-15.
 */
public class Util {

    /**
     *
     * @param context
     * @param fileId
     * @return
     */
    public static String readTxtFile(Context context, int fileId) {
        String data = null;
        InputStream inputStream = context.getResources().openRawResource(fileId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }

            data = new String(byteArrayOutputStream.toByteArray(),"UTF-8");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String byte2HexStr(byte[] b){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<b.length; i++){
            String hs = Integer.toHexString(0xff & b[i]);
            sb.append((hs.length()==1)?"0"+hs:hs);
        }
        return sb.toString();
    }

    public static byte[] hexStr2Byte(String str){
        byte[] ba = new byte[str.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    public static String byte2Str(byte[] b){
		return new java.math.BigInteger(1, b).toString(16);
    }

    public static byte[] hex2Byte(String str){
		return new java.math.BigInteger(str, 16).toByteArray();
    }

    /**
     * 심플 알림 창
     * @param context
     * @param titleId
     * @param msgId
     */
    public static void showAlertDialog(Context context, int titleId, int msgId){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        })
        .setTitle(titleId)
        .setMessage(msgId)
        .setCancelable(true)
        .show();
    }


    /**
     * 심플 알림 창
     * @param context
     * @param titleId
     * @param msgStr
     */
    public static void showAlertDialog(Context context, int titleId, String msgStr){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        })
        .setTitle(titleId)
        .setMessage(msgStr)
        .setCancelable(true)
        .show();
    }

    /**
     * 알림창 with 액션
     * @param context
     * @param titleId
     * @param msgId
     * @param pBtnName
     * @param pBtnListener
     * @param nBtnName
     * @param nBtnListener
     */
    public static void showAlertWithAction(Context context, int titleId, int msgId
            , String pBtnName, DialogInterface.OnClickListener pBtnListener
            , String nBtnName, DialogInterface.OnClickListener nBtnListener){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        if(pBtnName!=null && pBtnName.length()>0){
            alert.setPositiveButton(pBtnName, pBtnListener);
        }
        if(nBtnName!=null && nBtnName.length()>0){
            alert.setPositiveButton(nBtnName, nBtnListener);
        }
        alert.setTitle(titleId)
        .setMessage(msgId)
        .setCancelable(true)
        .show();
    }

    /**
     *
     * @param format
     * @return
     */
    public static String getDate(String format){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat( format, Locale.KOREA );
        Date currentTime = new Date( );
        String mTime = mSimpleDateFormat.format ( currentTime );
        return mTime;
    }
}
