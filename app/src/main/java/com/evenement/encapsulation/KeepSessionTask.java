package com.evenement.encapsulation;

import android.net.Uri;
import android.os.AsyncTask;

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

public class KeepSessionTask extends AsyncTask<String, String, Boolean> {

    private HttpsURLConnection connection = null;
    private URL url = null;
    private InputStream stream = null;
    private String csrfToken;
    private String formAction;
    private String username;
    private String password;
    private String server;
    private final String uri = "/tck.php/ticket/control";


    public KeepSessionTask(String server, String username, String password) {

        this.username = username;
        this.password = password;
        this.server = server;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {

        login(server + uri);

        return login(server + uri);
    }

    @Override
    protected void onPostExecute(Boolean statut) {
        super.onPostExecute(statut);
    }

    private String readStream(InputStream stream) {

        if(stream != null) {

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
        return null;
    }

    private InputStream getConnectionStream(String uri) {

        if (username != null & password != null & server != null) {

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

                int statusCode;
                try {
                    statusCode = connection.getResponseCode();

                } catch (IOException e) {
                    statusCode = connection.getResponseCode();
                }

                switch (statusCode) {

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stream;
        }

        return null;
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
            url = new URL(uri + formAction);

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

            if (connection.getResponseCode() == 200) {

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

        if (html != null) {
            parseResponse(html);

            return postLogin(url);
        }
        return false;
    }
}//taskClass