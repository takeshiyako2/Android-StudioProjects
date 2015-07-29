package headerbutton.post.nine.getjsontolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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


public class SubFragment extends Fragment {

    // ログ出力用のタグ
    private static final String TAG = SubFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

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
        String id = getArguments().getString("id");
        String url = getArguments().getString("url");
        String youtube_id = getArguments().getString("youtube_id");
        Log.d("onCreateView", "id:" + id);
        Log.d("onCreateView", "url:" + url);
        Log.d("onCreateView", "youtube_id:" + youtube_id);

        // レイアウトの設定
        View v = inflater.inflate(R.layout.fragment_sub, container, false);
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
    }

    /***
     * Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /***
     * Fragmentが破棄される時、最後に呼び出される
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}