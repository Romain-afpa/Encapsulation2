package com.evenement.encapsulation;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
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
    private Context context;
    private ProgressDialog dialog;
    private String formAction;
    private final String username = "librinfo";
    private final String password = "cR4MP0u=€";
    private String _URL;


    public LoginTask(WebView webView, Context context) {

        this.webView = webView;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog();
    }

    @Override
    protected String doInBackground(String... params) {

        _URL = params[0];

        login(_URL);

        return "";
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);

        webView.loadUrl("https://dev3.libre-informatique.fr/tck.php/ticket/control");
        hideProgressDialog();
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

    private boolean postLogin(String uri) {

        InputStream input = null;
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

            Log.d("aa", "cookiePost: " + CookieManager.getInstance().getCookie("https://dev3.libre-informatique.fr"));
            Log.d("aa", connection.getResponseCode() + "");
            Log.d("aa", connection.getResponseMessage() + "");

            if (connection.getResponseCode() == 200) {

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getQuery() {

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("signin[username]", username)
                .appendQueryParameter("signin[password]", password)
                .appendQueryParameter("signin[_csrf_token]", csrfToken);

        return builder.build().getEncodedQuery();
    }

    private boolean login(String url) {

        String html = readStream(getConnectionStream(url));

        parseResponse(html);

        return postLogin(url);
    }

    private void showProgressDialog(){

        if(dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setTitle(context.getString(R.string.progressDialogTitle));
            dialog.setMessage(context.getString(R.string.progressDialogMessage));
        }
        dialog.show();
    }

    private void hideProgressDialog() {

        if(dialog.isShowing()){

            dialog.hide();
        }
    }
}//taskClass
