package com.evenement.encapsulation;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.net.CookieHandler;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private WebSettings settings;
    private CookieManager cookieManager;
    private LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        CookieHandler.setDefault(new java.net.CookieManager());
        CookieSyncManager.createInstance(MainActivity.this);

        setContentView(R.layout.activity_main);

        //actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //DrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Listener Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new navItemListener(MainActivity.this, drawer));

        configWebview();

        loginTask = new LoginTask(webView);

        webView.setWebViewClient(new WebViewClient());

        cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie("https://dev3.libre-informatique.fr");

        //if (!cookie.contains("symfony")){

//Log.d("aa", "cookie absent");
            //tâche de connexion

           loginTask.execute("https://dev3.libre-informatique.fr/tck.php/ticket/control");
        //}else{

           // Log.d("aa", "cookie présent");

        //}
        CookieSyncManager.getInstance().sync();

       // webView.loadUrl("https://dev3.libre-informatique.fr/tck.php/ticket/control");


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

    private void configWebview(){

        webView = (WebView) findViewById(R.id.webview);
        settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
    }
}
