package com.mobile.countmydrinks;

import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
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

import java.util.Locale;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        Spinner typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.drink_type_array, R.layout.basic_spinner_item);
        typeAdapter.setDropDownViewResource(R.layout.basic_spinner_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PROFILE_SETTING,0);
                String what = parent.getItemAtPosition(position).toString();
                settings.edit().putString(MainActivity.DRINK_TYPE, what).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        ImageView addButton = (ImageView) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PROFILE_SETTING,0);
                String drinkType = settings.getString(MainActivity.DRINK_TYPE,"Beer");

                mainActivity.addDrink(drinkType);
                
            }
        });
        TextView bacText = (TextView) view.findViewById(R.id.bac);
        if(getArguments() != null) {
            double bac = getArguments().getDouble(MainActivity.BAC);
            String formatBac = String.format(Locale.US, "%.3f", bac);
            bacText.setText(formatBac);
        }
        return view;
    }
}
