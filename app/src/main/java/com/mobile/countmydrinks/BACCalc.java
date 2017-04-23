package com.mobile.countmydrinks;

import android.content.SharedPreferences;

/**
 * Created by Hanson on 4/23/2017.
 */

/**
 * This class updates the BAC every 6 minutes
 */
public class BACCalc {

    MainActivity mainActivity;
    private SharedPreferences settings;
    private int weight;
    private String gender;
    private double genderCoeff;
    private int numDrinks;
    private double bac;
    boolean init = false;

    public BACCalc(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        settings = mainActivity.getSharedPreferences(MainActivity.PROFILE_SETTING, 0);
        weight = settings.getInt(mainActivity.WEIGHT_SETTING, -1);
        gender = settings.getString(mainActivity.GENDER_SETTING,"Gender");
        if(gender.equals("Male"))
        {
            genderCoeff = .68;
        }
        else if(gender.equals("Female"))
        {
            genderCoeff = .55;
        }
        else
        {
            genderCoeff = .615;
        }
        bac = 0.00;
        numDrinks=0;

    }
    public void addDrink(int alcohol)
    {
        bac = bac + ((alcohol)/(weight * genderCoeff)) * 100;
        numDrinks++;
    }
    public int getNumDrinks()
    {
        return numDrinks;
    }


    public void updateBAC()
    {
        bac = bac - .0015;


    }
    public  double getBac()
    {
        return bac;
    }


}
