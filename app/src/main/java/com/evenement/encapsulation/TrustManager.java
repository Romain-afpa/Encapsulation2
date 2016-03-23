package com.evenement.encapsulation;

import java.security.cert.CertificateException;

import javax.net.ssl.X509TrustManager;

/**
 * Created by romain on 22/03/16.
 */
public class TrustManager implements X509TrustManager{

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
    }
}
