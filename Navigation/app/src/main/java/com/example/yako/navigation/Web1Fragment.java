package com.example.yako.navigation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Web1Fragment extends Fragment {

    private static String TAG = "Web1Fragment";

    private WebView mWebView;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Web1Fragment newInstance(int sectionNumber) {
        Web1Fragment fragment = new Web1Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public Web1Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //return rootView;

        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mWebView = (WebView)v.findViewById(R.id.webView1);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl("http://9post.jp/");
        return v;

    }

    /***
     * Activityに関連付けされた際に一度だけ呼び出される
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /***
     * Activityの「onResume」に基づき開始される
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
//        mWebView.destroy();
    }

    /***
     * Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mWebView.destroy();
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mWebView.destroy();
    }

    /***
     * Fragmentの内部のViewリソースの整理を行う
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    /***
     * Fragmentが破棄される時、最後に呼び出される
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mWebView.destroy();
    }

    /***
     * Activityの関連付けから外された時に呼び出される
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        mWebView.destroy();
    }



    public boolean canGoBack() {
        return  ( mWebView != null ) && mWebView.canGoBack();
    }

    public boolean GoBack(){
        mWebView.goBack();
        return true;
    }

}
