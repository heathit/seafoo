package co.kr.myseafood.erp.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.kr.myseafood.erp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment {
    private static final String ARG_TXT = "txt";

    private TextView textView;
    private String htmlStr;

    public static TextFragment newInstance(String htmlStr) {
        TextFragment f = new TextFragment();
        Bundle b = new Bundle();
        b.putString(ARG_TXT, htmlStr);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        htmlStr = getArguments().getString(ARG_TXT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text,container,false);
        textView = (TextView)rootView.findViewById(R.id.text);
        textView.setText(Html.fromHtml(htmlStr));
        return rootView;
    }

}
