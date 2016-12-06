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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
public class SubSaleListFragment extends SubFragment implements View.OnClickListener{
    public static final int NONE = -1;

    public static final int SDATE = 2001;
    public static final int EDATE = 2002;
    public static final int SALES = 2003;
    public static final int ITEM = 2004;
    public static final int STORE = 2005;
    public static final int SALES_NO = 2006;
    public static final int REMARK = 2007;
    public static final int SEARCH = 2008;
    private int current_act = NONE;

    private RelativeLayout btn_sales;
    private RelativeLayout btn_item;
    private RelativeLayout btn_store;

    private TextView txt_sales;
    private TextView txt_item;
    private TextView txt_store;

    private EditText ed_sales_no;
    private EditText ed_remark;

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
        rootView = inflater.inflate(R.layout.fragment_sub_sale_list,container,false);

        btn_sales = (RelativeLayout)rootView.findViewById(R.id.btn_sales);
        btn_item = (RelativeLayout)rootView.findViewById(R.id.btn_item);
        btn_store = (RelativeLayout)rootView.findViewById(R.id.btn_store);

        txt_sales = (TextView)rootView.findViewById(R.id.txt_sales);
        txt_item = (TextView)rootView.findViewById(R.id.txt_item);
        txt_store = (TextView)rootView.findViewById(R.id.txt_store);

        ed_sales_no = (EditText)rootView.findViewById(R.id.ed_sales_no);
        ed_remark = (EditText)rootView.findViewById(R.id.ed_remark);

        btn_start_date = (TextView)rootView.findViewById(R.id.btn_start_date);
        btn_end_date = (TextView)rootView.findViewById(R.id.btn_end_date);

        btn_search = (RelativeLayout)rootView.findViewById(R.id.btn_search);

        btn_sales.setOnClickListener(this);
        btn_item.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        btn_start_date.setOnClickListener(this);
        btn_end_date.setOnClickListener(this);
        btn_search.setOnClickListener(this);

        GregorianCalendar calendar = new GregorianCalendar();
        sYear = eYear = calendar.get(Calendar.YEAR);
        sMonth = eMonth = calendar.get(Calendar.MONTH);
        eDay = calendar.get(Calendar.DAY_OF_MONTH);
        sDay = 1;

        btn_start_date.setText(String.format("%04d-%02d-%02d", sYear, sMonth+1, sDay));
        btn_end_date.setText(String.format("%04d-%02d-%02d", eYear, eMonth + 1, eDay));


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
            case R.id.btn_start_date :
                new DatePickerDialog(getActivity(), sDateSetListener, sYear, sMonth, sDay).show();
                break;
            case R.id.btn_end_date :
                new DatePickerDialog(getActivity(), eDateSetListener, eYear, eMonth, eDay).show();
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
                                salesId = null;
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

                        Message msg = null;
                        switch (current_act){
                            case SALES :
                                LogUtil.d(TAGs.SALE_LIST, "'" + dataList.get(id).getId() + "', '" + dataList.get(id).getName() + "'");
                                txt_sales.setText(dataList.get(id).getName());
                                salesId = dataList.get(id).getId();
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

    private DatePickerDialog.OnDateSetListener sDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            sYear = year;
            sMonth = monthOfYear;
            sDay = dayOfMonth;

            String date = String.format("%04d-%02d-%02d", sYear, sMonth+1, sDay);
            btn_start_date.setText(date);
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
        }
    };

    String sDate = null;
    String eDate = null;
    String salesNo = null;
    String remark = null;
    String salesId = null;
    String itemId = null;
    String storeId = null;
    private void submit() {

        if(btn_start_date.length()>0){
            sDate = btn_start_date.getText().toString();
        }

        if(btn_end_date.length()>0){
            eDate = btn_end_date.getText().toString();
        }

        if(ed_sales_no.length()>0){
            salesNo = ed_sales_no.getText().toString();
        }

        if(ed_remark.length()>0){
            remark = ed_remark.getText().toString();
        }

        Message msg = handler.obtainMessage(SEARCH
                , new String[]{sDate, eDate, salesNo, remark, salesId, itemId, storeId});
        handler.sendMessage(msg);
    }
}
