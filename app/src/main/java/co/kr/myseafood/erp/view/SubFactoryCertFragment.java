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
public class SubFactoryCertFragment extends SubFragment implements View.OnClickListener{
    public static final int NONE = -1;

    public static final int CUST = 5001;
    public static final int ITEM = 5002;
    public static final int STORE = 5003;
    public static final int SALES_DATE = 5004;
    public static final int PAY_DATE = 5005;

    private int current_act = NONE;

    private RelativeLayout btn_cust;
    private TextView txt_cust;

    private TextView btn_sales_date;
    private TextView btn_pay_date;

    private RelativeLayout btn_filter_store;
    private RelativeLayout btn_filter_item;
    private TextView txt_filter_store;
    private TextView txt_filter_item;

    private int sYear;
    private int sMonth;
    private int sDay;
    private int pYear;
    private int pMonth;
    private int pDay;

    private ArrayList<InterfaceDO> dataList;
    private DataStorage dataStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_factory_cert,container,false);

        btn_cust = (RelativeLayout)rootView.findViewById(R.id.btn_cust);
        txt_cust = (TextView)rootView.findViewById(R.id.txt_cust);

        btn_sales_date = (TextView)rootView.findViewById(R.id.btn_sales_date);
        btn_pay_date = (TextView)rootView.findViewById(R.id.btn_pay_date);

        btn_filter_store = (RelativeLayout)rootView.findViewById(R.id.btn_filter_store);
        btn_filter_item = (RelativeLayout)rootView.findViewById(R.id.btn_filter_item);
        txt_filter_store = (TextView)rootView.findViewById(R.id.txt_filter_store);
        txt_filter_item = (TextView)rootView.findViewById(R.id.txt_filter_item);

        GregorianCalendar calendar = new GregorianCalendar();
        sYear = pYear = calendar.get(Calendar.YEAR);
        sMonth = pMonth = calendar.get(Calendar.MONTH);
        sDay = calendar.get(Calendar.DAY_OF_MONTH);
        pDay = calendar.getActualMaximum(calendar.DAY_OF_MONTH);

        dataList = new ArrayList<InterfaceDO>();
        dataStorage = DataStorage.getInstance();

        btn_sales_date.setText(String.format("%04d-%02d-%02d", sYear, sMonth+1, sDay));
        btn_pay_date.setText(String.format("%04d-%02d-%02d", pYear, pMonth+1, pDay));

        btn_cust.setOnClickListener(this);
        btn_filter_store.setOnClickListener(this);
        btn_filter_item.setOnClickListener(this);
        btn_sales_date.setOnClickListener(this);
        btn_pay_date.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btn_cust :
                current_act = CUST;
                dataList.clear();
                new NetAdapter().getCustArray(getActivity(), dataStorage.getData(DataStorage.WORK_CURRENT_ID), popupHandler);
                break;
            case R.id.btn_filter_store :
                current_act = STORE;
                dataList.clear();
                new NetAdapter().getStoreArray(getActivity(), popupHandler);
                break;
            case R.id.btn_filter_item :
                current_act = ITEM;
                dataList.clear();
                new NetAdapter().getItemArray(getActivity(), popupHandler);
                break;
            case R.id.btn_sales_date :
                new DatePickerDialog(getActivity(), salesDateSetListener, sYear, sMonth, sDay).show();
                break;
            case R.id.btn_pay_date :
                new DatePickerDialog(getActivity(), payDateSetListener, pYear, pMonth, pDay).show();
                break;
        }
    }
    final Handler popupHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            int errMsgId = -1;

            if(msg.what == NET.ERROR_CON){
                LogUtil.d(TAGs.FACTORY_CERT, "connection error");
                errMsgId = R.string.err_conn;
            }else if(msg.what==NET.SUCCESS){
                LogUtil.d(TAGs.FACTORY_CERT, "connection success");

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
                    LogUtil.d(TAGs.FACTORY_CERT, "json data parsing error");
                    errMsgId = R.string.err_json;
                }

            }else{
                LogUtil.d(TAGs.FACTORY_CERT, "unknown error");
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
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });

        // Adapter 셋팅
        alertBuilder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Message msg = null;
                        switch (current_act){
                            case CUST :
                                LogUtil.d(TAGs.FACTORY_CERT, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                msg = handler.obtainMessage(CUST, dataList.get(id).getId());
                                txt_cust.setText(dataList.get(id).getName());
                                break;
                            case ITEM :
                                LogUtil.d(TAGs.FACTORY_CERT, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                msg = handler.obtainMessage(ITEM, dataList.get(id).getId());
                                txt_filter_item.setText(dataList.get(id).getName());
                                break;
                            case STORE :
                                LogUtil.d(TAGs.FACTORY_CERT, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                msg = handler.obtainMessage(STORE, dataList.get(id).getId());
                                txt_filter_store.setText(dataList.get(id).getName());
                                break;
                        }
                        dataList.clear();
                        current_act = NONE;
//                        handler.sendMessage(msg);

                    }
                });
        alertBuilder.show();
    }

    private DatePickerDialog.OnDateSetListener salesDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            sYear = year;
            sMonth = monthOfYear;
            sDay = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", year, monthOfYear+1, dayOfMonth);
            btn_sales_date.setText(date);
//            Message msg = handler.obtainMessage(SALES_DATE, date);
//            handler.sendMessage(msg);
        }
    };
    private DatePickerDialog.OnDateSetListener payDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", year, monthOfYear+1, dayOfMonth);
            btn_pay_date.setText(date);
//            Message msg = handler.obtainMessage(PAY_DATE, date);
//            handler.sendMessage(msg);
        }
    };
}
