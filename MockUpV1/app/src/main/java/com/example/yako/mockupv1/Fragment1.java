package com.example.yako.mockupv1;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.Button;
import android.widget.Toast;
import android.view.KeyEvent;

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

    private static String TAG = "Fragment1";

    private WebView MyWebView;

    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    public Fragment1() {
        // Required empty public constructor
    }

    /***
     * Fragmentの初期化処理を行う
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /***
     * FragmentのView階層を生成し戻り値として返す
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment1, container, false);
        MyWebView = (WebView)v.findViewById(R.id.webView1);
        MyWebView.setWebViewClient(new WebViewClient());
        MyWebView.getSettings().setJavaScriptEnabled(true);
        MyWebView.getSettings().setUseWideViewPort(true);
        MyWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        MyWebView.loadUrl("http://9post.jp/");
        return v;

    }

    /***
     * Activityに関連付けされた際に一度だけ呼び出される
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /***
     * Activityの「onResume」に基づき開始される
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        MyWebView.destroy();
    }

    /***
     * Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        MyWebView.destroy();
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        MyWebView.destroy();
    }

    /***
     * Fragmentの内部のViewリソースの整理を行う
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        MyWebView.destroy();
    }

    /***
     * Fragmentが破棄される時、最後に呼び出される
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        MyWebView.destroy();
    }

    /***
     * Activityの関連付けから外された時に呼び出される
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        MyWebView.destroy();
    }

}