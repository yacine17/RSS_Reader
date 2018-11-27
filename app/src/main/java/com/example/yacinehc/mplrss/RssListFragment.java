package com.example.yacinehc.mplrss;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.model.FilRSS;

import java.util.ArrayList;


public class RssListFragment extends Fragment {
    private static final String RSS_LIST = "rssList";

    private ArrayList<FilRSS> rssList;


    public RssListFragment() {

    }

    public static RssListFragment newInstance(ArrayList<FilRSS> rssList) {
        RssListFragment fragment = new RssListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RSS_LIST, rssList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rssList = getArguments().getParcelableArrayList(RSS_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView rv = new RecyclerView(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new RSSRecycleAdapter(rssList));

        return rv;
    }

}
