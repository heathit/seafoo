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
import android.widget.ImageButton;
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
public class SubFactoryCertFragment2 extends SubFragment implements View.OnClickListener{
    public static final int NONE = -1;

    public static final int CUST = 5101;
    public static final int STORE = 5102;
    public static final int SDATE = 5103;
    public static final int EDATE = 5104;
    public static final int SEARCH = 5105;
    private int current_act = NONE;

    private RelativeLayout btn_cust;
    private RelativeLayout btn_store;

    private TextView txt_cust;
    private TextView txt_store;

    private TextView btn_start_date;
    private TextView btn_end_date;

    private RelativeLayout btn_search;

    private int sYear;
    private int sMonth;
    private int sDay;
    private int eYear;
    private int eMonth;
    private int eDay;

    private ArrayList<InterfaceDO> dataList;
    private DataStorage dataStorage;
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sub_factory_cert_2,container,false);

        btn_cust = (RelativeLayout)rootView.findViewById(R.id.btn_cust);
        btn_store = (RelativeLayout)rootView.findViewById(R.id.btn_store);

        txt_cust = (TextView)rootView.findViewById(R.id.txt_cust);
        txt_store = (TextView)rootView.findViewById(R.id.txt_store);

        btn_start_date = (TextView)rootView.findViewById(R.id.btn_start_date);
        btn_end_date = (TextView)rootView.findViewById(R.id.btn_end_date);

        btn_search = (RelativeLayout)rootView.findViewById(R.id.btn_search);

        btn_cust.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        btn_start_date.setOnClickListener(this);
        btn_end_date.setOnClickListener(this);
        btn_search.setOnClickListener(this);


        GregorianCalendar calendar = new GregorianCalendar();
        sYear = eYear = calendar.get(Calendar.YEAR);
        sMonth = eMonth = calendar.get(Calendar.MONTH);
        eDay = calendar.get(Calendar.DAY_OF_MONTH);
        sDay = 1;

        dataList = new ArrayList<InterfaceDO>();
        dataStorage = DataStorage.getInstance();

        btn_start_date.setText(String.format("%04d-%02d-%02d", sYear, sMonth+1, sDay));
        btn_end_date.setText(String.format("%04d-%02d-%02d", eYear, eMonth+1, eDay));

        return rootView;
    }

    @Override
    public void onClickDropBtn(ImageButton imgBtn) {
        if(rootView.getVisibility()==View.GONE){
            rootView.setVisibility(View.VISIBLE);
            imgBtn.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }else{
            rootView.setVisibility(View.GONE);
            imgBtn.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_cust:
                current_act = CUST;
                dataList.clear();
                new NetAdapter().getCustArray(getActivity(), dataStorage.getData(DataStorage.WORK_CURRENT_ID), popupHandler);
                break;
            case R.id.btn_store:
                current_act = STORE;
                dataList.clear();
                new NetAdapter().getStoreArray(getActivity(), popupHandler);
                break;
            case R.id.btn_start_date:
                new DatePickerDialog(getActivity(), sDateSetListener, sYear, sMonth, sDay).show();
                break;
            case R.id.btn_end_date:
                new DatePickerDialog(getActivity(), eDateSetListener, eYear, eMonth, eDay).show();
                break;
            case R.id.btn_search:
                submit();
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
                            case CUST :
                                txt_cust.setText("");
                                custId = null;
                                break;
                            case STORE :
                                txt_store.setText("");
                                storeId = null;
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
                            case CUST :
                                LogUtil.d(TAGs.SALE_LIST, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                txt_cust.setText(dataList.get(id).getName());
                                custId = dataList.get(id).getId();
                                break;
                            case STORE :
                                LogUtil.d(TAGs.SALE_LIST, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "')");
                                txt_store.setText(dataList.get(id).getName());
                                storeId = dataList.get(id).getId();
                                break;
                        }
                        dataList.clear();
                        current_act = NONE;

                    }
                });
        alertBuilder.show();
    }

    private DatePickerDialog.OnDateSetListener sDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            sYear = year;
            sMonth = monthOfYear;
            sDay = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", sYear, sMonth+1, sDay);
            btn_start_date.setText(date);
            Message msg = handler.obtainMessage(SDATE, date);
            handler.sendMessage(msg);
        }
    };
    private DatePickerDialog.OnDateSetListener eDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            eYear = year;
            eMonth = monthOfYear;
            eDay = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", eYear, eMonth+1, eDay);
            btn_end_date.setText(date);
            Message msg = handler.obtainMessage(EDATE, date);
            handler.sendMessage(msg);
        }
    };

    String sDate = null;
    String eDate = null;
    String custId = null;
    String storeId = null;

    private void submit() {
        // 매입일 from
        if(btn_start_date.length()>0){
            sDate = btn_start_date.getText().toString().trim();
        }

        // 매입일 to
        if(btn_end_date.length()>0){
            eDate = btn_end_date.getText().toString().trim();
        }

        Message msg = handler.obtainMessage(SEARCH,
                new String[]{sDate, eDate, custId, storeId});
        handler.sendMessage(msg);
    }
}
