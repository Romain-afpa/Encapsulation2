package com.evenement.encapsulation;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Created by romain on 21/03/16.
 */
public class navItemListener implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private DrawerLayout drawerLayout;

    public navItemListener(Context context, DrawerLayout drawerLayout){

        this.context = context;
        this.drawerLayout = drawerLayout;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
