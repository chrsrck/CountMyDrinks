package com.mobile.countmydrinks;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResourceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resources, container, false);
        TextView sitelink = (TextView) view.findViewById(R.id.sitelink);
        sitelink.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}
