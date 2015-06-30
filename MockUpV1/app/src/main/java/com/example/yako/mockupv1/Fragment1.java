package com.example.yako.mockupv1;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Fragment1 extends Fragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextFragment.
     */
    // TODO: Rename and change types and number of parameters

    private WebView mWebView;

    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment1, container, false);
        mWebView = (WebView)v.findViewById(R.id.webView1);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl("http://www.google.com/");
        return v;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


}