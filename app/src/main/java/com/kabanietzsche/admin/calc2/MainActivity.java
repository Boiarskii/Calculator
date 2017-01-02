package com.kabanietzsche.admin.calc2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        //Check time and choose theme
        int hours = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        if (hours > 23 || hours < 5)
            setTheme(R.style.DarkVariant);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, new MainFragment())
                .commit();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calculator) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new MainFragment())
                    .commit();
        } else if (id == R.id.nav_history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new HistoryFragment())
                    .commit();
        } else if (id == R.id.nav_statistics) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new GraphicsFragment())
                    .commit();

        } else if (id == R.id.nav_share) {

            if (isNetworkAvailable() && ResultModel.last(ResultModel.class) != null)
                if (ShareDialog.canShow(ShareLinkContent.class)) {

                    ResultModel resultModel = ResultModel.last(ResultModel.class);


                    String text = resultModel.getUserName()
                            + getString(R.string.pressed)
                            + resultModel.getButtonsTapped()
                            + getString(R.string.buttons_and_got_result) + Utils.formatResult(resultModel.getResult())
                            + getString(R.string.calculations)
                            + resultModel.getEnteredData();


                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentTitle("Fusion Calculator")
                            .setContentUrl(Uri.parse("http://fusionworks.md/wp-content/uploads/2013/06/fusion.png"))
                            .setQuote(text)
                            .build();

                    shareDialog.show(content);
                }

        } else if (id == R.id.nav_delete) {
            ResultModel.deleteAll(ResultModel.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Check internet connection
     * @return true if network is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
