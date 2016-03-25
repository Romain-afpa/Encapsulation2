package com.evenement.encapsulation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.net.CookieHandler;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private WebSettings settings;
    private LoginTask loginTask;
    private KeepSessionTask keepSessionTask;
    private SharedPreferences preferences;
    private String username;
    private String password;
    private String server;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupSharedPreferences();

SharedPreferences.Editor editor= preferences.edit();

        editor.remove("username");
        editor.remove("password");
        editor.remove("server");
        editor.clear();
        editor.commit();

        setupCookieManager();

        setContentView(R.layout.activity_main);

        setupWebview();

        loginTask = new LoginTask(webView, MainActivity.this, server, username, password);

        checkNetwork();

        keepSessionAlive();

        //actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerLayout
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //DrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //DrawerFragment
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame_new, new LeftFragment(drawer),
                        "LeftFragment").commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupWebview() {

        webView = (WebView) findViewById(R.id.webview);
        settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
    }


    private void setupCookieManager() {

        android.webkit.CookieSyncManager.createInstance(MainActivity.this);

        android.webkit.CookieManager.getInstance().setAcceptCookie(true);

        WebCookieManager coreCookieManager = new WebCookieManager(null, java.net.CookiePolicy.ACCEPT_ALL);

        java.net.CookieHandler.setDefault(coreCookieManager);
    }

    private void showDialog(String message, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog alertDialog = builder.create();

        alertDialog.setTitle("Erreur réseau");
        alertDialog.setMessage(message);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                checkNetwork();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                System.exit(0);
            }
        });
        alertDialog.show();
    }

    private boolean hasInternet() {

        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void checkNetwork(){

        if (!hasInternet()) {

            showDialog("Connexion réseau indisponible", "Réessayer");
        }else if(!checkPreferences()){

            showDialog("Informations érronées", "Configurer");

        }else{
           loginTask.execute();
        }
    }

    private void keepSessionAlive(){

        keepSessionTask = new KeepSessionTask(server, username, password);

        long delay = 1000*60*5;

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                keepSessionTask.execute();
            }
        },delay, delay);
    }

    private void setupSharedPreferences(){

        preferences = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

        username = preferences.getString("username", "");
        password = preferences.getString("password", "");
        server = preferences.getString("server", "");
    }

    private boolean checkPreferences() {

        if ("".equals(username) || username == null) {
            return false;
        }

        if ("".equals(password) || password == null) {
            return false;
        }

        if ("".equals(server) || server == null) {
            return false;
        }

        return true;
    }
}
