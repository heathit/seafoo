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
import android.widget.EditText;
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
public class SubStockListFragment extends SubFragment implements View.OnClickListener{
    public static final int NONE = -1;

    public static final int SDATE1 = 3001;
    public static final int EDATE1 = 3002;
    public static final int SDATE2 = 3003;
    public static final int EDATE2 = 3004;
    public static final int SALES = 3005;
    public static final int ITEM = 3006;
    public static final int STORE = 3007;
    public static final int SIZE = 3008;
    public static final int WEIGHT = 3009;
    public static final int ORIGIN = 3010;
    public static final int SALES_NO = 3011;
    public static final int REMARK = 3012;
    public static final int SEARCH = 3013;
    private int current_act = NONE;

    private RelativeLayout btn_sales;
    private RelativeLayout btn_item;
    private RelativeLayout btn_store;

    private TextView txt_sales;
    private TextView txt_item;
    private TextView txt_store;

    private EditText ed_size;
    private EditText ed_weight;
    private EditText ed_origin;
    private EditText ed_remark;

    private TextView btn_start_date_1;
    private TextView btn_end_date_1;

    private TextView btn_start_date_2;
    private TextView btn_end_date_2;

    private RelativeLayout btn_search;

    private int sYear1;
    private int sMonth1;
    private int sDay1;
    private int eYear1;
    private int eMonth1;
    private int eDay1;
    private int sYear2;
    private int sMonth2;
    private int sDay2;
    private int eYear2;
    private int eMonth2;
    private int eDay2;

    private ArrayList<InterfaceDO> dataList;
    private DataStorage dataStorage;
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sub_stock_list,container,false);

        btn_sales = (RelativeLayout)rootView.findViewById(R.id.btn_sales);
        btn_item = (RelativeLayout)rootView.findViewById(R.id.btn_item);
        btn_store = (RelativeLayout)rootView.findViewById(R.id.btn_store);

        txt_sales = (TextView)rootView.findViewById(R.id.txt_sales);
        txt_item = (TextView)rootView.findViewById(R.id.txt_item);
        txt_store = (TextView)rootView.findViewById(R.id.txt_store);

        ed_size = (EditText)rootView.findViewById(R.id.ed_size);
        ed_weight = (EditText)rootView.findViewById(R.id.ed_weight);
        ed_origin = (EditText)rootView.findViewById(R.id.ed_origin);
        ed_remark = (EditText)rootView.findViewById(R.id.ed_remark);

        btn_start_date_1 = (TextView)rootView.findViewById(R.id.btn_start_date_1);
        btn_end_date_1 = (TextView)rootView.findViewById(R.id.btn_end_date_1);

        btn_start_date_2 = (TextView)rootView.findViewById(R.id.btn_start_date_2);
        btn_end_date_2 = (TextView)rootView.findViewById(R.id.btn_end_date_2);

        btn_search = (RelativeLayout)rootView.findViewById(R.id.btn_search);

        btn_sales.setOnClickListener(this);
        btn_item.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        btn_start_date_1.setOnClickListener(this);
        btn_end_date_1.setOnClickListener(this);
        btn_start_date_2.setOnClickListener(this);
        btn_end_date_2.setOnClickListener(this);
        btn_search.setOnClickListener(this);

        GregorianCalendar calendar = new GregorianCalendar();
        sYear1 = eYear1 = sYear2 = eYear2 = calendar.get(Calendar.YEAR);
        sMonth1 = eMonth1 = sMonth2 = eMonth2 = calendar.get(Calendar.MONTH);
        sDay1 = eDay1 = sDay2 = eDay2 = calendar.get(Calendar.DAY_OF_MONTH);

        dataList = new ArrayList<InterfaceDO>();
        dataStorage = DataStorage.getInstance();

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
            case R.id.btn_store :
                current_act = STORE;
                dataList.clear();
                new NetAdapter().getStoreArray(getActivity(), popupHandler);
                break;
            case R.id.btn_start_date_1 :
                new DatePickerDialog(getActivity(), sDateSetListener1, sYear1, sMonth1, sDay1).show();
                break;
            case R.id.btn_end_date_1 :
                new DatePickerDialog(getActivity(), eDateSetListener1, eYear1, eMonth1, eDay1).show();
                break;
            case R.id.btn_start_date_2 :
                new DatePickerDialog(getActivity(), sDateSetListener2, sYear2, sMonth2, sDay2).show();
                break;
            case R.id.btn_end_date_2 :
                new DatePickerDialog(getActivity(), eDateSetListener2, eYear2, eMonth2, eDay2).show();
                break;
            case R.id.btn_search :
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
                            case SALES :
                                txt_sales.setText("");
                                custId = null;
                                break;
                            case ITEM :
                                txt_item.setText("");
                                itemId = null;
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

                        switch (current_act){
                            case SALES :
                                LogUtil.d(TAGs.SALE_LIST, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "'");
                                txt_sales.setText(dataList.get(id).getName());
                                custId = dataList.get(id).getId();
                                break;
                            case ITEM :
                                LogUtil.d(TAGs.SALE_LIST, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "'");
                                txt_item.setText(dataList.get(id).getName());
                                itemId = dataList.get(id).getId();
                                break;
                            case STORE :
                                LogUtil.d(TAGs.SALE_LIST, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "'");
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


    private DatePickerDialog.OnDateSetListener sDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            sYear1 = year;
            sMonth1 = monthOfYear;
            sDay1 = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", sYear1, sMonth1+1, sDay1);
            btn_start_date_1.setText(date);
        }
    };
    private DatePickerDialog.OnDateSetListener eDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            eYear1 = year;
            eMonth1 = monthOfYear;
            eDay1 = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", eYear1, eMonth1+1, eDay1);
            btn_end_date_1.setText(date);
        }
    };

    private DatePickerDialog.OnDateSetListener sDateSetListener2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            sYear2 = year;
            sMonth2 = monthOfYear;
            sDay2 = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", sYear2, sMonth2+1, sDay2);
            btn_start_date_2.setText(date);
        }
    };
    private DatePickerDialog.OnDateSetListener eDateSetListener2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            eYear2 = year;
            eMonth2 = monthOfYear;
            eDay2 = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", eYear2, eMonth2+1, eDay2);
            btn_end_date_2.setText(date);
        }
    };

    String sDate1 = null;
    String eDate1  = null;
    String sDate2 = null;
    String eDate2 = null;
    String custId = null;
    String itemId = null;
    String storeId = null;
    String size = null;
    String weight = null;
    String origin = null;
    String remark = null;
    private void submit() {

        // 규격
        if(ed_size.length()>0){
            size = ed_size.getText().toString().trim();
        }

        // 중량
        if(ed_weight.length()>0){
            weight = ed_weight.getText().toString().trim();
        }

        // 원산지
        if(ed_origin.length()>0){
            origin = ed_origin.getText().toString().trim();
        }

        // 적요
        if(ed_remark.length()>0){
            remark = ed_remark.getText().toString().trim();
        }


        // 매입일 from
        if(btn_start_date_1.length()>0){
            sDate1 = btn_start_date_1.getText().toString().trim();
        }

        // 매입일 to
        if(btn_end_date_1.length()>0){
            eDate1 = btn_end_date_1.getText().toString().trim();
        }

        // 입고일 from
        if(btn_start_date_2.length()>0){
            sDate2 = btn_start_date_2.getText().toString().trim();
        }

        // 입고일 to
        if(btn_end_date_2.length()>0){
            eDate2 = btn_end_date_2.getText().toString().trim();
        }

        Message msg = handler.obtainMessage(SEARCH,
                new String[]{sDate1, eDate1, sDate2, eDate2, custId, itemId, storeId, size, weight, origin, remark});
        handler.sendMessage(msg);
    }
}
