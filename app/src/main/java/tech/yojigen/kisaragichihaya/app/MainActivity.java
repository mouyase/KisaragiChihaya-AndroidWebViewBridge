package tech.yojigen.kisaragichihaya.app;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import tech.yojigen.kisaragichihaya.Chihaya;
import tech.yojigen.kisaragichihaya.WebViewBridgeClient;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        Chihaya chihaya = new Chihaya(webView);
        webView.setWebViewClient(new WebViewBridgeClient(chihaya));
        chihaya.registCallback("onQQLogin");
        chihaya.registListener("test", () -> {
            System.out.println("啊啊啊啊");
            try {
                chihaya.callback("onQQLogin", new JSONObject(""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
//        webView.loadUrl("https://m.baidu.com");
        webView.loadUrl("file:///android_asset/index.html");
//        webView.loadUrl("http://share.danaopai.cn/");
//        webView.loadUrl("https://4cy.me");
//        webView.loadUrl("https://4cy.me/test.html");
//        webView.loadUrl("https://4cy.me/test.html");
    }
}
