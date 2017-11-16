package xyz.lmiceli.webviewbench;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
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
          //+ "console.log(data); "
          //+ "console.log(JSON.stringify(data)); "
          + "ItxDaumService.processDaumAddress(data.roadAddress, data.bname, data.buildingName, data.zonecode, data.autoRoadAddress); "
          + " },"
          + "onresize:function(size){e.style.height=size.height+'px';},width:'100%',height:'100%'}).embed(e);}</script>";

  protected static String originalDAUM_HTML_CODE =
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

    };

    webView.setWebViewClient(client);
  }

  class ItxDaumServiceInterface {

    ItxDaumServiceInterface() {
    }

    @android.webkit.JavascriptInterface
    public void processDaumAddress(final String roadAddress, final String bname, final String buildingName, final String zonecode, final String autoRoadAddress) {

      /**
       * the rules are:
       * 1) address is roadAddress or autoRoadAddress less the city part, plus "(bname, buildingName)"
       * 2) city is address until first blank
       * 3) zipcode is zonecode
       * 4) province is KRNONE
       */


      Log.w("DAUM roadAddress: ", roadAddress);
      Log.w("DAUM bname: ", bname);
      Log.w("DAUM buildingName: ", buildingName);
      Log.w("DAUM zonecode: ", zonecode);
      Log.w("DAUM autoRoadAddress: ", autoRoadAddress);

      String address = !TextUtils.isEmpty(roadAddress) ? roadAddress : autoRoadAddress;

      String[] addressPart = address.split("\\s", 2);

      String city = "";

      if (addressPart.length == 2) {
        city = addressPart[0];
        address = addressPart[1];
      }

      address = address + " (" + bname + ", " + buildingName + ")";

      Log.w("DAUM address: ", address);
      Log.w("DAUM city: ", city);
      Log.w("DAUM zone: ", zonecode);




      /*
        address = roadAddress + " " + (data.bname, + " " + data.buildingName); => roadAddress (desde el primer espacio en blanco hasta el final) + bname + ' ' + buildingName
        city = data.roadAddress.substr(0,data.roadAddress.indexOf(' ')); => hasta el primer espacio en blanco de roadAddress
        ZIPCODE = zonecode
        PROVINCIA = KRNONE
      */


      //(data.roadAddress ? data.roadAddress : data.autoRoadAddress)



    }
    //public void processDaumAddress(final String paramString) {
    //  Log.w("DAUM ADDRESS: ", paramString);
    //  //DialogUtils.createOkDialog(CoreaDaumActivity.this, paramString, true, null);
    //
    //  String[] addressComponents = paramString.split(DELIMITER_STRING);
    //  String zipCode = addressComponents[0];
    //  String addressLine1 = addressComponents[1];
    //  String addressLine2 = "";
    //}
  }
}
