package co.kr.myseafood.erp.view;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import co.kr.myseafood.erp.R;
import co.kr.myseafood.erp.data.InterfaceDO;
import co.kr.myseafood.erp.define.NET;
import co.kr.myseafood.erp.define.TAGs;
import co.kr.myseafood.erp.net.NetAdapter;
import co.kr.myseafood.erp.util.DataStorage;
import co.kr.myseafood.erp.util.LogUtil;
import co.kr.myseafood.erp.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubSalePurchaseFragment extends SubFragment implements View.OnClickListener{
    public static final int NONE = -1;
    public static final int PDATE = 1001;
    public static final int SDATE = 1002;
    public static final int PUCHASE = 1003;
    public static final int SALES = 1004;
    public static final int ITEM = 1005;
    public static final int STORE = 1006;
    public static final int ADDITEM = 1007;
    private int current_act = NONE;

    private TextView btn_purchase_date;
    private TextView btn_sale_date;

    private RelativeLayout btn_purchase;
    private RelativeLayout btn_sales;
    private RelativeLayout btn_item;
    private RelativeLayout btn_store;

    private TextView txt_purchase;
    private TextView txt_sales;
    private TextView txt_item;
    private TextView txt_store;

    private RelativeLayout btn_add_item;

    private int pYear;
    private int pMonth;
    private int pDay;
    private int sYear;
    private int sMonth;
    private int sDay;

    private ArrayList<InterfaceDO> dataList;
    private DataStorage dataStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_sale_purchase,container,false);

        btn_purchase_date = (TextView)rootView.findViewById(R.id.btn_purchase_date);
        btn_sale_date = (TextView)rootView.findViewById(R.id.btn_sale_date);

        btn_purchase = (RelativeLayout)rootView.findViewById(R.id.btn_purchase);
        btn_sales = (RelativeLayout)rootView.findViewById(R.id.btn_sales);
        btn_item = (RelativeLayout)rootView.findViewById(R.id.btn_item);
        btn_store = (RelativeLayout)rootView.findViewById(R.id.btn_store);

        txt_purchase = (TextView)rootView.findViewById(R.id.txt_purchase);
        txt_sales = (TextView)rootView.findViewById(R.id.txt_sales);
        txt_item = (TextView)rootView.findViewById(R.id.txt_item);
        txt_store = (TextView)rootView.findViewById(R.id.txt_store);

        btn_add_item = (RelativeLayout)rootView.findViewById(R.id.btn_add_item);

        btn_purchase.setOnClickListener(this);
        btn_sales.setOnClickListener(this);
        btn_item.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        btn_purchase_date.setOnClickListener(this);
        btn_sale_date.setOnClickListener(this);
        btn_sale_date.setOnClickListener(this);
        btn_add_item.setOnClickListener(this);

        GregorianCalendar calendar = new GregorianCalendar();
        pYear = sYear = calendar.get(Calendar.YEAR);
        pMonth = sMonth = calendar.get(Calendar.MONTH);
        pDay = sDay= calendar.get(Calendar.DAY_OF_MONTH);

        btn_purchase_date.setText(String.format("%04d-%02d-%02d", pYear, pMonth+1, pDay));
        btn_sale_date.setText(String.format("%04d-%02d-%02d", sYear, sMonth+1, sDay));

        dataList = new ArrayList<InterfaceDO>();
        dataStorage = DataStorage.getInstance();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btn_purchase :
                current_act = PUCHASE;
                dataList.clear();
                new NetAdapter().getPurchaseArray(getActivity(), dataStorage.getData(DataStorage.WORK_CURRENT_ID), popupHandler);
                break;
            case R.id.btn_sales :
                current_act = SALES;
                dataList.clear();
                new NetAdapter().getSalesArray(getActivity(), dataStorage.getData(DataStorage.WORK_CURRENT_ID), popupHandler);
                break;
            case R.id.btn_item :
                current_act = ITEM;
                dataList.clear();
                new NetAdapter().getItemArray(getActivity(), popupHandler);
                break;
            case R.id.btn_store :
                current_act = STORE;
                dataList.clear();
                new NetAdapter().getStoreArray(getActivity(), popupHandler);
                break;
            case R.id.btn_purchase_date :
                new DatePickerDialog(getActivity(), pDateSetListener, pYear, pMonth, pDay).show();
                break;
            case R.id.btn_sale_date :
                new DatePickerDialog(getActivity(), sDateSetListener, sYear, sMonth, sDay).show();
                break;
            case R.id.btn_add_item :
                addItem();
                break;
        }
    }


    final Handler popupHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            int errMsgId = -1;

            if(msg.what == NET.ERROR_CON){
                LogUtil.d(TAGs.SALE_LIST, "connection error");
                errMsgId = R.string.err_conn;
            }else if(msg.what==NET.SUCCESS){
                LogUtil.d(TAGs.SALE_LIST, "connection success");

                try {
                    JSONArray jsonArr = (JSONArray)msg.obj;

                    for(int i=0; i<jsonArr.length(); i++){
                        JSONObject json = jsonArr.getJSONObject(i);

                        dataList.add(new InterfaceDO(
                                json.getString("id")
                                , json.getString("name")
                        ));

                    }

                    String[] arr = new String[dataList.size()];
                    for(int i=0; i<dataList.size(); i++){
                        arr[i] = dataList.get(i).getName();
                    }
                    showList(arr);


                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAGs.SALE_LIST, "json data parsing error");
                    errMsgId = R.string.err_json;
                }

            }else{
                LogUtil.d(TAGs.SALE_LIST, "unknown error");
                errMsgId = R.string.err_unknown;
            }

            if(errMsgId>0){
                Util.showAlertDialog(getContext(), R.string.simple_alert_title, errMsgId);
            }
        }
    };

    private void showList(String[] array){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

        // List Adapter 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);

        adapter.addAll(array);
        // 버튼 생성
        alertBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (current_act){
                            case PUCHASE :
                                txt_purchase.setText("");
                                break;
                            case SALES :
                                txt_sales.setText("");
                                break;
                            case ITEM :
                                txt_item.setText("");
                                break;
                            case STORE :
                                txt_store.setText("");
                                break;
                        }
                        dataList.clear();
                        current_act = NONE;
                        dialog.dismiss();
                    }
                });

        // Adapter 셋팅
        alertBuilder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Message msg = null;
                        switch (current_act){
                            case PUCHASE :
                                LogUtil.d(TAGs.SALE_PURCHASE, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                msg = handler.obtainMessage(PUCHASE, dataList.get(id).getId());
                                txt_purchase.setText(dataList.get(id).getName());
                                break;
                            case SALES :
                                LogUtil.d(TAGs.SALE_PURCHASE, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                msg = handler.obtainMessage(SALES, dataList.get(id).getId());
                                txt_sales.setText(dataList.get(id).getName());
                                break;
                            case ITEM :
                                LogUtil.d(TAGs.SALE_PURCHASE, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                msg = handler.obtainMessage(ITEM, dataList.get(id).getId());
                                txt_item.setText(dataList.get(id).getName());
                                break;
                            case STORE :
                                LogUtil.d(TAGs.SALE_PURCHASE, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                msg = handler.obtainMessage(STORE, dataList.get(id).getId());
                                txt_store.setText(dataList.get(id).getName());
                                break;
                        }
                        dataList.clear();
                        current_act = NONE;
//                        handler.sendMessage(msg);

                    }
                });
        alertBuilder.show();
    }

    private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", pYear, pMonth+1, pDay);
            btn_purchase_date.setText(date);
            Message msg = handler.obtainMessage(PDATE, date);
//            handler.sendMessage(msg);
        }
    };
    private DatePickerDialog.OnDateSetListener sDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            sYear = year;
            sMonth = monthOfYear;
            sDay = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", sYear, sMonth+1, sDay);
            btn_sale_date.setText(date);
            Message msg = handler.obtainMessage(SDATE, date);
//            handler.sendMessage(msg);
        }
    };

    private void addItem() {
        String sales_no = "";
        String remark = "";
        Message msg = null;

        if(txt_item.length()>0){
            System.out.println("dsfsdfsdfsd");
        }


        if(txt_store.length()>0){
            System.out.println("dsfsdfsdfsdasdasdasdas2");
        }


        msg = handler.obtainMessage(ADDITEM);
//        handler.sendMessage(msg);
    }
}
