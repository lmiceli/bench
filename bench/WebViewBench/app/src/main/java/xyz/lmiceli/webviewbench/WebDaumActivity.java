package xyz.lmiceli.webviewbench;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebDaumActivity extends WebViewBaseActivity {

  public static final int AUTHORIZATION_REQUEST = 1;
  public static final int RESULT_ERROR = 2;
  private static final String TAG = "WebViewCheckoutRedirect";

  protected static final String DELIMITER_STRING = "ITXSEPARATOR";
  protected static String DAUM_HTML_CODE =
      "<div id='d'/><script src='https://spi.maps.daum.net/imap/map_js_init/postcode.v2.js'></script>"
          + "<script>window.onload=function(){"
          + "var e=document.getElementById('d');"
          + "new daum.Postcode({oncomplete:function(data){ "
          + "ItxDaumService.processDaumAddress(data.zonecode + '"
          + DELIMITER_STRING
          + "' + (data.roadAddress ? data.roadAddress : data.autoRoadAddress) + ' (' + data.bname + ',' + data.buildingName + ')'); },onresize:function(size){e.style.height=size.height+'px';},width:'100%',height:'100%'}).embed(e);}</script>";


  public static void startUrlForResult(Activity context) {
    Intent starter = new Intent(context, WebDaumActivity.class);
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_HTML, DAUM_HTML_CODE);
    starter.putExtra(KEY_HTML_BASE_URL, "https://spi.maps.daum.net/");
    //starter.putExtra(KEY_TOOLBAR_TITLE_RESOURCE, toolbarTitleResId);
    context.startActivityForResult(starter, AUTHORIZATION_REQUEST);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    webView.addJavascriptInterface(new ItxDaumServiceInterface(), "ItxDaumService");
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void setWebViewClients() {

    final WebViewClient client = new WebViewClient() {

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (!BuildConfig.BUILD_TYPE.equals("release")) {
          handler.proceed();
        }
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.loadUrl("javascript:ItxDaumService.processDaumAddress('GOATSEITXSEPARATORMIERDA');");
      }
    };

    webView.setWebViewClient(client);
  }

  class ItxDaumServiceInterface {

    ItxDaumServiceInterface() {
    }

    @android.webkit.JavascriptInterface
    public void processDaumAddress(final String paramString) {
      Log.w("DAUM ADDRESS: ", paramString);
      //DialogUtils.createOkDialog(CoreaDaumActivity.this, paramString, true, null);

      String[] addressComponents = paramString.split(DELIMITER_STRING);
      String zipCode = addressComponents[0];
      String addressLine1 = addressComponents[1];
      String addressLine2 = "";
    }
  }
}
