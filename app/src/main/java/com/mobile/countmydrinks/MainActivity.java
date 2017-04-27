package com.mobile.countmydrinks;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* STRING KEYS FOR SHARED PREFERENCES */
    public final static String PROFILE_SETTING = "profile settings";
    public final static String GENDER_SETTING = "gender settings";
    public final static String WEIGHT_SETTING = "weight settings";
    public final static String DRINK_TYPE = "drink type";

    /* STRING KEYS FOR FRAGMENT TAGS */
    public final static String HOME_TAG = "home tag";
    public final static String PROFILE_TAG = "profile tag";
    public final static String REACTION_TAG = "reaction tag";
    public final static String ABOUT_TAG = "about tag";

    /* STRING KEYS FOR HOME FRAGMENT */
    public static final String CURRENT_BAC = "current bac";
    public static final String TOTAL_DRINKS = "total drinks";

    /* KEEPS TRACK OF CURRENT FRAGMENT TAG */
    String currTag;

    TimeAsyncTask timeAsyncTask;
    BACCalc bacCalc;
    boolean running;
    Bundle homeBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        navigationView.getMenu().getItem(0).setChecked(true);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new HomeFragment(), HOME_TAG);
        ft.commit();
        currTag = HOME_TAG;

        SharedPreferences settings = getSharedPreferences(PROFILE_SETTING, 0);

        if (!settings.getBoolean("has_profile", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.alert_title);
            builder.setMessage(R.string.alert_message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            settings.edit().putBoolean("has_profile", false).apply();
        }

        int weight = settings.getInt(MainActivity.WEIGHT_SETTING, -1);
        String gender = settings.getString(MainActivity.GENDER_SETTING, "Gender");

        bacCalc = new BACCalc(weight, gender);
        running = false;
        homeBundle = new Bundle();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String fragTag = "";

        if (id == R.id.home_page) {
            fragment = new HomeFragment();
            homeBundle.putDouble(CURRENT_BAC, this.getBac());
            homeBundle.putInt(TOTAL_DRINKS, this.getNumDrinks());
            fragment.setArguments(homeBundle);
            fragTag = HOME_TAG;
        }
        else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
            fragTag = PROFILE_TAG;
        }
        else if (id == R.id.nav_reaction) {
            fragment = new ReactionFragment();
            fragTag = REACTION_TAG;
        }
        else if (id == R.id.nav_about) {
            fragment = new AboutFragment();
            fragTag = ABOUT_TAG;
        }

        if (!fragTag.equals(currTag) && fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragTag);
            ft.commit();
            currTag = fragTag;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addDrink(String drinkType) {
        bacCalc.addDrink(drinkType);
    }

    public double getBac() {
        return bacCalc.getBac();
    }

    public int getNumDrinks() {
        return bacCalc.getNumDrinks();
    }

    public void onTouchFired() {
        Log.d("asdf", "asdf");
        if (currTag.equals(REACTION_TAG)) {
            ReactionFragment reactionFragment =
                    (ReactionFragment) getSupportFragmentManager().findFragmentByTag(REACTION_TAG);
            reactionFragment.onTouchFired();
        }
    }

    public void startBACCalc() {
        if (!running) {
            timeAsyncTask = new TimeAsyncTask();
            timeAsyncTask.execute();
            running = true;
        }
    }

    private class TimeAsyncTask extends AsyncTask<Double, Double, Void> {

        @Override
        protected Void doInBackground(Double... integers) {
            while (running) {
                try {
                    Thread.sleep(360000); // sleep for 6 minutes
                }
                catch (Exception e) {
                    System.out.println(e);
                }
                bacCalc.updateBAC();
                publishProgress(bacCalc.getBac());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            double val = values[0];
            if (val < 0) {
                val = 0;
                running = false;
            }
            String formatBac = String.format(Locale.US, "%.3f", val);
            formatBac += "%";
            if (currTag.equals(HOME_TAG)) {
                HomeFragment homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
                homeFrag.setBacText(formatBac);
            }
        }
    }
}
