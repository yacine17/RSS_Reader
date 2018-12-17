package com.example.yacinehc.mplrss;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.db.AccesDonnees;
import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.model.RssItem;
import com.example.yacinehc.mplrss.utils.DownloadHelper;
import com.example.yacinehc.mplrss.utils.MyParser;
import com.example.yacinehc.mplrss.utils.SimpleDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static android.content.Context.DOWNLOAD_SERVICE;


public class RssListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String SCROLL_POSITION = "scrollPosition";
    private final static String SELECTED_ITEMS_LIST = "selectedItemsList";
    private List<Long> idList;
    private CustomCursorRecyclerViewAdapter customCursorRecyclerViewAdapter;
    private LoaderManager loaderManager;
    private RssListFragment thisInstance;
    private MyBroadcastReceiver broadcastReceiver;
    private RecyclerView mRecyclerView;
    private Integer scrollPosition;


    public RssListFragment() {
        idList = new ArrayList<>();
        scrollPosition = 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customCursorRecyclerViewAdapter = new CustomCursorRecyclerViewAdapter(getContext(), null, this);

        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.restartLoader(0, null, this);
        thisInstance = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customCursorRecyclerViewAdapter.getMyObsarvable().addObserver((Observer) getActivity());
        ((MainActivity) getActivity()).getMyObsarvable().addObserver(customCursorRecyclerViewAdapter);
        customCursorRecyclerViewAdapter.setOnItemClickListener((CustomCursorRecyclerViewAdapter.OnItemClickListener) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rss_list, container, false);

        mRecyclerView = view.findViewById(R.id.homeRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(customCursorRecyclerViewAdapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        final RssListFragment thisInstance = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("RSS URL", "Entrez un nouveau URL");
                simpleDialogFragment.setTargetFragment(thisInstance, 0);
                simpleDialogFragment.show(fragmentTransaction, "SimpleDialogFragment");
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SELECTED_ITEMS_LIST, customCursorRecyclerViewAdapter.getSelectedItemsList());

        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
            int scrollPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            outState.putInt(SCROLL_POSITION, scrollPosition);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            customCursorRecyclerViewAdapter.setSelectedItemsList(savedInstanceState.<RSS>getParcelableArrayList(SELECTED_ITEMS_LIST));
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION, 0);
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case 0:
                Uri.Builder builder = new Uri.Builder();
                Uri uri = builder.scheme("content").authority(AccesDonnees.authority).appendPath("rss").build();
                return new CursorLoader(getActivity(), uri, new String[]{"title AS _id", "title", "description", "link", "path", "time"}, null, null, null);
            default:
                throw new IllegalArgumentException("no id handled");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        customCursorRecyclerViewAdapter.swapCursor(data);
        customCursorRecyclerViewAdapter.notifyDataSetChanged();

        mRecyclerView.getLayoutManager().scrollToPosition(scrollPosition);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        customCursorRecyclerViewAdapter.swapCursor(null);
        customCursorRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        broadcastReceiver = new MyBroadcastReceiver();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        customCursorRecyclerViewAdapter.getMyObsarvable().setChanged();
        customCursorRecyclerViewAdapter.getMyObsarvable().notifyObservers(customCursorRecyclerViewAdapter.getSelectedItemsList().size());
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.d(getTag(), "IllegalArgumentException: Receiver not registered");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void addRss(Uri uri) {
        DownloadHelper downloadHelper = new DownloadHelper(getActivity(), true);
        idList.add(downloadHelper.downloadFile(uri));
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (idList.contains(reference)) {
                idList.remove(reference);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(reference);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    String link = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                    try {
                        RSS rss = MyParser.getRss(localPath);
                        File file = new File(localPath.replace("file://", ""));
                        rss.setLink(link);
                        System.out.println("rss = " + rss);
                        AccesDonnees accesDonnees = new AccesDonnees(getActivity());
                        accesDonnees.addRSSFeed(rss);

                        scrollPosition = customCursorRecyclerViewAdapter.getItemCount();

                        loaderManager.restartLoader(0, null, thisInstance);

                        List<RssItem> rssItems = MyParser.getItems(rss);
                        accesDonnees.addRssItems(rssItems, rss);

                        Snackbar snackbar = Snackbar.make(getView(), "Flux ajouté avec succès", Snackbar.LENGTH_LONG);
                        snackbar.show();

                        file.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar snackbar = Snackbar.make(getView(), "Erreur lors de téléchegement, vérifiez bien le lien", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            }
        }
    }
}
