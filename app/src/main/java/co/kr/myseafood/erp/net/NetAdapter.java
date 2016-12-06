package co.kr.myseafood.erp.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import co.kr.myseafood.erp.define.NET;
import co.kr.myseafood.erp.define.TAGs;
import co.kr.myseafood.erp.define.URL;
import co.kr.myseafood.erp.util.DataStorage;
import co.kr.myseafood.erp.util.LogUtil;
import co.kr.myseafood.erp.util.WatingDialog;

public class NetAdapter {
	private final int POST = 1001;
	private final int GET = 1002;

	private int method = POST;
	private DataStorage dataStorage;
	private Activity activity;
	private NetTask netTask;
	private String url;
	private Handler handler;
	
	private ArrayList<ReqHeader> headers = null;
    private ArrayList<ReqParam> params = null;
    private boolean showDialog = false;
    
    
	public NetAdapter() {
		netTask = new NetTask();
		dataStorage = DataStorage.getInstance();
		init();
	}

	/**
	 * init
	 */
	private void init() {
		activity = null;
		url = null;
		handler = null;
		headers = null;
		params = null;
		showDialog = true;
		method = POST;
	}
	
//*******************
// API 세팅 - start
//*******************

	/**
	 * 로그인
	 * @param activity
	 * @param id
	 * @param pass
	 * @param handler
	 */
	public void login(Activity activity, String id, String pass, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.LOGIN;

		/*
		 * header/params 처리
		 */
		params = new ArrayList<>();
		params.add(new ReqParam("uid", id));
		params.add(new ReqParam("password", pass));

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void join(Activity activity, String id, String pass, String repass, String name, String email, String mobile, String company, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.LOGIN;

		/*
		 * header/params 처리
		 */
		params = new ArrayList<>();
		params.add(new ReqParam("did", id));
		params.add(new ReqParam("password", pass));
		params.add(new ReqParam("password-cdm", repass));
		params.add(new ReqParam("name", name));
		params.add(new ReqParam("email", email));
		params.add(new ReqParam("mobile", mobile));
		params.add(new ReqParam("company", company));

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	public void getCategory(Activity activity, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.ACC_MAIN_CATEGORY;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void getStatus(Activity activity, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.STATUS;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 알림 갯수
	 * @param activity
	 * @param workplace_id
	 * @param handler
     */
	public void getAlertCnt(Activity activity, String workplace_id, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.ALERT_CNT
				+ "?workplace_id=" + workplace_id;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 품목 목록
	 * @param activity
	 * @param handler
	 */
	public void getItemArray(Activity activity, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.ITEM_ARRAY;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 창고 목록
	 * @param activity
	 * @param handler
	 */
	public void getStoreArray(Activity activity, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.STORE_ARRAY;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 매출처 목록
	 * @param activity
	 * @param workplace_id
	 * @param handler
	 */
	public void getSalesArray(Activity activity, String workplace_id, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.SALES_ARRAY
				+ "?workplace_id=" + workplace_id;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 거래처 목록
	 * @param activity
	 * @param workplace_id
	 * @param handler
	 */
	public void getCustArray(Activity activity, String workplace_id, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.CUST_ARRAY
				+ "?workplace_id=" + workplace_id;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 매입처 목록
	 * @param activity
	 * @param workplace_id
	 * @param handler
	 */
	public void getPurchaseArray(Activity activity, String workplace_id, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.PURCHASE_ARRAY
				+ "?workplace_id=" + workplace_id;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 거래은행 목록
	 * @param activity
	 * @param handler
	 */
	public void getBankArray(Activity activity, Handler handler){
		init();

		this.activity = activity;
		this.handler = handler;
		this.url = URL.SERVER + URL.BANK_ARRAY;
		method = GET;

		netTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

//*******************
// API 세팅 - end
//*******************
	
	/**
	 * JSON return type
	 * @author minu
	 *
	 */
	class NetTask extends AsyncTask<Void, Void, Message> {
		WatingDialog watingDialog = null;
		
		@Override
		protected void onPreExecute() {
			LogUtil.d(TAGs.NET, "onPreExecute()");
			super.onPreExecute();
			
			if(showDialog){
				watingDialog = new WatingDialog(activity);
	            watingDialog.startWaitingDialog("잠시기다려주세요");
			}
            
		}

		@Override
		protected Message doInBackground(Void... args) {
			LogUtil.d(TAGs.NET, "doInBackground()");
			JSONObject json = null;
			String response = null;

			HttpConnect con = new HttpConnect(url);
			con.setHeaders(headers);
			con.setParams(params);
			
			try {
				switch (method){
					case POST :
						response = con.requestPost();
						break;
					case GET :
						response = con.requestGet();
						break;
				}

//				System.out.println("response : " + response);
			} catch (Exception e) {
				e.printStackTrace();
				return handler.obtainMessage(NET.ERROR_CON, e.getMessage());
			}

			try {
				json = new JSONObject(response);
			} catch (Exception e) {
				try {
					JSONArray jsonArr = new JSONArray(response);
					return handler.obtainMessage(NET.SUCCESS, jsonArr);
				} catch (Exception e2) {
					try {
						if(response instanceof String)
						return handler.obtainMessage(NET.SUCCESS, response);
					} catch (Exception e3) {
						e.printStackTrace();
						return handler.obtainMessage(NET.ERROR_CON, e.getMessage());
					}
				}
			}

			return handler.obtainMessage(NET.SUCCESS, json);
		}
		 
		@Override
		protected void onPostExecute(Message msg) {
			LogUtil.d(TAGs.NET, "onPostExecute()");
	        if (watingDialog != null && showDialog){
	        	watingDialog.dismissWaitingDialog();
	        }
			
			if(msg == null){
				msg = handler.obtainMessage(NET.ERROR_CON, "unknown error");
			}
			handler.sendMessage(msg);
		}
	}

}
