package co.kr.myseafood.erp.view;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import co.kr.myseafood.erp.R;
import co.kr.myseafood.erp.define.NET;
import co.kr.myseafood.erp.define.PREF;
import co.kr.myseafood.erp.define.TAGs;
import co.kr.myseafood.erp.net.NetAdapter;
import co.kr.myseafood.erp.util.DataStorage;
import co.kr.myseafood.erp.util.LogUtil;
import co.kr.myseafood.erp.util.PrefUtil;
import co.kr.myseafood.erp.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{


    private EditText ed_login;
    private EditText ed_pass;
    private CheckBox chk_remember_acc;
    private CheckBox chk_auto_login;
    private TextView btn_find_acc;
    private Button btn_login;
    private DataStorage dataStorage;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        ed_login = (EditText)rootView.findViewById(R.id.ed_login);
        ed_pass = (EditText)rootView.findViewById(R.id.ed_pass);

        chk_remember_acc = (CheckBox)rootView.findViewById(R.id.chk_remember_acc);
        chk_auto_login = (CheckBox)rootView.findViewById(R.id.chk_auto_login);
        btn_find_acc = (TextView)rootView.findViewById(R.id.btn_find_acc);
        btn_login = (Button)rootView.findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);

        // 아이디 기억 및 자동 로그인 처리
        String arg1 = PrefUtil.getFromPrefs(getContext(), PREF.ARG1, null);
        if(arg1 != null && arg1.length() > 0){
            chk_remember_acc.setChecked(true);
            ed_login.setText(arg1);

            String arg2 = PrefUtil.getFromPrefs(getContext(), PREF.ARG2, null);
            if(arg2 != null && arg2.length() > 0) {

                String loginChk = PrefUtil.getFromPrefs(getContext(), PREF.AUTO_LOGIN, "N");
                ed_pass.setText(arg2);

                if(loginChk != null && loginChk.length() > 0 && "Y".equals(loginChk)){
                    chk_auto_login.setChecked(true);
                    new NetAdapter().login(getActivity(), arg1, arg2, loginHandler);
                }
            }else{
                ed_pass.requestFocus();
            }
        }

        // 아이디 기억하기 체크해제했을때 자동로그인이 체크되어 있을 경우
        // 자동로그인도 체크해제
        chk_remember_acc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    chk_auto_login.setChecked(false);
                }
            }
        });

        chk_auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chk_remember_acc.setChecked(true);
                }
            }
        });

        dataStorage = DataStorage.getInstance();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(dataCheck()){
            new NetAdapter().login(
                    getActivity()
                    , ed_login.getText().toString().trim()
                    , ed_pass.getText().toString().trim()
                    , loginHandler);
        }
    }

    /**
     * 데이터 체크
     * @return
     */
    private boolean dataCheck() {
        if(ed_login.getText().length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_chk_id);
            return false;
        }
        if(ed_pass.getText().length()==0){
            Util.showAlertDialog(getContext(), R.string.simple_alert_title, R.string.login_chk_pw);
            return false;
        }
        return true;
    }

    /**
     * 로그인 핸들러
     */
    final Handler loginHandler = new Handler(){
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
                        if("Y".equals(jsonObj.getString("result"))){
                            // 로그인 성공

                            if(chk_remember_acc.isChecked()){
                                PrefUtil.saveToPrefs(getContext(),PREF.ARG1,ed_login.getText().toString().trim());
                                if(chk_auto_login.isChecked()){
                                    PrefUtil.saveToPrefs(getContext(),PREF.ARG2,ed_pass.getText().toString().trim());
                                    PrefUtil.saveToPrefs(getContext(),PREF.AUTO_LOGIN,"Y");
                                }else{
                                    PrefUtil.removeFromPrefs(getContext(), PREF.ARG2);
                                }

                            }else{
                                PrefUtil.removeFromPrefs(getContext(), PREF.ARG1);
                                PrefUtil.removeFromPrefs(getContext(), PREF.ARG2);
                                PrefUtil.saveToPrefs(getContext(),PREF.AUTO_LOGIN,"N");

                            }

                            new NetAdapter().getStatus(getActivity(), statusHandler);
                        }else{
                            errMsgId = R.string.login_err;
                        }
                    }else{
                        LogUtil.d(TAGs.LOGIN, "json data parsing error");
                        errMsgId = R.string.err_json;

                    }

                } catch (Exception e) {
                    LogUtil.d(TAGs.LOGIN, "json data parsing error");
                    errMsgId = R.string.err_json;

                    new NetAdapter().getStatus(getActivity(), statusHandler);//
                }

            }else{
                LogUtil.d(TAGs.LOGIN, "unknown error");
                errMsgId = R.string.err_unknown;
            }

            if(errMsgId>0){
                Util.showAlertDialog(getContext(), R.string.simple_alert_title, errMsgId);
                ed_login.setText("");
                ed_pass.setText("");
            }
        }
    };
    /**
     * Status 핸들러
     */
    final Handler statusHandler = new Handler(){
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

                    if(jsonObj.has("user")){
                        JSONObject jsonUser = jsonObj.getJSONObject("user");
                        dataStorage.setData(DataStorage.USER_ID, jsonUser.getString("id").trim());
                        dataStorage.setData(DataStorage.USER_UID, jsonUser.getString("uid").trim());
                        dataStorage.setData(DataStorage.USER_NAME, jsonUser.getString("name").trim());
                        dataStorage.setData(DataStorage.USER_ADDR, jsonUser.getString("address").trim());
                        dataStorage.setData(DataStorage.USER_MOBILE, jsonUser.getString("mobile").trim());
                        dataStorage.setData(DataStorage.USER_DIV, jsonUser.getString("division").trim());
                        dataStorage.setData(DataStorage.USER_POS, jsonUser.getString("position").trim());
                        dataStorage.setData(DataStorage.USER_EMAIL, jsonUser.getString("email").trim());
                        dataStorage.setData(DataStorage.USER_STATE, jsonUser.getString("state").trim());
                        dataStorage.setData(DataStorage.USER_EID, jsonUser.getString("enterprise_id").trim());
                        dataStorage.setData(DataStorage.USER_FAX, jsonUser.getString("fax").trim());
                        dataStorage.setData(DataStorage.USER_COMPANY, jsonUser.getString("company").trim());

//                        System.out.println(dataStorage.getData(DataStorage.USER_ID));
//                        System.out.println(dataStorage.getData(DataStorage.USER_UID));
//                        System.out.println(dataStorage.getData(DataStorage.USER_NAME));
//                        System.out.println(dataStorage.getData(DataStorage.USER_ADDR));
//                        System.out.println(dataStorage.getData(DataStorage.USER_MOBILE));
//                        System.out.println(dataStorage.getData(DataStorage.USER_DIV));
//                        System.out.println(dataStorage.getData(DataStorage.USER_POS));
//                        System.out.println(dataStorage.getData(DataStorage.USER_EMAIL));
//                        System.out.println(dataStorage.getData(DataStorage.USER_STATE));
//                        System.out.println(dataStorage.getData(DataStorage.USER_EID));
//                        System.out.println(dataStorage.getData(DataStorage.USER_FAX));
//                        System.out.println(dataStorage.getData(DataStorage.USER_COMPANY));
                    }else{throw new Exception();}

                    if(jsonObj.has("workplace_id")){
                        dataStorage.setData(DataStorage.WORK_ID, jsonObj.getString("workplace_id").trim());
                        dataStorage.setData(DataStorage.WORK_CURRENT_ID, jsonObj.getString("workplace_id").trim());
                    }else{throw new Exception();}

                    if(jsonObj.has("workplaces")){
                        String worksList = jsonObj.getJSONArray("workplaces").toString();
//                        System.out.println("worksList : [" + worksList + "]");
                        dataStorage.setData(DataStorage.WORK_LIST_JSON, worksList);
                    }else{throw new Exception();}

                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
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
}
