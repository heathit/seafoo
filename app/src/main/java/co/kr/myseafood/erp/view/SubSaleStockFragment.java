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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import co.kr.myseafood.erp.MainActivity;
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
public class SubSaleStockFragment extends SubFragment implements View.OnClickListener{
    public static final int NONE = -1;

    public static final int DATE = 4001;
    public static final int SALES = 4002;
    public static final int ITEM = 4003;
    public static final int SEND_ALERT = 4004;
    public static final int RECENT_SALE = 4005;
    public static final int GRID_REFRESH = 4006;

    private int current_act = NONE;

    private TextView btn_sale_date;

    private RelativeLayout btn_sales;
    private RelativeLayout btn_item;

    private TextView txt_sales;
    private TextView txt_item;

    private CheckBox chk_send_alert;
    private CheckBox chk_all_list;

    private int year;
    private int month;
    private int day;

    private ArrayList<InterfaceDO> dataList;
    private DataStorage dataStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_sale_stock,container,false);

        btn_sale_date = (TextView)rootView.findViewById(R.id.btn_sale_date);

        btn_sales = (RelativeLayout)rootView.findViewById(R.id.btn_sales);
        btn_item = (RelativeLayout)rootView.findViewById(R.id.btn_item);

        txt_sales = (TextView)rootView.findViewById(R.id.txt_sales);
        txt_item = (TextView)rootView.findViewById(R.id.txt_item);

        chk_send_alert = (CheckBox)rootView.findViewById(R.id.chk_send_alert);
        chk_all_list = (CheckBox)rootView.findViewById(R.id.chk_all_list);

        btn_sales.setOnClickListener(this);
        btn_item.setOnClickListener(this);
        btn_sale_date.setOnClickListener(this);
        chk_send_alert.setOnClickListener(this);
        chk_all_list.setOnClickListener(this);


        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);

        String date = String.format("%04d-%02d-%02d", year, month+1, day);
        btn_sale_date.setText(date);

        dataList = new ArrayList<InterfaceDO>();
        dataStorage = DataStorage.getInstance();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
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
            case R.id.btn_sale_date :
                new DatePickerDialog(getActivity(), dateSetListener, year, month, day).show();
                break;
            case R.id.chk_send_alert :
                Message msg = handler.obtainMessage(SEND_ALERT, chk_send_alert.isChecked()?"TRUE":"FALSE");
                handler.sendMessage(msg);
                break;
            case R.id.chk_all_list :
                handler.sendEmptyMessage(RECENT_SALE);
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

                        InterfaceDO data = new InterfaceDO(json.getString("id"), json.getString("name"));
                        if(json.has("aliased_workplace_id")) data.setAliased_workplace_id(json.getString("aliased_workplace_id"));
                        dataList.add(data);
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
                        Message msg = null;
                        switch (current_act){
                            case SALES :
                                txt_sales.setText("");
                                chk_send_alert.setVisibility(View.GONE);
                                msg = handler.obtainMessage(SALES, "");
                                break;
                            case ITEM :
                                txt_item.setText("");
                                msg = handler.obtainMessage(SALES, "");
                                break;
                        }
                        dataList.clear();
                        current_act = NONE;
                        if(msg!=null) handler.sendMessage(msg);
                        dialog.dismiss();
                    }
                });

        // Adapter 셋팅
        alertBuilder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Message msg = null;
                        switch (current_act){
                            case SALES :
                                boolean flag = ((MainActivity)getActivity()).isMyWorkplace(dataList.get(id).getAliased_workplace_id());
                                txt_sales.setText(dataList.get(id).getName());
                                chk_send_alert.setVisibility(flag?View.VISIBLE:View.GONE);
                                msg = handler.obtainMessage(SALES, dataList.get(id).getId());
                                break;
                            case ITEM :
                                txt_item.setText(dataList.get(id).getName());
                                msg = handler.obtainMessage(ITEM, dataList.get(id).getId());
                                break;
                        }
                        dataList.clear();
                        current_act = NONE;
                        if(msg!=null) handler.sendMessage(msg);

                    }
                });
        alertBuilder.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            SubSaleStockFragment.this.year = year;
            month = monthOfYear;
            day = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", SubSaleStockFragment.this.year, month+1, day);
            btn_sale_date.setText(date);
            Message msg = handler.obtainMessage(DATE, date);
            handler.sendMessage(msg);
        }
    };
}
