package com.mobile.countmydrinks;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* STRING KEYS FOR SHARED PREFERENCES */
    public final static String PROFILE_SETTING = "profile settings";
    public final static String GENDER_SETTING = "gender settings";
    public final static String WEIGHT_SETTING = "weight settings";
    public final static String DRINK_TYPE = "drink type";
    public final static String BASELINE = "baseline_value";

    /* STRING KEYS FOR FRAGMENT TAGS */
    public final static String HOME_TAG = "home tag";
    public final static String PROFILE_TAG = "profile tag";
    public final static String REACTION_TAG = "reaction tag";
    public final static String ABOUT_TAG = "about tag";
    public final static String RESOURCE_TAG = "resource tag";

    /* STRING KEYS FOR HOME FRAGMENT */
    public static final String CURRENT_BAC = "current bac";
    public static final String TOTAL_DRINKS = "total drinks";
    public static final String CURRENT_DRINK = "current drink";

    /* KEEPS TRACK OF CURRENT FRAGMENT TAG */
    String currTag;

    TimeAsyncTask timeAsyncTask;
    BACCalc bacCalc;
    boolean running;
    Bundle homeBundle;
    boolean abovePositiveZone;
    boolean hasNotified;
    boolean needsToNotify;

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
        abovePositiveZone = false;
        hasNotified = false;
        needsToNotify = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
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
            SharedPreferences settings = getSharedPreferences(MainActivity.PROFILE_SETTING, 0);
            String currDrink = settings.getString(DRINK_TYPE, "Beer");
            homeBundle.putDouble(CURRENT_BAC, this.getBac());
            homeBundle.putInt(TOTAL_DRINKS, this.getNumDrinks());
            homeBundle.putString(CURRENT_DRINK, currDrink);
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
        else if(id == R.id.resources) {
            fragment = new ResourceFragment();
            fragTag = RESOURCE_TAG;
        }

        if (!fragTag.equals(currTag) && fragment != null) {
            if (currTag.equals(PROFILE_TAG)) {
                // Hide the keyboard on fragment change
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragTag);
            ft.commit();
            currTag = fragTag;
        }

        if (needsToNotify) {
            needsToNotify = false;
            promptEndSession();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addDrink(String drinkType) {
        if (getNumDrinks() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.warning_title);
            builder.setMessage(R.string.warning_message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        bacCalc.addDrink(drinkType);
    }

    public double getBac() {
        return bacCalc.getBac();
    }

    public int getNumDrinks() {
        return bacCalc.getNumDrinks();
    }

    public void onTouchFired() {
        if (currTag.equals(REACTION_TAG)) {
            ReactionFragment reactionFragment =
                    (ReactionFragment) getSupportFragmentManager().findFragmentByTag(REACTION_TAG);
            reactionFragment.onTouchFired();
        }
    }

    public void startBACCalc() {
        if (!running) {
            timeAsyncTask = new TimeAsyncTask();
            timeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            running = true;
        }
    }

    public void notifyUser(boolean above) {
        String contentText;
        if (above) {
            contentText = "You're no longer in the party positive zone!";
            hasNotified = true;
        }
        else {
            contentText = "You're back in the party positive zone";
        }
        int notificationId = 1;
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
        NotificationCompat.Builder notificationBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon_pitcher)
                        .setContentTitle("Count My Drinks")
                        .setContentText(contentText)
                        .setContentIntent(viewPendingIntent)
                        .setVibrate(new long[] {1000, 1000});

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
    }

    public void promptEndSession() {
        timeAsyncTask.cancel(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.end_title);
        builder.setMessage(R.string.end_message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                bacCalc.resetNumDrinks();
                if (currTag.equals(HOME_TAG)) {
                    HomeFragment homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
                    homeFrag.setTotalText(bacCalc.getNumDrinks());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void updateBACSettings(String currGender, int weight) {
        bacCalc.setGenderCoeff(currGender);
        bacCalc.setWeight(weight);
    }

    private class TimeAsyncTask extends AsyncTask<Double, Double, Void> {

        @Override
        protected Void doInBackground(Double... integers) {
            while (running) {
                try {
                    Thread.sleep(5000); // sleep for 6 minutes 360000
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
            if (val <= 0) {
                val = 0;
                running = false;
            }
            else if (val > 0.06 && !hasNotified) {
                abovePositiveZone = true;
                notifyUser(abovePositiveZone);
            }
            else if (val <= 0.06 && abovePositiveZone) {
                abovePositiveZone = false;
                notifyUser(abovePositiveZone);
                hasNotified = false;
            }
            String formatBac = String.format(Locale.US, "%.3f", val);
            formatBac += "%";
            if (currTag.equals(HOME_TAG)) {
                HomeFragment homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
                homeFrag.setBacText(formatBac);
            }
            if (!running) {
                if (!currTag.equals(REACTION_TAG)) {
                    promptEndSession();
                }
                else {
                    needsToNotify = true;
                }
            }
        }
    }
}
