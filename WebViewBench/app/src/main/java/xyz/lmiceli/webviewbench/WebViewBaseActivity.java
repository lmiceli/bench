package xyz.lmiceli.webviewbench;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.lmiceli.webviewbench.BuildConfig;
import xyz.lmiceli.webviewbench.R;

public abstract class WebViewBaseActivity extends AppCompatActivity {

  protected static String KEY_URL = "URL";
  protected static String KEY_TOOLBAR_TITLE_RESOURCE = "KEY_TOOLBAR_TITLE_RESOURCE";
  protected static String KEY_TOOLBAR_TITLE = "KEY_TOOLBAR_TITLE";
  protected static String KEY_HTML = "HTML";
  protected static String KEY_HTML_BASE_URL = "HTML_BASE_URL";
  protected static String KEY_POST = "POST";

  protected WebView webView;
  /*@BindView(R.id.toolbar_title)
  protected TextView titleView;*/
  @BindView(R.id.loading)
  protected View loaderView;

  protected static boolean resizeWebView = false;

  protected static void startUrlWebViewActivity(Context context, String url, int toolbarTitleResId,
      Intent starter, boolean resize) {
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_URL, url);
    resizeWebView = resize;
    if (toolbarTitleResId != 0) {
      starter.putExtra(KEY_TOOLBAR_TITLE_RESOURCE, toolbarTitleResId);
    }
    context.startActivity(starter);
  }

  protected static void startUrlWebViewActivity(Context context, String url, String toolbarTitle,
      Intent starter, boolean resize) {
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_URL, url);
    starter.putExtra(KEY_TOOLBAR_TITLE, toolbarTitle);
    resizeWebView = resize;
    context.startActivity(starter);
  }

  protected static void startUrlWebViewActivityForResult(Activity context, String url,
      int toolbarTitleResId,
      Intent starter, int requestCode) {
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_URL, url);
    starter.putExtra(KEY_TOOLBAR_TITLE_RESOURCE, toolbarTitleResId);
    context.startActivityForResult(starter, requestCode);
  }

  protected static void startHtmlWebViewActivity(Context context, String html,
      int toolbarTitleResId,
      Intent starter, boolean resize) {
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_HTML, html);
    starter.putExtra(KEY_TOOLBAR_TITLE_RESOURCE, toolbarTitleResId);
    resizeWebView = resize;
    context.startActivity(starter);
  }

  protected static void startHtmlWebViewActivity(Context context, String html,
      String toolbarTitle,
      Intent starter, boolean resize) {
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_HTML, html);
    starter.putExtra(KEY_TOOLBAR_TITLE, toolbarTitle);
    resizeWebView = resize;
    context.startActivity(starter);
  }

  protected static void startPostWebViewActivity(Activity context, String url, String postData,
      int toolbarTitleResId, Intent starter, int requestCode) {
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_URL, url);
    starter.putExtra(KEY_POST, postData);
    starter.putExtra(KEY_TOOLBAR_TITLE_RESOURCE, toolbarTitleResId);

    context.startActivityForResult(starter, requestCode);
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_webview);
    ButterKnife.bind(this);
    // Get reference of WebView from layout/activity_main.xml
    if (resizeWebView) {
      webView = (WebView) findViewById(R.id.webview2);
      webView.setVisibility(View.VISIBLE);
    } else {
      webView = (WebView) findViewById(R.id.webview);
      webView.setVisibility(View.VISIBLE);
    }


    webView.setWebViewClient(new WebViewClient() {
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (!BuildConfig.BUILD_TYPE.equals("release")) {
          handler.proceed();
        }
      }
    });

    setUpWebViewDefaults(webView);

    // Check whether we're recreating a previously destroyed instance
    if (savedInstanceState != null) {
      // Restore the previous URL and history stack
      webView.restoreState(savedInstanceState);
    }

    if (getIntent().hasExtra(KEY_POST)) {
      String urlToPost = getIntent().getStringExtra(KEY_URL);
      String postData = getIntent().getStringExtra(KEY_POST);
      webView.postUrl(urlToPost, postData.getBytes());
    } else if (getIntent().hasExtra(KEY_URL)) {
      String url = getIntent().getStringExtra(KEY_URL);
      webView.loadUrl(url);
    } else if (getIntent().hasExtra(KEY_HTML)) {
      String html = getIntent().getStringExtra(KEY_HTML);
      webView.getSettings().setDefaultTextEncodingName("utf-8");
      String baseUrl = null;
      if (getIntent().hasExtra(KEY_HTML_BASE_URL)) {
        baseUrl = getIntent().getStringExtra(KEY_HTML_BASE_URL);
      }
      webView.loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null);
    }

    setWebViewClients();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  protected void setUpWebViewDefaults(WebView webView) {
    WebSettings settings = webView.getSettings();
    // Enable Javascript
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    webView.setWebViewClient(new WebViewClient());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      WebView.setWebContentsDebuggingEnabled(true);
    }
  }

  protected abstract void setWebViewClients();

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      // clear webview
      super.onBackPressed();
    }
  }
}
