package co.kr.myseafood.erp.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class WatingDialog {
    private Context mContext;
    private Handler prgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(watingDialog!=null){
                //특별히 msg에 대한 처리는 하지 않음.
                watingDialog.dismiss();
            }
        }
    };
    ProgressDialog watingDialog;

    public WatingDialog(Context context)
    {
        mContext = context;
    }

    /**
     * < WaitingDialog 를 멈추기 위한 메서드. >
     *
     *
     */
    public void dismissWaitingDialog(){
        Message msg = prgressHandler.obtainMessage();
        prgressHandler.sendMessage(msg);
    }

    /**
     * < WaitingDialog에 Massege를 설정하고 다이어로그를 보여주는 메서드. >
     *
     * @param msg [Dialog 에 보여지는 내용]
     * @author
     */
    public void startWaitingDialog(final String msg){
        new Thread(){
            public void run(){
                Looper.prepare();
                watingDialog = (ProgressDialog) onCreateDialog(msg);
                watingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // 로딩 중에 뒤로가기 버튼이 눌렸을 때 현재 Activity를 finish 해준다.
                        //((Activity) mContext).finish();
                    }
                });
                watingDialog.show();
                Looper.loop();
            }
        }.start();
    }

    /**
     * < startWaitingDialog()시 Dialog를 생성하는 메서드 >
     *
     * @param msg [Dialog 에 보여지는 내용]
     * @return [Dialog를 반환함.]
     *
     */
    protected Dialog onCreateDialog(String msg) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);

        return progressDialog;
    }

    public boolean isShowing(){
        return watingDialog.isShowing();
    }
}