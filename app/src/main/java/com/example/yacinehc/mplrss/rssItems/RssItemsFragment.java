package com.example.yacinehc.mplrss.rssItems;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.db.AccesDonnees;
import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.model.RssItem;
import com.example.yacinehc.mplrss.utils.DownloadHelper;
import com.example.yacinehc.mplrss.utils.MyParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

public class RssItemsFragment extends Fragment {
    private static final String RSS_ITEMS = "rssItems";
    private static final String RSS_FEED = "rssFeed";
    private static final String SCROLL_POSITION = "scrollPosition";

    private ArrayList<RssItem> mRssItems;
    private RSS rssFeed;
    private RssItemsAdapter rssItemsAdapter;
    private long id;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv;
    private int scrollPosition = 0;

    public RssItemsFragment() {
        // Required empty public constructor
    }


    public static RssItemsFragment newInstance(ArrayList<RssItem> rssItems, RSS rssFeed) {
        RssItemsFragment fragment = new RssItemsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RSS_ITEMS, rssItems);
        args.putParcelable(RSS_FEED, rssFeed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRssItems = getArguments().getParcelableArrayList(RSS_ITEMS);
            rssFeed = getArguments().getParcelable(RSS_FEED);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION, ((LinearLayoutManager) rv.getLayoutManager()).findFirstVisibleItemPosition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        swipeRefreshLayout = new SwipeRefreshLayout(getContext());
        rv = new RecyclerView(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rssItemsAdapter = new RssItemsAdapter(mRssItems);
        rv.setAdapter(rssItemsAdapter);
        rv.setVerticalScrollBarEnabled(true);
        rv.setScrollbarFadingEnabled(true);

        swipeRefreshLayout.addView(rv);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DownloadHelper downloadHelper = new DownloadHelper(getActivity(), false);
                id = downloadHelper.downloadFile(Uri.parse(rssFeed.getLink()));

                getActivity().registerReceiver(new MyBroadcastReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });

        if (savedInstanceState != null) {

            rv.scrollToPosition(savedInstanceState.getInt(SCROLL_POSITION, 0));
        }

        return swipeRefreshLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        rssItemsAdapter.setOnRssItemSelected((RssItemsAdapter.OnRssItemSelected) getActivity());
    }

    public void setmRssItems(ArrayList<RssItem> mRssItems) {
        this.mRssItems = mRssItems;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity() != null) {
                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == reference) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        String link = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                        try {
                            RSS rss = MyParser.getRss(localPath);
                            rss.setLink(link);
                            File file = new File(localPath.replace("file://", ""));
                            System.out.println("rss = " + rss);

                            AccesDonnees accesDonnees = new AccesDonnees(getActivity());

                            final List<RssItem> rssItems = MyParser.getItems(rss);
                            accesDonnees.addRssItems(rssItems, rss);

                            RssItemsFragment.this.setmRssItems((ArrayList<RssItem>) accesDonnees.getRssItems(rss));
                            RssItemsFragment.this.rssItemsAdapter.notifyDataSetChanged();
                            if (RssItemsFragment.this.swipeRefreshLayout.isRefreshing()) {
                                RssItemsFragment.this.swipeRefreshLayout.setRefreshing(false);
                            }
                            System.out.println("SwipeRefreshLayout");

                            file.delete();

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        if (RssItemsFragment.this.swipeRefreshLayout.isRefreshing()) {
                            RssItemsFragment.this.swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            }
        }
    }
}
