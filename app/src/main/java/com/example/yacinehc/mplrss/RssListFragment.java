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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.db.AccesDonnees;
import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.utils.SimpleDialogFragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;


public class RssListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private final static String TAG = RssListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private CustomCursorRecyclerViewAdapter customCursorRecyclerViewAdapter;
    private List<Long> idList;
    private DownloadManager downloadManager;
    private LoaderManager loaderManager;


    private Uri uri = new Uri.Builder().scheme("content").appendPath("rss").build();


    public RssListFragment() {
        idList = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customCursorRecyclerViewAdapter = new CustomCursorRecyclerViewAdapter(getContext(), null);
        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.restartLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rss_list, container, false);
        mRecyclerView = view.findViewById(R.id.homeRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(customCursorRecyclerViewAdapter);
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case 0:
                Uri.Builder builder = new Uri.Builder();
                Uri uri = builder.scheme("content").authority(AccesDonnees.authority).appendPath("rss").build();
                return new CursorLoader(getActivity(), uri, new String[]{"link AS _id", "title"}, null, null, null);
            default:
                throw new IllegalArgumentException("no id handled");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        customCursorRecyclerViewAdapter.swapCursor(data);
        customCursorRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        customCursorRecyclerViewAdapter.swapCursor(null);
        customCursorRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void initFragment() {
        final RssListFragment thisInstance = this;
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
                        String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Snackbar snackbar = Snackbar.make(getView(), "Téléchargement finie : " + fileTitle, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        try {
                            RSS rss = MyParser.getRss(localPath);
                            AccesDonnees accesDonnees = new AccesDonnees(getActivity());
                            accesDonnees.addRSSFeed(rss);
                            loaderManager.restartLoader(0, null, thisInstance);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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
