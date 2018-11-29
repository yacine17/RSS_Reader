package com.example.yacinehc.mplrss;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.utils.SimpleDialogFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;


public class RssListFragment extends Fragment  {
    private static final String RSS_LIST = "rssList";
    private List<Long> idList;
    private List<RSS> rssList;
    private DownloadManager downloadManager;


    public RssListFragment() {
        idList = new ArrayList<>();
    }

    public void initFragment() {
        downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (idList.contains(reference)) {
                    idList.remove(reference);

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        String fileTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                        System.out.println("fileTitle = " + fileTitle);
                        String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        System.out.println("localPath = " + localPath);
                        Snackbar snackbar = Snackbar.make(getView(), "Téléchargement finie : " + fileTitle, Snackbar.LENGTH_LONG);
                        snackbar.show();

                        File file = new File(localPath);
                        try {

                            InputStream inputStream = new FileInputStream(localPath);
                            System.out.println("URLConnection.guessContentTypeFromStream(inputStream) = " + URLConnection.guessContentTypeFromStream(inputStream));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        };

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

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

        FloatingActionButton fab = view.findViewById(R.id.fab);
        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        final RssListFragment thisInstance = this;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("RSS URL", "Entrez un nouveau URL");
                simpleDialogFragment.setTargetFragment(thisInstance, 0);
                simpleDialogFragment.show(fragmentTransaction, "SimpleDialogFragment");
            }
        });

        return view;
    }


    public void downloadFile(Uri uri) {

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS, "mplrss");

        idList.add(downloadManager.enqueue(request));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initFragment();
    }

    public void addRss(Uri uri) {
        downloadFile(uri);
    }
}
