package xyz.lmiceli.webviewbench;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //UrlLoadingTestViewActivity.startUrlForResult(this, "https://lmiceli.github.io");
    GenericWebViewActivity.startUrlForResult(this, "https://www.zarahome.com/es/en/");
    //JsWebViewActivity.startUrlForResult(this);
    //BenchActivity.startUrlForResult(this);
    //WebDaumActivity.startUrlForResult(this);
  }
}
