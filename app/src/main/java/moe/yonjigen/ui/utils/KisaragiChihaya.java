package moe.yonjigen.ui.utils;

import android.webkit.WebView;

public class KisaragiChihaya {
    WebView mWebView;

    private KisaragiChihaya() {
    }

    public void setWebView(WebView webView) {
        this.mWebView = webView;
    }

    public void addFlat(String key, Flat flat) {

    }

    public void runFromJS() {
    }

    public interface Flat {
        void run(String json);
    }
}
