package com.example.yacinehc.mplrss;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.utils.SimpleDialogFragment;

import java.util.ArrayList;


public class RssListFragment extends Fragment {
    private static final String RSS_LIST = "rssList";

    private ArrayList<RSS> rssList;


    public RssListFragment() {
    }

    public static RssListFragment newInstance(ArrayList<RSS> rssList) {
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
        View view = inflater.inflate(R.layout.fragment_rss_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RSSRecycleAdapter(rssList));

        FloatingActionButton fab =  view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("RSS URL", "Entrez un nouveau URL");
                simpleDialogFragment.show(fragmentTransaction, "SimpleDialogFragment");
            }
        });

        return view;
    }


    /*public void downloadFile(Uri uri) {
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS, "mplrss");

        long id = downloadManager.enqueue(request);

    }*/

}
