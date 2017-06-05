package xyz.lmiceli.webviewbench;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebViewClient;

public class BenchActivity extends WebViewBaseActivity {

  public static final int AUTHORIZATION_REQUEST = 1;
  public static final int RESULT_ERROR = 2;
  private static final String TAG = "WebViewCheckoutRedirect";
  public static final String STATIC_URL =
      "https://google.com";

  public static void startUrlForResult(Activity context) {
    Intent starter = new Intent(context, BenchActivity.class);
    starter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    starter.putExtra(KEY_URL, STATIC_URL);
    context.startActivityForResult(starter, AUTHORIZATION_REQUEST);
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void setWebViewClients() {

    final WebViewClient client = new WebViewClient() {

    };

    webView.setWebViewClient(client);
  }

}
