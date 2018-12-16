package com.example.yacinehc.mplrss.rssItems;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.model.RssItem;

import java.util.ArrayList;

public class RssItemsFragment extends Fragment {
    private static final String RSS_ITEMS = "rssItems";

    private ArrayList<RssItem> mRssItems;
    private RssItemsAdapter rssItemsAdapter;

    public RssItemsFragment() {
        // Required empty public constructor
    }

    public static RssItemsFragment newInstance(ArrayList<RssItem> rssItems) {
        RssItemsFragment fragment = new RssItemsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RSS_ITEMS, rssItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRssItems = getArguments().getParcelableArrayList(RSS_ITEMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView rv = new RecyclerView(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rssItemsAdapter = new RssItemsAdapter(mRssItems);
        rv.setAdapter(rssItemsAdapter);


        return rv;
    }

    @Override
    public void onResume() {
        super.onResume();
        rssItemsAdapter.setOnRssItemSelected((RssItemsAdapter.OnRssItemSelected) getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
