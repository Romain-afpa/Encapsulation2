package com.evenement.encapsulation;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

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
}