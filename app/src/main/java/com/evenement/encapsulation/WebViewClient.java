package com.evenement.encapsulation;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.CookieHandler;

/**
 * Created by romain on 21/03/16.
 */
public class WebViewClient extends android.webkit.WebViewClient {

    public WebViewClient() {
    }


    @Override
    public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {

        handler.proceed();

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
        Log.d("aa", "cookiess:" + cookies);
    }
}