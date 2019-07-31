package tech.yojigen.kisaragichihaya;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WebViewBridge {
    private WebView mWebView;
    private WebViewBridgeClient mWebViewBridgeClient;
    private Activity mActivity;
    private String javaScript;
    private Map<String, DataListener> mDataListenerMap = new HashMap<>();
    private Map<String, NoDataListener> mNoDataListenerMap = new HashMap<>();
    private List<String> mCallbackList = new ArrayList<>(new HashSet<>());
    private final String TEMPLATE_FUNCTION = "Bridge['%s']=function(data){if(data){bridge.runOnAndroid('%s',JSON.stringify(data))}else{bridge.runOnAndroid('%s',null)}};";
    private final String TEMPLATE_CALLBACK = "Bridge['%s']=function(func){Bridge.functions['%s']=func};";


    public WebViewBridge(WebView webView) {
        this.mWebView = webView;
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        //默认打开JS支持
        this.mActivity = (Activity) this.mWebView.getContext();
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.addJavascriptInterface(this, "bridge");
        this.mWebView.setWebViewClient(this.mWebViewBridgeClient);
    }

    private void createScript() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("var Bridge={};");
        stringBuilder.append("var Chihaya=Bridge;");
        stringBuilder.append("var KisaragiChihaya=Bridge;");
        stringBuilder.append("Bridge['functions']={};");
        stringBuilder.append("Bridge['runFromAndroid']=function(name,value){var func=Bridge.functions[name];if(value){func(JSON.parse(value);)}else{func();}};");
        for (String key : mNoDataListenerMap.keySet()) {
            stringBuilder.append(String.format(TEMPLATE_FUNCTION, key, key, key));
        }
        for (String key : mDataListenerMap.keySet()) {
            stringBuilder.append(String.format(TEMPLATE_FUNCTION, key, key, key));
        }
        for (String key : mCallbackList) {
            stringBuilder.append(String.format(TEMPLATE_CALLBACK, key, key));
        }
        javaScript = stringBuilder.toString();
    }

    public String getJavaScript() {
        createScript();
        return javaScript;
    }

    public void setJavaScript(String javaScript) {
        this.javaScript = javaScript;
    }

    private void runOnJavaScript(String script) {
        mActivity.runOnUiThread(() -> this.mWebView.loadUrl("javascript:" + script));
    }

    @JavascriptInterface
    public void runOnAndroid(String key, String json) {
        mActivity.runOnUiThread(() -> {
            if (json == null) {
                mNoDataListenerMap.get(key).run();
            } else {
                try {
                    mDataListenerMap.get(key).run(new JSONObject(json));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void callback(String key) {
        runOnJavaScript("Bridge.runFromAndroid('" + key + "')");
    }

    public void callback(String key, JSONObject jsonObject) {
        runOnJavaScript("Bridge.runFromAndroid('" + key + "','" + jsonObject.toString() + "')");
    }

    public void registCallback(String key) {
        mCallbackList.add(key);
        createScript();
    }

    public void registListener(String key, DataListener listener) {
        mDataListenerMap.put(key, listener);
        createScript();
    }

    public void registListener(String key, NoDataListener listener) {
        mNoDataListenerMap.put(key, listener);
        createScript();
    }

    public interface DataListener {
        void run(JSONObject jsonObject);
    }

    public interface NoDataListener {
        void run();
    }
}