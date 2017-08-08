package oluwaseun.akintola.movies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;

import java.util.List;

import oluwaseun.akintola.movies.domain.TrailerBean;

/**
 * Created by AKINTOLA OLUWASEUN on 8/7/2017.
 */

public class TrailerViewActivity extends AppCompatActivity{
    private WebView mWebView;
    private ProgressBar progress;
    private List<TrailerBean.AllResults> Trailerlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_dialog);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String seen3 = "";
        int index = 0;
        if(extras != null && extras.containsKey("index") &&
                extras.containsKey("seen3")) {
            index = intent.getIntExtra("index", 0);
            seen3 = intent.getStringExtra("seen3");
        }

        TrailerBean models = JSON.parseObject(seen3, TrailerBean.class);
        List<TrailerBean.AllResults> mod = models.getResults();

        Trailerlist = mod;

        Uri uri = Uri.parse(getString(R.string.youtube_url)).buildUpon()
                .appendQueryParameter("v", Trailerlist.get(index).getKey()).build();

        mWebView = (WebView) findViewById(R.id.webView);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        mWebView.getSettings().setJavaScriptEnabled(true);
       // mWebView.loadUrl("http://www.youtube.com/watch?v=");
       // mWebView.setWebViewClient(new MyWebViewClient());
       // mWebView.loadUrl(String.valueOf(uri));
        Log.d("URI", String.valueOf(uri));

        String playVideo= "<html><body>Youtube video .. <br> <iframe class=\"youtube-player\" type=\"text/html\" width=\"640\" height=\"385\" src=\"http://www.youtube.com/embed/"+Trailerlist.get(index).getKey()+"\" frameborder=\"0\"></body></html>";

        mWebView.loadData(playVideo, "text/html", "utf-8");
    }


    private class MyWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(String.valueOf(request.getUrl()));
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            progress.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }

}
