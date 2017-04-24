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
    private double weight;
    private String gender;
    private double genderCoeff;
    private int numDrinks;
    private double bac;
    boolean init = false;

    public BACCalc(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        settings = mainActivity.getSharedPreferences(MainActivity.PROFILE_SETTING, 0);
        weight = ((double) settings.getInt(mainActivity.WEIGHT_SETTING, -1)) / 2.2046;
        gender = settings.getString(mainActivity.GENDER_SETTING,"Gender");
        if(gender.equals("Male"))
        {
            genderCoeff = .58;
        }
        else if(gender.equals("Female"))
        {
            genderCoeff = .49;
        }
        else
        {
            genderCoeff = .615;
        }
        bac = 0.00;
        numDrinks=0;

    }
    public void addDrink(String type)
    {
        double ounces = 0;
        double alcoholContent = 0;
        if(type.equals("Beer"))
        {
            ounces = 12;
            alcoholContent = .045;
        }
        else if(type.equals("Wine"))
        {
            ounces = 5;
            alcoholContent = .125;
        }
        else if(type.equals("Hard Liquor"))
        {
            ounces = 1.5;
            alcoholContent = .4;
        }

        double totalWater = weight * genderCoeff * 1000;
        double alcoholPerMl = 23.36 / totalWater;
        double concentration = 100 * alcoholPerMl * .806; //.806 blood is composed of 80.6% of water
        double consumed = ounces * alcoholContent;
        double finalBac = concentration * consumed;

        bac = finalBac;
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
