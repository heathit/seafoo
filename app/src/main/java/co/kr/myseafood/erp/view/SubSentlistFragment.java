package co.kr.myseafood.erp.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.kr.myseafood.erp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubSentlistFragment extends SubFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_sent_list,container,false);
        return rootView;
    }
}
