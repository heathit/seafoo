package co.kr.myseafood.erp.view;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import co.kr.myseafood.erp.R;
import co.kr.myseafood.erp.define.NET;
import co.kr.myseafood.erp.define.PREF;
import co.kr.myseafood.erp.define.TAGs;
import co.kr.myseafood.erp.net.NetAdapter;
import co.kr.myseafood.erp.util.LogUtil;
import co.kr.myseafood.erp.util.PrefUtil;
import co.kr.myseafood.erp.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinFragment extends Fragment implements View.OnClickListener{

    private ScrollView scroll;
    //약관
    private Button btn_terms1;
    private Button btn_terms2;
    private CheckBox chk_term1;
    private CheckBox chk_term2;

    //회원 가입 필드
    private EditText ed_name;
    private EditText ed_id;
    private EditText ed_pass;
    private EditText ed_repass;
    private EditText ed_email;
    private EditText ed_phone;
    private EditText ed_company;

    //부가 서비스
    private CheckBox sub_chk1;

    //가입 버튼
    private Button btn_join;

    private boolean read_terms1 = false;
    private boolean read_terms2 = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join, container, false);

        ((TextView)rootView.findViewById(R.id.text)).setText(Html.fromHtml("<u>" + getResources().getString(R.string.login_join_title) + "</u>"));

        scroll = (ScrollView)rootView.findViewById(R.id.scroll);

        //약관
        btn_terms1 = (Button)rootView.findViewById(R.id.btn_terms1);
        btn_terms2 = (Button)rootView.findViewById(R.id.btn_terms2);
        chk_term1 = (CheckBox)rootView.findViewById(R.id.chk_term1);
        chk_term2 = (CheckBox)rootView.findViewById(R.id.chk_term2);

        //회원 가입 필드
        ed_name = (EditText)rootView.findViewById(R.id.ed_name);
        ed_id = (EditText)rootView.findViewById(R.id.ed_id);
        ed_pass = (EditText)rootView.findViewById(R.id.ed_pass);
        ed_repass = (EditText)rootView.findViewById(R.id.ed_repass);
        ed_email = (EditText)rootView.findViewById(R.id.ed_email);
        ed_phone = (EditText)rootView.findViewById(R.id.ed_phone);
        ed_company = (EditText)rootView.findViewById(R.id.ed_company);

        //부가 서비스
        sub_chk1 = (CheckBox)rootView.findViewById(R.id.sub_chk1);

        //가입 버튼
        btn_join = (Button)rootView.findViewById(R.id.btn_join);

        btn_terms1.setOnClickListener(this);
        btn_terms2.setOnClickListener(this);
        btn_join.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btn_terms1:
                read_terms1 = true;
                showTermDialog(R.string.login_join_terms1_txt, R.raw.terms1);
                break;
            case R.id.btn_terms2:
                read_terms2 = true;
                showTermDialog(R.string.login_join_terms2_txt, R.raw.terms2);
                break;
            case R.id.btn_join:
                if(validChk()){
                    new NetAdapter().join(getActivity()
                            , ed_id.getText().toString().trim()
                            , ed_pass.getText().toString().trim()
                            , ed_repass.getText().toString().trim()
                            , ed_name.getText().toString().trim()
                            , ed_email.getText().toString().trim()
                            , ed_phone.getText().toString().trim()
                            , ed_company.getText().toString().trim()
                            , joinHandler
                            );
                }
                break;
        }
    }

    /**
     * 회원가입 핸들러
     */
    final Handler joinHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            int errMsgId = -1;

            if(msg.what == NET.ERROR_CON){
                LogUtil.d(TAGs.LOGIN, "connection error");
                errMsgId = R.string.err_conn;
            }else if(msg.what==NET.SUCCESS){
                LogUtil.d(TAGs.LOGIN, "connection success");

                try {
                    JSONObject jsonObj = (JSONObject)msg.obj;
                    LogUtil.d(TAGs.LOGIN, "[" + jsonObj.toString() + "]");

                    if(jsonObj.has("result")){

                    }else{
                        LogUtil.d(TAGs.LOGIN, "json data parsing error");
                        errMsgId = R.string.err_json;
                    }

                } catch (Exception e) {
                    LogUtil.d(TAGs.LOGIN, "json data parsing error");
                    errMsgId = R.string.err_json;
                }

            }else{
                LogUtil.d(TAGs.LOGIN, "unknown error");
                errMsgId = R.string.err_unknown;
            }

            if(errMsgId>0){
                Util.showAlertDialog(getContext(), R.string.simple_alert_title, errMsgId);
            }
        }
    };

    /**
     * 유효성 체크
     * @return
     */
    private boolean validChk() {
        if(!read_terms1){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_terms1);
            scroll.scrollTo(0, 0);
            return false;
        }
        if(!chk_term1.isChecked()){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_agr_terms1);
            scroll.scrollTo(0, 0);
            return false;
        }
        if(!read_terms2){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_terms2);
            scroll.scrollTo(0, 0);
            return false;
        }
        if(!chk_term2.isChecked()){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_agr_terms2);
            scroll.scrollTo(0, 0);
            return false;
        }

        if(ed_name.length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_name);
            ed_name.requestFocus();
            return false;
        }
        if(ed_id.length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_id);
            ed_id.requestFocus();
            return false;
        }
        if(ed_pass.length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_pass);
            ed_pass.requestFocus();
            return false;
        }
        if(ed_repass.length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_repass);
            ed_repass.requestFocus();
            return false;
        }
        if(!ed_pass.getText().toString().trim().equals(ed_repass.getText().toString().trim())){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_repass2);
            ed_pass.setText("");
            ed_repass.setText("");
            ed_pass.requestFocus();
            return false;
        }
        if(ed_email.length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_email);
            ed_email.requestFocus();
            return false;
        }
        if(ed_phone.length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_phone);
            ed_phone.requestFocus();
            return false;
        }
        if(ed_company.length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_join_chk_company);
            ed_company.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 약관 확인 창
     * @param alertTitle
     * @param fileId
     */
    public void showTermDialog(int alertTitle, int fileId){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_text,null);
        TextView textview = (TextView)layout.findViewById(R.id.text);
        textview.setText( Util.readTxtFile(getActivity(),fileId));

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setTitle(alertTitle);
        alert.setView(layout);
        alert.setCancelable(true);
        alert.show();
    }
}
