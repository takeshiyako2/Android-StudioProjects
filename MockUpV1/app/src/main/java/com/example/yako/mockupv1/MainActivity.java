package com.example.yako.mockupv1;

import java.util.Locale;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    private static String TAG = "MainActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        // Action barのモードをタブモードに切り替え
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // FragmentPagerAdapterを継承したクラスのアダプターを作成
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        // ViewPagerにSectionPagerAdapterをセット
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        // スワイプしたときにもActionbarのタブ（NavigationItem）を常に表示させる処理
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        // getCountでタブの数を指定。
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            // Actionbarにタブを追加。
            // getPageTitleでタブのタイトルを表示
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    // メニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // メニュー Settingsを押したとき
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return false;
    }

    // タブを選択した時の処理
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    // タブの選択が外れた場合の処理
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    // タブが2度目以降に選択された場合の処理
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    // ViewPagerの動作を作成
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        // タブの数を決定
        @Override
        public int getCount() {
            // Show 4 total pages.
            return 8;
        }

        // タブのタイトルを決定
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section5).toUpperCase(l);
                case 5:
                    return getString(R.string.title_section6).toUpperCase(l);
                case 6:
                    return getString(R.string.title_section7).toUpperCase(l);
                case 7:
                    return getString(R.string.title_section8).toUpperCase(l);
                case 8:
                    return getString(R.string.title_section9).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int position = getArguments().getInt(ARG_SECTION_NUMBER);
            RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);

            FrameLayout layout = (FrameLayout)rootView.getChildAt(0);

            layout.setId(ViewUtil.generateViewId());

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if(position == 1){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment1.newInstance())
                        .commit();
            }
            else if(position == 2){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment2.newInstance())
                        .commit();
            }
            else if(position == 3){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment3.newInstance())
                        .commit();
            }
            else if(position == 4){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment4.newInstance())
                        .commit();
            }
            else if(position == 5){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment5.newInstance())
                        .commit();
            }
            else if(position == 6){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment6.newInstance())
                        .commit();
            }
            else if(position == 7){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment7.newInstance())
                        .commit();
            }
            else if(position == 8){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment8.newInstance())
                        .commit();
            }
            else if(position == 9){
                fragmentManager.beginTransaction()
                        .replace(layout.getId(), Fragment9.newInstance())
                        .commit();
            }
            return rootView;
        }


    }




}