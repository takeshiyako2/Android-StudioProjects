package monst.post.nine.fragmenttest;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    private Boolean ThisIsFlagment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            // MainActivity非表示
            findViewById(R.id.text1).setVisibility(View.GONE);

            // Fragmentに受け渡す値
            Bundle args = new Bundle();
            args.putString("test", "AAA1000");

            // Fragment表示
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            BlankFragment frag1 = new BlankFragment();
            frag1.setArguments(args);
            fragmentTransaction.replace(R.id.fragment, frag1);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();

            // フラグをtrue
            ThisIsFlagment = true;

            // アクションバーに戻る(<-)を表示
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // アクションバーの戻るを押したときの処理
        else if(id == android.R.id.home){
            makeBackFromFragment();
        }

        return super.onOptionsItemSelected(item);
    }


    // 戻るボタンを押した時
    @Override
    public void onBackPressed() {
        makeBackFromFragment();
    }

    // Fragmentから戻る処理
    public void makeBackFromFragment() {
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();

        Log.d("onBackPressed", "ThisIsFlagment:" + ThisIsFlagment);

        if (ThisIsFlagment) {

            // Fragment終了
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {
                fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.fragment));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }

            // MainActivity表示
            findViewById(R.id.text1).setVisibility(View.VISIBLE);

            // フラグをfalse
            ThisIsFlagment = false;

            // アクションバーの戻る(<-)を消す
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

}
