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
import android.widget.Toast;

import java.net.URISyntaxException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebviewFragment extends Fragment {

    // ログ出力用のタグ
    private static final String TAG = WebviewFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // WebView
    private WebView mWebView;

    // エラーページ
    private View mErrorPage;

    // ページ取得失敗判定
    private boolean mIsFailure = false;

    // ローディングダイアログ
    private Dialog waitDialog;
    private Integer sleep_time;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebviewFragment newInstance(String param1, String param2) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WebviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");

        // ここで値を受け取ってる
        String url = getArguments().getString("url");
        Integer site_js_flag = getArguments().getInt("site_js_flag");
        Log.d("BlankFragment onCreateView", "url:" + url);
        Log.d("BlankFragment onCreateView", "site_js_flag:" + site_js_flag);

        // WebViewの設定
        View v = inflater.inflate(R.layout.fragment_webview, container, false);
        mWebView = (WebView) v.findViewById(R.id.webView1);
        mWebView.setWebViewClient(new MyWebViewClient(getActivity()));
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
        mErrorPage = container.findViewById(R.id.webview_error_page);
        mWebView.setBackgroundColor(0);
        mWebView.loadUrl(url);

        AdView mAdView = (AdView)v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return v;
    }

    // WebViewClientを継承
    public class MyWebViewClient extends WebViewClient {

        // ローディングダイアログ
        public MyWebViewClient(Context c) {
            waitDialog = new Dialog(c, R.style.Theme_CustomProgressDialog);
            waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            waitDialog.setContentView(R.layout.custom_progress_dialog);
            waitDialog.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        // エラーが発生した場合
        @Override
        public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
            mIsFailure = true;
        }

        // ページの読み込み前に呼ばれる
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // スレッドのローディングダイアログをスタート
            sleep_time = 1300;
            try {
                waitDialog.show();
                // 実際に行いたい処理は、プログレスダイアログの裏側で行うため、別スレッドにて実行する
                (new Thread(runnable)).start();
            } catch (Exception ex) {
            } finally {
            }

        }

        // ページ読み込み完了時に呼ばれる
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

        // リンクをタップしたときに呼ばれる
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            // リンクがニコニコ動画「アプリで再生」の場合
            if (scheme.indexOf("intent") != -1 && url.indexOf("jp.nicovideo.android") != -1) {
                Intent intent = null;
                // インテントを作る
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                // アクティビティを起動
                try {
                    getActivity().startActivityForResult(intent, 12345);
                } catch (android.content.ActivityNotFoundException e) {
                    // 指定パッケージのアプリがインストールされていないか
                    // ACTION_SENDに対応していないか
                    Toast.makeText(getActivity(), "not installed", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    // スレッドのローディングダイアログを終了
    private Runnable runnable = new Runnable(){
        public void run() {
            // ここではダミーでスリープを行う
            // 実際にはここに処理を書く
            try {
                Thread.sleep(sleep_time);
            } catch (InterruptedException e) {
                Log.e("Runnable", "InterruptedException");
            }
            // 処理が完了したら、ローディングダイアログを消すためにdismiss()を実行する
            waitDialog.dismiss();
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
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
