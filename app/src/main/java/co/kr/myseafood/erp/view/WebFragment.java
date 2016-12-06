package co.kr.myseafood.erp.view;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import co.kr.myseafood.erp.MainActivity;
import co.kr.myseafood.erp.R;
import co.kr.myseafood.erp.data.InterfaceDO;
import co.kr.myseafood.erp.define.TAGs;
import co.kr.myseafood.erp.net.URLAdapter;
import co.kr.myseafood.erp.util.DataStorage;
import co.kr.myseafood.erp.util.LogUtil;
import co.kr.myseafood.erp.util.WatingDialog;

public class WebFragment extends Fragment {
    public static final String URL = "url";
    public static final String TAG = "tag";

    private final int TAG_NONE = -1;
    private final int TAG_PURCHASE = 1001;
    private final int TAG_SALES = 1002;
    private final int TAG_ITEM = 1003;
    private final int TAG_STORE = 1004;
    private final int TAG_CUSTOMER = 1005;
    private final int TAG_BANK = 1006;
    private int current_tag = TAG_NONE;


    private String url;
    private String tag;

    private WebView webview;
    private ImageButton btn_drop_panel;
    private WatingDialog watingDialog;

    private final Handler handler = new Handler();

    private int year;
    private int month;
    private int day;

    private DataStorage dataStorage;
    private ArrayList<InterfaceDO> dataList;
    private SubFragment subfrag;
    private boolean drop_flag=false; //가로모드시 메뉴 드롭다운 여부

    private ValueCallback<Uri> fileCallback;
    private ValueCallback<Uri[]> fileCallbackOverLollypop;

    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        CookieSyncManager.createInstance(getActivity());

        btn_drop_panel = (ImageButton)rootView.findViewById(R.id.btn_drop_panel);
        webview = (WebView)rootView.findViewById(R.id.webview);
        Bundle bundle = this.getArguments();
        url = bundle.getString(URL);
        tag = bundle.getString(TAG);


        setSubPanel(tag, rootView);

        webview.setWebChromeClient(new WebChromeClient() {
            // For Android < 3.0
            public void openFileChooser( ValueCallback<Uri> callback) {
                LogUtil.d(TAGs.WEB, "input file (android 3.0-)");
                openFileChooser(callback, "");
            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback<Uri> callback, String acceptType) {
                LogUtil.d(TAGs.WEB, "input file (android 3.0+)");
                fileCallbackOverLollypop = null;
                fileCallback = callback;

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                getActivity().startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.WEB_ACTIVITY_FLAG);
            }
            // For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> callback, String acceptType, String capture) {
                LogUtil.d(TAGs.WEB, "input file (android 4.1+)");
                openFileChooser(callback, acceptType);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, FileChooserParams fileChooserParams) {
                LogUtil.d(TAGs.WEB, "input file (android 5.0+)");

                fileCallbackOverLollypop = callback;
                fileCallback = null;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                getActivity().startActivityForResult(Intent.createChooser(intent, "File Chooser"), MainActivity.WEB_ACTIVITY_FLAG);

                return true;
            }

        });
        setUpWebViewDefaults(webview);

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);

//        webview.addJavascriptInterface(new AndroidBridge(), "AppIF");

        webview.loadUrl(url);

        dataStorage = DataStorage.getInstance();
        dataList = new ArrayList<InterfaceDO>();

        watingDialog = new WatingDialog(getActivity());

        btn_drop_panel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(subfrag!=null&&drop_flag) {
                    subfrag.onClickDropBtn(btn_drop_panel);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(subfrag!=null&&drop_flag) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { // 세로 전환시
                btn_drop_panel.setVisibility(View.GONE);
                subfrag.getView().setVisibility(View.VISIBLE);
            } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 가로 전환시
                btn_drop_panel.setVisibility(View.VISIBLE);
                subfrag.getView().setVisibility(View.GONE);
            }
        }
    }

    private void setSubPanel(String tag, View rootView){
        FragmentManager childFragMgr = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMgr.beginTransaction();
        subfrag = null;
        drop_flag = false;

        if(TAGs.SALE_PURCHASE.equals(tag)){
//            subfrag = new SubSalePurchaseFragment ();
//            subfrag.setHandler(sale_purchase_handler);
        }else if(TAGs.SALE_LIST.equals(tag)){
            subfrag = new SubSaleListFragment();
            subfrag.setHandler(sale_list_handler);
            drop_flag = true;
        }else if(TAGs.STOCK_LIST.equals(tag)){
            subfrag = new SubStockListFragment();
            subfrag.setHandler(stock_list_handler);
            drop_flag = true;
        }else if(TAGs.SALE_STOCK.equals(tag)){
//            subfrag = new SubSaleStockFragment();
//            subfrag.setHandler(sale_stock_handler);
        }else if(TAGs.FACTORY_CERT.equals(tag)){
            subfrag = new SubFactoryCertFragment();
            subfrag.setHandler(sale_purchase_handler);
        }else if(TAGs.FACTORY_CERT2.equals(tag)){
            subfrag = new SubFactoryCertFragment2();
            subfrag.setHandler(sale_cert2_handler);
            drop_flag = true;
        }else if(TAGs.CUST_LIST.equals(tag)){
        }else if(TAGs.FILE_MANAGER.equals(tag)){
        }else if(TAGs.ALERT.equals(tag)){
        }else if(TAGs.MOBILE_FAX.equals(tag)){
            subfrag = new SubFaxFragment();
            subfrag.setHandler(sale_cert2_handler);
        }else if(TAGs.MOBILE_FAX_SENTLIST.equals(tag)){
            subfrag = new SubSentlistFragment();
            subfrag.setHandler(sale_cert2_handler);
        }

        if(subfrag != null){
            childFragTrans.add(R.id.sub_fragment, subfrag);
            childFragTrans.addToBackStack(null);
            childFragTrans.commit();
        }

    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(false);
        settings.setLoadWithOverviewMode(false);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(false);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        webView.setWebViewClient(new WebViewClient() {


            // 링크 클릭에 대한 반응
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }


            // 웹페이지 호출시 오류 발생에 대한 처리
            @Override
            public void onReceivedError(WebView view, int errorcode,
                                        String description, String fallingUrl) {
            }

            // 페이지 로딩 시작시 호출
            @Override
            public void onPageStarted(WebView view,String url , Bitmap favicon){
                watingDialog.startWaitingDialog("잠시기다려주세요");
            }

            //페이지 로딩 종료시 호출
            public void onPageFinished(WebView view,String Url){
                watingDialog.dismissWaitingDialog();
//                System.out.println("=======================");
//                System.out.println("["+Url+"]");
//                System.out.println("=======================");
                if("http://myseafood.co.kr:8080/#/login".equals(Url)){
                    logoutHandler.sendEmptyMessage(100);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
    }

//    /**
//     * 웹->앱 인터페이스
//     */
//    private class AndroidBridge {
//        /**
//         * 달력 팝업
//         */
//        @JavascriptInterface
//        public void showCalendar() { // must be final
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    new DatePickerDialog(getActivity(), dateSetListener, year, month, day).show();
//                }
//            });
//        }
//
//
//        /**
//         * 매입처 목록 팝업
//         */
//        @JavascriptInterface
//        public void purchase(){
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    current_tag = TAG_PURCHASE;
//                    dataList.clear();
//                    new NetAdapter().getPurchaseArray(getActivity(), dataStorage.getData(DataStorage.WORK_CURRENT_ID), popupHandler);
//                }
//            });
//        }
//        /**
//         * 매출처 목록 팝업
//         */
//        @JavascriptInterface
//        public void sales(){
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    current_tag = TAG_SALES;
//                    dataList.clear();
//                    new NetAdapter().getSalesArray(getActivity(), dataStorage.getData(DataStorage.WORK_CURRENT_ID), popupHandler);
//                }
//            });
//        }
//        /**
//         * 거래처 목록 팝업
//         */
//        @JavascriptInterface
//        public void customer(){
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    current_tag = TAG_CUSTOMER;
//                    dataList.clear();
//                    new NetAdapter().getCustArray(getActivity(), dataStorage.getData(DataStorage.WORK_CURRENT_ID), popupHandler);
//                }
//            });
//        }
//
//        /**
//         * 품목 목록 팝업
//         */
//        @JavascriptInterface
//        public void item(){
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    current_tag = TAG_ITEM;
//                    dataList.clear();
//                    new NetAdapter().getItemArray(getActivity(), popupHandler);
//                }
//            });
//        }
//        /**
//         * 창고 목록 팝업
//         */
//        @JavascriptInterface
//        public void store(){
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    current_tag = TAG_STORE;
//                    dataList.clear();
//                    new NetAdapter().getStoreArray(getActivity(), popupHandler);
//                }
//            });
//        }
//
//        /**
//         * 거래은행 목록 팝업
//         */
//        @JavascriptInterface
//        public void bank(){
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    current_tag = TAG_BANK;
//                    dataList.clear();
//                    new NetAdapter().getBankArray(getActivity(), popupHandler);
//                }
//            });
//        }
//    }
//
//    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear,
//                              int dayOfMonth) {
//            // TODO Auto-generated method stub
//            String msg = String.format("%04d-%02d-%02d", year, monthOfYear+1, dayOfMonth);
//            webview.loadUrl("javascript:date(" + msg + ")");
//        }
//    };
//
//    final Handler popupHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg){
//
//            int errMsgId = -1;
//
//            if(msg.what == NET.ERROR_CON){
//                LogUtil.d(tag, "connection error");
//                errMsgId = R.string.err_conn;
//            }else if(msg.what==NET.SUCCESS){
//                LogUtil.d(tag, "connection success");
//
//                try {
//                    JSONArray jsonArr = (JSONArray)msg.obj;
//
//                    for(int i=0; i<jsonArr.length(); i++){
//                        JSONObject json = jsonArr.getJSONObject(i);
//
//                        dataList.add(new InterfaceDO(
//                                json.getString("id")
//                                , json.getString("name")
//                        ));
//
//                    }
//
//                    String[] arr = new String[dataList.size()];
//                    for(int i=0; i<dataList.size(); i++){
//                        arr[i] = dataList.get(i).getName();
//                    }
//                    showList(arr);
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LogUtil.d(tag, "json data parsing error");
//                    errMsgId = R.string.err_json;
//                }
//
//            }else{
//                LogUtil.d(tag, "unknown error");
//                errMsgId = R.string.err_unknown;
//            }
//
//            if(errMsgId>0){
//                Util.showAlertDialog(getContext(), R.string.simple_alert_title, errMsgId);
//            }
//
//
//        }
//    };
//
//
//    private void showList(String[] array){
//        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
//
//        // List Adapter 생성
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                getActivity(),
//                android.R.layout.select_dialog_item);
//
//        adapter.addAll(array);
//        // 버튼 생성
//        alertBuilder.setNegativeButton("취소",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//        // Adapter 셋팅
//        alertBuilder.setAdapter(adapter,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//
//                        switch (current_tag){
//                            case TAG_PURCHASE :
//                                LogUtil.d("TEST", "javascript:purchase_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                webview.loadUrl("javascript:purchase_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                break;
//                            case TAG_SALES :
//                                LogUtil.d("TEST", "javascript:sales_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                webview.loadUrl("javascript:sales_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                break;
//                            case TAG_CUSTOMER :
//                                LogUtil.d("TEST", "javascript:customer_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                webview.loadUrl("javascript:customer_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                break;
//                            case TAG_ITEM :
//                                LogUtil.d("TEST", "javascript:item_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                webview.loadUrl("javascript:item_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                break;
//                            case TAG_STORE :
//                                LogUtil.d("TEST", "javascript:store_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                webview.loadUrl("javascript:store_bind('" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
//                                break;
//                            case TAG_BANK :
//                                LogUtil.d("TEST", "javascript:bank_bind('" + dataList.get(id).getId() + "', '"  + dataList.get(id).getName() + "')");
//                                webview.loadUrl("javascript:bank_bind('" + dataList.get(id).getId() + "', '"  + dataList.get(id).getName() + "')");
//                                break;
//                            default :
//                                System.out.println("0000 current_tag : [" + current_tag + "]" );
//
//                        }
//                        dataList.clear();
//                        current_tag = TAG_NONE;
//
//                    }
//                });
//        alertBuilder.show();
//    }

    //매입후판매
    Handler sale_purchase_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SubSalePurchaseFragment.PDATE :
                    webview.loadUrl("javascript:date_from_bind('" + (String)msg.obj + "')");
                    break;
                case SubSalePurchaseFragment.SDATE :
                    webview.loadUrl("javascript:date_to_bind('" + (String)msg.obj + "')");
                    break;
                case SubSalePurchaseFragment.PUCHASE :
                    webview.loadUrl("javascript:customer_bind('" + (String)msg.obj + "')");
                    break;
                case SubSalePurchaseFragment.SALES :
                    webview.loadUrl("javascript:item_bind('" + (String)msg.obj + "')");
                    break;
                case SubSalePurchaseFragment.ITEM :
                    webview.loadUrl("javascript:item_bind('" + (String)msg.obj + "')");
                    break;
                case SubSalePurchaseFragment.STORE :
                    webview.loadUrl("javascript:store_bind('" + (String)msg.obj + "')");
                    break;
                case SubSalePurchaseFragment.ADDITEM :
                    webview.loadUrl("javascript:add_item()");
                    break;
            }
        }
    };
    //재고판매
    Handler sale_stock_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SubSaleStockFragment.DATE :
                    webview.loadUrl("javascript:sales_date_bind('" + (String)msg.obj + "')");
                    break;
                case SubSaleStockFragment.SALES :
                    webview.loadUrl("javascript:sales_value_bind('" + (String)msg.obj + "')");
                    webview.loadUrl("javascript:grid_refresh_load()");
                    break;
                case SubSaleStockFragment.ITEM :
                    webview.loadUrl("javascript:item_bind('" + (String)msg.obj + "')");
                    webview.loadUrl("javascript:grid_refresh_load()");
                    break;
                case SubSaleStockFragment.SEND_ALERT :
                    webview.loadUrl("javascript:queueing_value_bind('" + (String)msg.obj + "')");
                    break;
                case SubSaleStockFragment.RECENT_SALE :
                    webview.loadUrl("javascript:recent_sale_load()");
                    break;
            }
        }
    };
    //매출조회
    Handler sale_list_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SubSaleListFragment.SEARCH :
                    String[] dataArr = (String[])msg.obj;
                    String refleshURL = URLAdapter.getSalesList(
                            dataArr[0]
                            , dataArr[1]
                            , dataArr[2]
                            , dataArr[3]
                            , dataArr[4]
                            , dataArr[5]
                            , dataArr[6]
                            , dataStorage.getData(DataStorage.WORK_CURRENT_ID)
                    );
                    webview.loadUrl(refleshURL);
                    break;
            }
        }
    };
    //재고조회
    Handler stock_list_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SubStockListFragment.SEARCH :
                    String[] dataArr = (String[])msg.obj;
                    String refleshURL = URLAdapter.getStockList(
                            dataArr[0]
                            , dataArr[1]
                            , dataArr[2]
                            , dataArr[3]
                            , dataArr[4]
                            , dataArr[5]
                            , dataArr[6]
                            , dataArr[7]
                            , dataArr[8]
                            , dataArr[9]
                            , dataArr[10]
                            , dataStorage.getData(DataStorage.WORK_CURRENT_ID)
                    );
                    webview.loadUrl(refleshURL);
                    break;
            }
        }
    };
    //출고증 재발급
    Handler sale_cert2_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SubFactoryCertFragment2.SEARCH :
                    String[] dataArr = (String[])msg.obj;
                    String refleshURL = URLAdapter.getFactoryCertList(
                            dataArr[0]
                            , dataArr[1]
                            , dataArr[2]
                            , dataArr[3]
                            , dataStorage.getData(DataStorage.WORK_CURRENT_ID)
                    );
                    webview.loadUrl(refleshURL);
                    break;
            }
        }
    };

    public Handler logoutHandler = null;
    public void setLogoutHandler(Handler logoutHandler){
        this.logoutHandler = logoutHandler;
    }

    public void fileChooseResult(int resultCode, Intent data){
        if (fileCallback != null) {
            Uri result = (data == null || resultCode != Activity.RESULT_OK) ? null : data.getData();
            fileCallback.onReceiveValue(result);
        } else if (fileCallbackOverLollypop != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fileCallbackOverLollypop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            }
        }

        fileCallback = null;
        fileCallbackOverLollypop = null;
    }
    //
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_FILE_CHOOSE && mUploadMsg != null) {
//            Uri result = null;
//            if ( data != null || resultCode == RESULT_OK ){
//                result = data.getData();
//            }
//            mUploadMsg.onReceiveValue(result);
//            mUploadMsg = null;
//        }
//    }
}
