package com.mobile.countmydrinks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AboutFragment extends Fragment {

TextView drinkInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);
        drinkInfo = (TextView) view.findViewById(R.id.drinkInfo);
        drinkInfo.setText("Party Positive Zone BAC  <= .06 \n"+
                "Legal Driving Zone BAC <= .08 \n"+
                "Danger Zone BAC  <= .10 \n");


        return view;
    }
}
