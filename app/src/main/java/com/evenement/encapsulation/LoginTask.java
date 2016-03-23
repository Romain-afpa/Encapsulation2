package com.evenement.encapsulation;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by romain on 21/03/16.
 */
public class LoginTask extends AsyncTask<String, String, String> {

    private HttpsURLConnection connection = null;
    private URL url = null;
    private InputStream stream = null;
    private WebView webView;
    private String csrfToken;
    private String formAction;
    private final String username = "librinfo";
    private final String password = "cR4MP0u=€";

    public LoginTask() {
    }

    public LoginTask(WebView webView) {

        this.webView = webView;
    }


    @Override
    protected String doInBackground(String... params) {

        String html = readStream(getConnectionStream(params[0]));

        parseResponse(html);

        String pageContent = readStream(postLogin(params[0]));

        return pageContent;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);

        //postLogin("https://dev3.libre-informatique.fr" + formAction);


        webView.loadDataWithBaseURL("https://dev3.libre-informatique.fr/tck.php/ticket/control", data, "text/html; charset=UTF-8",null, "https://dev3.libre-informatique.fr/tck.php/ticket/control");

    }

    private String readStream(InputStream stream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuffer buffer = new StringBuffer();

        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }

    private InputStream getConnectionStream(String uri) {

        try {

            url = new URL(uri);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        assignTrustManager();

        try {

            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");

            connection.connect();

            switch (connection.getResponseCode()) {

                case 200:
                    stream = connection.getInputStream();
                    break;

                case 401:
                    stream = connection.getErrorStream();
                    break;

                default:
                    stream = connection.getErrorStream();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }finally{
            if(connection != null){

                //connection.disconnect();
            }
        }
        return stream;
    }

    private void assignTrustManager() {

        TrustManager manager = new TrustManager();

        TrustManager[] trustAllCerts = new TrustManager[]{manager};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseResponse(String html) {

        Document doc = Jsoup.parse(html);

        Elements tokenTag = doc.select("#signin__csrf_token");
        csrfToken = tokenTag.attr("value");

        Elements formTag = doc.select(".login form");
        formAction = formTag.attr("action");
    }

    private InputStream postLogin(String uri) {

        InputStream input = null;

        String cookies = CookieManager.getInstance().getCookie(uri);

        URL url = null;

        try {
            url = new URL(uri);

            assignTrustManager();

            connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(getQuery());
            writer.flush();

            connection.connect();
            Log.d("aa", connection.getResponseCode() + "");
            Log.d("aa", connection.getResponseMessage() + "");

            if(connection.getResponseCode() == 200){

                input = connection.getInputStream();
            }else{
                input =  connection.getErrorStream();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

    private String getQuery() {

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("signin[username]", username)
                .appendQueryParameter("signin[password]", password)
                .appendQueryParameter("signin[_csrf_token]", csrfToken);

        return builder.build().getEncodedQuery();
    }
}//taskClass
