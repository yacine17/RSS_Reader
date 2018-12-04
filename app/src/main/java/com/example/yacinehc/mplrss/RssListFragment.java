package com.example.yacinehc.mplrss;


import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.example.yacinehc.mplrss.db.AccesDonnees;
import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.utils.SimpleDialogFragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;


public class RssListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String TAG = RssListFragment.class.getSimpleName();
    private List<Long> idList;
    private RecyclerView mRecyclerView;
    private CustomCursorRecyclerViewAdapter customCursorRecyclerViewAdapter;
    private DownloadManager downloadManager;
    private LoaderManager loaderManager;

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
                return new CursorLoader(getActivity(), uri, new String[]{"link AS _id", "title", "description"}, null, null, null);
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
                        String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        try {
                            RSS rss = MyParser.getRss(localPath);
                            AccesDonnees accesDonnees = new AccesDonnees(getActivity());
                            accesDonnees.addRSSFeed(rss);
                            loaderManager.restartLoader(0, null, thisInstance);
                            Snackbar snackbar = Snackbar.make(getView(), "Flux ajouté avec succès", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar snackbar = Snackbar.make(getView(), "Erreur lors de téléchegement, vérifiez bien le lien", Snackbar.LENGTH_SHORT);
                            snackbar.show();
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

        request.setDescription(uri.toString());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        final long id = downloadManager.enqueue(request);
        idList.add(id);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                Notification notification;
                NotificationManager notificationManager;

                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(id);

                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor.
                            getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    final int dl_progress = (bytes_downloaded / bytes_total) * 100;

                    Intent intent = new Intent();
                    final PendingIntent pendingIntent = PendingIntent.getActivity(
                            getContext().getApplicationContext(), 0, intent, 0);

                    notification = new Notification(R.drawable.rss_icon, "Téléchargement du flux",
                            System.currentTimeMillis());

                    notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
                    notification.contentView = new RemoteViews(getContext().getApplicationContext().getPackageName(),
                            R.layout.download_progress_bar);

                    notification.contentView.setProgressBar(R.id.progress_bar, 100, dl_progress, false);

                    getActivity().getApplicationContext();

                    notificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(
                            Context.NOTIFICATION_SERVICE);
                    notificationManager.notify((int) id, notification);
                }
            }
        }).start();


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
