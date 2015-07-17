package com.example.yako.webviewswipetorefresh;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends Activity
{
    WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout)findViewById(R.id.swipe);
        browser = (WebView)findViewById(R.id.webView1);
        browser.setWebViewClient(new MyBrowser());
        browser.loadUrl("http://www.yahoo.co.jp/");
        swipeView.setColorSchemeResources(android.R.color.holo_blue_dark,android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeView.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        swipeView.setRefreshing(false);
                        browser.loadUrl("http://www.yahoo.co.jp/");
                    }
                }, 1000);
            }
        });
    }

    private class MyBrowser extends WebViewClient  //If you click on any link inside the webpage of the WebView , that page will not be loaded inside your WebView. In order to do that you need to extend your class from WebViewClient by using the below function.
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
