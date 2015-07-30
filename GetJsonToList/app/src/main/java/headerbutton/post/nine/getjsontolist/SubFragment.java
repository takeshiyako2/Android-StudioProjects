package headerbutton.post.nine.getjsontolist;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SubFragment extends Fragment {

    // リスナー？
    private OnFragmentInteractionListener mListener;

    // ログ出力用のタグ
    private static final String TAG = SubFragment.class.getSimpleName();

    // API キー
    private static final String API_KEY = "AIzaSyAmG880XF_VyLirMrqCYroGIvfDTQMMZHQ";

    // YouTubeのビデオID
    private static String VIDEO_ID = "EGy39OMyHzw";

    // WebView
    private WebView MyWebView;

    // アクティビティから渡される値
    String id;
    String title;
    String url;
    String youtube_id;

    // TODO: Rename and change types and number of parameters
    public static SubFragment newInstance(String param1, String param2) {
        SubFragment fragment = new SubFragment();
        return fragment;
    }

    public SubFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");

        // ここで値を受け取ってる
        id = getArguments().getString("id");
        title = getArguments().getString("title");
        url = getArguments().getString("url");
        youtube_id = getArguments().getString("youtube_id");
        Log.d("onCreateView", "id:" + id);
        Log.d("onCreateView", "url:" + url);
        Log.d("onCreateView", "youtube_id:" + youtube_id);

        // VIDEO_ID をセット
        VIDEO_ID = youtube_id;

        // レイアウトの設定
        View rootView = inflater.inflate(R.layout.fragment_sub, container, false);

        // WebView
        MyWebView = (WebView)rootView.findViewById(R.id.webView1);
        MyWebView.setWebViewClient(new WebViewClient() {
            // タップしたURLが "9postじゃなかったら" デフォルトブラウザで開く
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && is_not_9post(url)) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
        });
        MyWebView.getSettings().setUseWideViewPort(true);
        String ua = MyWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        MyWebView.getSettings().setUserAgentString(ua);
        MyWebView.setBackgroundColor(0);
        MyWebView.loadUrl(url);

        // YouTubeフラグメントインスタンスを取得
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        // レイアウトにYouTubeフラグメントを追加
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();

        // YouTubeフラグメントのプレーヤーを初期化する
        youTubePlayerFragment.initialize(API_KEY, new OnInitializedListener() {

            // YouTubeプレーヤーの初期化成功
            @Override
            public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    player.loadVideo(VIDEO_ID);
                    player.play();
                }
            }

            // YouTubeプレーヤーの初期化失敗
            @Override
            public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
                // YouTube error
                String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                Log.d("errorMessage:", errorMessage);
            }
        });

        return rootView;
    }

    // 9post判定
    public boolean is_not_9post(String url) {
        String str = url;
        Pattern p = Pattern.compile("http://9post.jp/.*");
        Matcher a = p.matcher(str);
        if (a.find()) {
            return false;
        }else{
            return true;
        }
    }

    // ボタンのリスナー
    @Override
    public void onStart() {
        super.onStart();
        // Facebook
        Button button1 = (Button)getActivity().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Hi!Facebook", Toast.LENGTH_SHORT).show();
                share("com.facebook.katana", url);
            }
        });
        // Twitter
        Button button2 = (Button)getActivity().findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        MyWebView.onResume();
    }

    /***
     * Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        MyWebView.onPause();
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        MyWebView.onPause();
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

}