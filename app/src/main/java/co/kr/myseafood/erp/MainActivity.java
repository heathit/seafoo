package co.kr.myseafood.erp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import co.kr.myseafood.erp.data.WorkplaceDO;
import co.kr.myseafood.erp.define.Define;
import co.kr.myseafood.erp.define.NET;
import co.kr.myseafood.erp.define.PREF;
import co.kr.myseafood.erp.define.Properties;
import co.kr.myseafood.erp.define.TAGs;
import co.kr.myseafood.erp.define.URL;
import co.kr.myseafood.erp.net.NetAdapter;
import co.kr.myseafood.erp.util.DataStorage;
import co.kr.myseafood.erp.util.LogUtil;
import co.kr.myseafood.erp.util.PrefUtil;
import co.kr.myseafood.erp.util.Util;
import co.kr.myseafood.erp.view.LoginActivity;
import co.kr.myseafood.erp.view.MainFragment;
import co.kr.myseafood.erp.view.WebFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final int LOGIN_ACTIVITY_FLAG = 1000;
    public static final int WEB_ACTIVITY_FLAG = 1001;

    private LayoutInflater inflater;
    private FragmentManager fragmentManager;
    private DataStorage dataStorage;

    // 사업장 이름 표시
    private MenuItem current_workplace;
    // 사업장리스트
    private ArrayList<WorkplaceDO> workplaceList;
    private View headerView;
    private WebFragment currentWebFragment;
    private MainFragment currentMainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);//inflateHeaderView(R.layout.nav_header_main);

        inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentManager = getSupportFragmentManager();

        dataStorage = DataStorage.getInstance();
        dataStorage.setData(DataStorage.CURRENT_ALERT_CNT, "0");

        //메인 화면 플래그먼트 세팅
        currentMainFragment = new MainFragment();
        replaceFragment(currentMainFragment, null);
        //로그인 액티비티 시작
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivityForResult(intent, LOGIN_ACTIVITY_FLAG);

    }

    public void replaceFragment( Fragment fragment, Bundle bundle ){
        if(bundle!=null) fragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.layout_fragment, fragment);
        transaction.commit();

    }


    long checkTime = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - checkTime < Properties.FINISH_CHK_TIME) {
                // 설정된 시간 이내에 백버튼을 다시 누른 경우 => 앱 종료
                finish();
            } else {
                Toast.makeText(this, R.string.msg_pressed_backBtn, Toast.LENGTH_SHORT).show();
                checkTime = currentTime;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        current_workplace = menu.getItem(0);
        return true;
    }

    int i = 0;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.select_workplace) {
            showWorkplaceList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            currentMainFragment = new MainFragment();
            replaceFragment(currentMainFragment, null);
            getSupportActionBar().setSubtitle("");
        } else if (id == R.id.nav_sale_after_purchase) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.SALE_PURCHASE, TAGs.SALE_PURCHASE);
            getSupportActionBar().setSubtitle(R.string.txt_sale_after_purchase);
        } else if (id == R.id.nav_sale_list) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.get_sale_list(), TAGs.SALE_LIST);
            getSupportActionBar().setSubtitle(R.string.txt_sale_list);
        } else if (id == R.id.nav_stock_list) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.STOCK_LIST, TAGs.STOCK_LIST);
            getSupportActionBar().setSubtitle(R.string.txt_stock_list);
        } else if (id == R.id.nav_sale_stock) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.SALE_STOCK, TAGs.SALE_STOCK);
            getSupportActionBar().setSubtitle(R.string.txt_sale_stock);
        } else if (id == R.id.nav_factory_cert) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.FACTORY_CERT, TAGs.FACTORY_CERT);
            getSupportActionBar().setSubtitle(R.string.txt_factory_cert);
        } else if (id == R.id.nav_cust_list) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.CUST_LIST, TAGs.CUST_LIST);
            getSupportActionBar().setSubtitle(R.string.txt_cust_list);
        } else if (id == R.id.nav_file_manager) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.FILE_MANAGER, TAGs.FILE_MANAGER);
            getSupportActionBar().setSubtitle(R.string.txt_file_manager);
        } else if (id == R.id.nav_alert) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.ALERT, TAGs.ALERT);
            getSupportActionBar().setSubtitle(R.string.txt_alert);
        } else if (id == R.id.nav_mobilefax) {
            showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.MOBILE_FAX, TAGs.MOBILE_FAX);
            getSupportActionBar().setSubtitle(R.string.txt_mobilefax);
//            getSupportActionBar().setSubtitle(R.string.txt_mobilefax);
        } else if (id == R.id.nav_pc_ver) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URL.WEBURL));
            startActivity(i);
        } else if (id == R.id.nav_logout) {
//            CookieManager cookieManager = CookieManager.getInstance();
//            cookieManager.setAcceptCookie(true);
//            cookieManager.removeSessionCookie();
//            System.out.println("logout");
            // 자동로그인 해제
            PrefUtil.saveToPrefs(this, PREF.AUTO_LOGIN, "N");
            // 메인 화면 플래그먼트 세팅
            currentMainFragment = new MainFragment();
            replaceFragment(currentMainFragment, null);
            // 로그인 액티비티 시작
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btn_sale_after_purchase :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.SALE_PURCHASE, TAGs.SALE_PURCHASE);
                getSupportActionBar().setSubtitle(R.string.txt_sale_after_purchase);
                break;
            case R.id.btn_sale_list :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.get_sale_list(), TAGs.SALE_LIST);
                getSupportActionBar().setSubtitle(R.string.txt_sale_list);
                break;
            case R.id.btn_stock_list :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.STOCK_LIST, TAGs.STOCK_LIST);
                getSupportActionBar().setSubtitle(R.string.txt_stock_list);
                break;
            case R.id.btn_sale_stock :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.SALE_STOCK, TAGs.SALE_STOCK);
                getSupportActionBar().setSubtitle(R.string.txt_sale_stock);
                break;
            case R.id.btn_factory_cert :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.FACTORY_CERT, TAGs.FACTORY_CERT);
                getSupportActionBar().setSubtitle(R.string.txt_factory_cert);
                break;
            case R.id.btn_cust_list :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.CUST_LIST, TAGs.CUST_LIST);
                getSupportActionBar().setSubtitle(R.string.txt_cust_list);
                break;
            case R.id.btn_file_manager :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.FILE_MANAGER, TAGs.FILE_MANAGER);
                getSupportActionBar().setSubtitle(R.string.txt_file_manager);
                break;
            case R.id.btn_alert :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.ALERT, TAGs.ALERT);
                getSupportActionBar().setSubtitle(R.string.txt_alert);
                break;
            case R.id.btn_trans_cert_view :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.get_factory_cert2(), TAGs.FACTORY_CERT2);
                getSupportActionBar().setSubtitle(R.string.txt_factory_cert2);
                break;
            case R.id.btn_mobile_fax :
            case R.id.btn_trans_mobile_fax :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.MOBILE_FAX, TAGs.MOBILE_FAX);
                getSupportActionBar().setSubtitle(R.string.txt_mobilefax);
                break;
            case R.id.btn_trans_sent_list :
                showWebView(dataStorage.getData(DataStorage.WORK_CURRENT_ID), URL.MOBILE_FAX_SENTLIST, TAGs.MOBILE_FAX_SENTLIST);
                getSupportActionBar().setSubtitle(R.string.txt_sentlist);
                break;
        }
    }

    private void showWebView(String current_work_id, String url, String tag){
        Bundle bundle = new Bundle();
        bundle.putString(WebFragment.TAG, tag);
        bundle.putString(WebFragment.URL, URL.SERVER + "/#/" + current_work_id + url);
        currentWebFragment = new WebFragment();
        currentWebFragment.setLogoutHandler(logoutHandler);
        replaceFragment(currentWebFragment, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == WEB_ACTIVITY_FLAG && currentWebFragment != null) {
            currentWebFragment.fileChooseResult(resultCode, data);
            return;
        }

        if(resultCode == RESULT_OK && requestCode == LOGIN_ACTIVITY_FLAG) {
            try {
                JSONArray workJsonArr = new JSONArray(dataStorage.getData(DataStorage.WORK_LIST_JSON));
                workplaceList = new ArrayList<>();

                for (int i = 0; i < workJsonArr.length(); i++) {
                    JSONObject json = workJsonArr.getJSONObject(i);
                    workplaceList.add(new WorkplaceDO(json.getString("id"), json.getString("name")));
                }

                String workId = dataStorage.getData(DataStorage.WORK_CURRENT_ID);
                current_workplace.setTitle(getWorkplaceName(workId));

                TextView txt_name = (TextView) headerView.findViewById(R.id.txt_name);
                txt_name.setText(dataStorage.getData(DataStorage.USER_NAME));

                TextView txt_addr = (TextView) headerView.findViewById(R.id.txt_addr);
                txt_addr.setText(dataStorage.getData(DataStorage.USER_ADDR));

                TextView txt_email = (TextView) headerView.findViewById(R.id.txt_email);
                txt_email.setText(dataStorage.getData(DataStorage.USER_EMAIL));

                new NetAdapter().getAlertCnt(this, workId, alertHandler);
            } catch (JSONException e) {
                Util.showAlertWithAction(this, R.string.simple_alert_title, R.string.err_login_result,
                        "확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, null, null);
            }
        }else{
            Util.showAlertWithAction(this, R.string.simple_alert_title, R.string.err_login_result,
                    "확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, null, null);
        }
    }

    /**
     * get Alert Count 핸들러
     */
    final Handler alertHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            int errMsgId = -1;

            if(msg.what == NET.ERROR_CON){
                LogUtil.d(TAGs.MAIN, "connection error");
                errMsgId = R.string.err_conn;
            }else if(msg.what==NET.SUCCESS){
                LogUtil.d(TAGs.MAIN, "connection success");

                try {
                    String alertCnt = (String)msg.obj;
                    currentMainFragment.setAlertCnt(alertCnt.trim());
                    dataStorage.setData(DataStorage.CURRENT_ALERT_CNT, alertCnt.trim());
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAGs.MAIN, "data parsing error");
                    errMsgId = R.string.err_json;
                }

            }else{
                LogUtil.d(TAGs.MAIN, "unknown error");
                errMsgId = R.string.err_unknown;
            }

            if(errMsgId>0){
                Util.showAlertDialog(MainActivity.this, R.string.simple_alert_title, errMsgId);
            }
        }
    };


    private void showWorkplaceList(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("사업장 선택");

        // List Adapter 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_item);

        if(workplaceList==null) return;

        for(int i=0; i<workplaceList.size(); i++){
            adapter.add(workplaceList.get(i).getName());
        }
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
                        current_workplace.setTitle(workplaceList.get(id).getName());
                        dataStorage.setData(DataStorage.WORK_CURRENT_ID, workplaceList.get(id).getId());
                        replaceFragment(currentMainFragment, null);
                        getSupportActionBar().setSubtitle("");

                        new NetAdapter().getAlertCnt(MainActivity.this, workplaceList.get(id).getId(), alertHandler);
                    }
                });
        alertBuilder.show();
    }

    /**
     * workplace name으로 id 찾기
     * name이 중복되는 경우?
     * @param name
     * @return
     */
    private String getWorkplaceId(String name){
        return "";
    }

    /**
     * workplace id으로 name 찾기
     * @param id
     * @return
     */
    private String getWorkplaceName(String id){
        if(id==null) return "";
        String result = "";

        for(WorkplaceDO w : workplaceList){
            if(id.equals(w.getId())){
                return w.getName();
            }
        }
        return "";
    }

    public boolean isMyWorkplace(String id){
        boolean rst = false;
        for(WorkplaceDO w : workplaceList){
//            System.out.println(id + " : " + w.getId());
            if(id.equals(w.getId())){
                rst = true;
            }
        }
        return rst;
    }

    private Handler logoutHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==100){
                // 자동로그인 해제
                PrefUtil.saveToPrefs(MainActivity.this, PREF.AUTO_LOGIN, "N");
                // 메인 화면 플래그먼트 세팅
                currentMainFragment = new MainFragment();
                replaceFragment(currentMainFragment, null);
                // 로그인 액티비티 시작
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }
    };
}
