package nine.post.monst;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.Random;

import com.five_corp.ad.FiveAd;
import com.five_corp.ad.FiveAdCustomLayout;
import com.five_corp.ad.FiveAdState;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import static android.R.attr.width;

public class WebviewFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // ログ出力用のタグ
    private static final String TAG = WebviewFragment.class.getSimpleName();

    // WebView
    private WebView mWebView;

    // ページ取得失敗判定
    private boolean mIsFailure = false;

    // Five
    private FiveAdCustomLayout Custom;
    private LinearLayout linearLayout;
    private int width;

    public WebviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // ここで値を受け取ってる
        String url = getArguments().getString("url");
        Integer site_js_flag = getArguments().getInt("site_js_flag");
        Log.d("BlankFragment onCreateView", "url:" + url);
        Log.d("BlankFragment onCreateView", "site_js_flag:" + site_js_flag);

        // WebViewの設定
        View v = inflater.inflate(R.layout.fragment_webview, container, false);
        mWebView = (WebView) v.findViewById(R.id.webView1);
        WebSettings webSettings = mWebView.getSettings();
        // site_js_flagフラグが1なら、JavaScriptをオン
        if (site_js_flag == 1) {
            webSettings.setJavaScriptEnabled(true);
        }
        String ua = mWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.setBackgroundColor(0);
        mWebView.loadUrl(url);
/*
        // AD おみくじ 90/100
        Random rnd = new Random();
        int Omikuji = rnd.nextInt(100);
        if (Omikuji <= 90) {
            // Go to Ad
            linearLayout = (LinearLayout) v.findViewById(R.id.adView);
            width = linearLayout.getWidth() - linearLayout.getPaddingLeft() - linearLayout.getPaddingRight();
            Custom = new FiveAdCustomLayout(getActivity(), "913416", width);
            Custom.setListener(new Listener(getActivity(), "Custom"));
            Custom.loadAd();
            if (Custom != null && Custom.getState() == FiveAdState.LOADED) {
                linearLayout.addView(Custom);
            } else if (Custom.getState() == FiveAdState.ERROR) {
                Custom = new FiveAdCustomLayout(getActivity(), "913416", width);
                Custom.loadAd();
            }
        }
*/
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
     * Fragmentが破棄される時、最後に呼び出される
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mWebView.destroy();
    }

}
