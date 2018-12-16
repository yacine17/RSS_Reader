package com.example.yacinehc.mplrss.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.RemoteViews;

import com.example.yacinehc.mplrss.R;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadHelper {
    private Activity activity;
    private boolean notif;
    private long id;
    private DownloadManager downloadManager;

    public DownloadHelper(Activity activity, boolean notif) {
        this.notif = notif;
        this.activity = activity;
    }


    public long downloadFile(Uri uri) {
        this.downloadManager = (DownloadManager) this.activity.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(this.activity, Environment.DIRECTORY_DOWNLOADS, "mplrss");

        request.setDescription(uri.toString());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        id = downloadManager.enqueue(request);

        if (this.notif) {
            new NotificationThread().run();
        }
        return id;
    }


    class NotificationThread implements Runnable {
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
                        activity.getApplicationContext(), 0, intent, 0);

                notification = new Notification(R.drawable.rss_icon, "Téléchargement du flux",
                        System.currentTimeMillis());

                notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
                notification.contentView = new RemoteViews(activity.getApplicationContext().getPackageName(),
                        R.layout.download_progress_bar);

                notification.contentView.setProgressBar(R.id.progress_bar, 100, dl_progress, false);

                activity.getApplicationContext();

                notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(
                        Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) id, notification);
            }
        }
    }
}

