package com.example.yako.navigation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Web1Fragment extends Fragment {

    private static String TAG = "Web1Fragment";
    private static String top_url = "http://9post.jp/";
//    private static String top_url = "https://www.google.co.jp/";

    private WebView mWebView;

    /** エラーページ */
    private View mErrorPage;

    /** ページ取得失敗判定 */
    private boolean mIsFailure = false;

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
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        // ユーザーエージェントの設定
        String ua = mWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.loadUrl(top_url);

        // エラーページのレイアウト
        mErrorPage = v.findViewById(R.id.webview_error_page);

        return v;
    }

    // ボタン
    @Override
    public void onStart() {
        super.onStart();

        // Facebook
        Button button = (Button)getActivity().findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mWebView.getUrl();
                share("com.facebook.katana", url);
            }
        });

        // Twitter
        Button button2 = (Button)getActivity().findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mWebView.getUrl();
                String title = mWebView.getTitle();
                share("com.twitter.android", title + " " + url);
            }
        });
    }

    // シェア用
    private void share(String packageName, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setPackage(packageName);   // パッケージをそのまま指定
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // 指定パッケージのアプリがインストールされていないか
            // ACTION_SENDに対応していないか
            Toast.makeText(getActivity(), "not installed", Toast.LENGTH_SHORT).show();
        }
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
        mWebView.onResume();
    }

    /***
     * Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mWebView.onPause();
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mWebView.onPause();
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

    /***
     * ローディングをカスタマイズ
     */
    public class CustomProgressDialog extends Dialog
    {
        /**
         * コンストラクタ
         * @param context
         */
        public CustomProgressDialog(Context context)
        {
            super(context, R.style.Theme_CustomProgressDialog);

            // レイアウトを決定
            setContentView(R.layout.custom_progress_dialog);
        }
    }

    /***
     * WebViewClientをカスタマイズ
     */
    private class CustomWebViewClient extends WebViewClient{
        private Dialog waitDialog;

        public CustomWebViewClient() {
            super();
        }

        // エラーだったらフラグをtrueにします。
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mIsFailure = true;
        }

        // ページ読み込み開始
        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon){

            // topページのみローディング
            Pattern p = Pattern.compile(top_url);
            Matcher m1 = p.matcher(url);

            //Loading....
            if (waitDialog == null && m1.find()) {
                waitDialog = new CustomProgressDialog(view.getContext());
                // ダイアログの表示位置　上部に表示
                WindowManager.LayoutParams wmlp=waitDialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP;
                wmlp.y = 380;
                waitDialog.getWindow().setAttributes(wmlp);
                // 画面を暗くしないように
                waitDialog.getWindow().setFlags( 0 , WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                waitDialog.show();
            }

        }

        // ページ読み込み完了
        @Override
        public void onPageFinished (WebView view, String url){

            // 読み込みエラー処理
            if (mIsFailure) {
                // エラー表示
                mErrorPage.setVisibility(View.VISIBLE);
            } else {
                // エラー非表示
                mErrorPage.setVisibility(View.GONE);
            }

            waitDialog.dismiss();
            waitDialog = null;
        }
    }



}
