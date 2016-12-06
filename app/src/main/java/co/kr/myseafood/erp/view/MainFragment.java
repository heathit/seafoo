package co.kr.myseafood.erp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.kr.myseafood.erp.R;
import co.kr.myseafood.erp.util.DataStorage;

/**
 * Created by minu on 2016-05-14.
 */
public class MainFragment extends Fragment {
    private static MainFragment instance;
    private TextView txtAlertCnt;

    public MainFragment() {
    }

    public static MainFragment getInstance(){
        if(instance == null){
            instance = new MainFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        txtAlertCnt = (TextView)v.findViewById(R.id.txt_alert_cnt);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAlertCnt(DataStorage.getInstance().getData(DataStorage.CURRENT_ALERT_CNT));
    }

    public void setAlertCnt(String alertCnt){
        if(alertCnt!=null&&alertCnt.length()>0){
            txtAlertCnt.setText("(" + alertCnt + ")");
        }
    }


}
