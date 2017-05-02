package com.mobile.countmydrinks;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Hanson on 5/1/2017.
 */

public class ResourceFragment extends Fragment {
    TextView sitelink;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resources, container, false);
        sitelink = (TextView) view.findViewById(R.id.sitelink);
        sitelink.setClickable(true);

        sitelink.setText(Html.fromHtml(getResources().getString(R.string.help_site)));
        sitelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    sitelink.setMovementMethod(LinkMovementMethod.getInstance());



            }
        });



        return view;
    }


}
