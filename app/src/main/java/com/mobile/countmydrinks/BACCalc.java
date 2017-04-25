package com.mobile.countmydrinks;

/**
 * This class updates the BAC every 6 minutes.
 *
 * Created by Hanson on 4/23/2017.
 * Updated by Justin Park on 4/24/2017.
 */
public class BACCalc {

    private final int DEFAULT_WEIGHT = 120;
    private final double GRAMS_PER_OZ_ALCOHOL = 23.36;
    private final double BLOOD_WATER_PERCENT = 0.806;
    private final double METABOLIZATION_RATE = 0.0015; // per 6 minutes
    private double weight;
    private double genderCoeff;
    private int numDrinks;
    private double bac;

    public BACCalc(int weight, String gender) {
        if (weight < 0) {
            weight = DEFAULT_WEIGHT;
        }
        this.weight = ((double) weight) / 2.2046;
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
            genderCoeff = .535;
        }

        bac = 0;
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
        double alcoholPerML = GRAMS_PER_OZ_ALCOHOL / totalWater;
        double concentration = 100 * alcoholPerML * BLOOD_WATER_PERCENT;
        double consumed = ounces * alcoholContent;
        double finalBac = concentration * consumed;

        bac += finalBac;
        numDrinks++;
    }

    public int getNumDrinks()
    {
        return numDrinks;
    }

    public void updateBAC() {
        bac = bac - METABOLIZATION_RATE;
        if (bac < 0) {
            bac = 0;
        }
    }

    public double getBac()
    {
        return bac;
    }
}
