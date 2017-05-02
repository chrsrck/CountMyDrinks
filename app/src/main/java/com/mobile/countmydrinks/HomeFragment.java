package com.mobile.countmydrinks;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Locale;

/**
 * @author Chris
 * @author Justin Park
 */
public class HomeFragment extends Fragment {
    TextView bacText;
    TextView totalText;
    ImageView addButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home, container, false);
        bacText = (TextView) view.findViewById(R.id.bac);
        totalText = (TextView) view.findViewById(R.id.total);
        addButton = (ImageView) view.findViewById(R.id.addButton);
        final MainActivity mainActivity = (MainActivity) getActivity();

        Spinner typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.drink_type_array, R.layout.basic_spinner_item);
        typeAdapter.setDropDownViewResource(R.layout.basic_spinner_item);
        typeSpinner.setAdapter(typeAdapter);

        if(getArguments() != null) {
            double bac = getArguments().getDouble(MainActivity.CURRENT_BAC);
            String formatBac = String.format(Locale.US, "%.3f", bac);
            formatBac += "%";
            bacText.setText(formatBac);

            int numDrinks = getArguments().getInt(MainActivity.TOTAL_DRINKS);
            totalText.setText(numDrinks + " Total Drinks");

            String currDrink = getArguments().getString(MainActivity.CURRENT_DRINK);
            final int picID;
            if (currDrink.equals("Beer")) {
                picID = R.drawable.glasspitcher;
                typeSpinner.setSelection(0);
            }
            else if (currDrink.equals("Wine")) {
                picID = R.drawable.wine_glass;
                typeSpinner.setSelection(1);
            }
            else {
                picID = R.drawable.shot;
                typeSpinner.setSelection(2);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = getActivity().getResources().openRawResource(picID);
                    final Bitmap imageBitmap = BitmapFactory.decodeStream(is);
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            addButton.setImageBitmap(imageBitmap);
                        }
                    });
                }
            });
        }
        else {
            addButton.setImageResource(R.drawable.glasspitcher);
        }

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View spinView, int position, long id) {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PROFILE_SETTING, 0);
                String what = parent.getItemAtPosition(position).toString();
                settings.edit().putString(MainActivity.DRINK_TYPE, what).apply();
                final int picID;
                if (what.equals("Beer")) {
                    picID = R.drawable.glasspitcher;
                }
                else if (what.equals("Wine")) {
                    picID = R.drawable.wine_glass;
                }
                else if (what.equals("Hard Liquor")) {
                    picID = R.drawable.shot;
                }
                else {
                    picID = 0;
                }
                if (picID != 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            InputStream is = getActivity().getResources().openRawResource(picID);
                            final Bitmap imageBitmap = BitmapFactory.decodeStream(is);
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    addButton.setImageBitmap(imageBitmap);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PROFILE_SETTING,0);
                String drinkType = settings.getString(MainActivity.DRINK_TYPE, "Beer");
                mainActivity.addDrink(drinkType);
                mainActivity.startBACCalc();
                String formatBac = String.format(Locale.US, "%.3f", mainActivity.getBac());
                formatBac += "%";
                bacText.setText(formatBac);
                totalText.setText(mainActivity.getNumDrinks() + " Total Drinks");
                String addMsg = "";
                if (drinkType.equals("Beer")) {
                    addMsg = getString(R.string.added_beer);
                }
                else if (drinkType.equals("Wine")) {
                    addMsg = getString(R.string.added_wine);
                }
                else if (drinkType.equals("Hard Liquor")) {
                    addMsg = getString(R.string.added_shot);
                }
                Toast.makeText(getContext(), addMsg, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public void setBacText(String txt) {
        bacText.setText(txt);
    }

    public void setTotalText(int total) {
        totalText.setText(total + " Total Drinks");
    }

}
