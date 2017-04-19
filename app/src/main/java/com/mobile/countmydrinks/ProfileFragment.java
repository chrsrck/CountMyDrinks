package com.mobile.countmydrinks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    EditText weightEdit;
    Button updateButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        /* Initializing the spinner */
        Spinner spinner = (Spinner) view.findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gender_array, R.layout.basic_spinner_item);
        adapter.setDropDownViewResource(R.layout.basic_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PROFILE_SETTING, 0);
                String what = parent.getItemAtPosition(position).toString();
                settings.edit().putString(MainActivity.GENDER_SETTING, what).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        /* Initializing the EditText for weight */
        weightEdit = (EditText) view.findViewById(R.id.weightEdit);
        weightEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                weightEdit.setCursorVisible(false);
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(weightEdit.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        weightEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightEdit.setCursorVisible(true);
            }
        });

        /* Initializing the Update Profile Button */
        updateButton = (Button) view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Saving the profile data */
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PROFILE_SETTING, 0);
                String currGender = settings.getString(MainActivity.GENDER_SETTING, "Gender");
                String weightText = weightEdit.getText().toString();
                if (!weightText.isEmpty()) {
                    int weight = Integer.parseInt(weightText);
                    settings.edit().putInt(MainActivity.WEIGHT_SETTING, weight).apply();
                }

                /* If the user has both updated their gender and weight then the data is saved */
                if (!currGender.equals("Gender") && weightText.length() > 0) {
                    settings.edit().putBoolean("has_profile", true).apply();
                }
                else {
                    settings.edit().putBoolean("has_profile", false).apply();
                }
            }
        });

        /* Checking if the user has updated the profile and sets up their info */
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PROFILE_SETTING, 0);
        String currGender = settings.getString(MainActivity.GENDER_SETTING, "Gender");
        if (!currGender.equals("Gender")) {
            spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition(currGender));
        }
        int currWeight = settings.getInt(MainActivity.WEIGHT_SETTING, -1);
        if (currWeight > -1) {
            Log.d("OKAY", "OKAY");
            weightEdit.setText("" + currWeight);
        }
        return view;
    }

}
