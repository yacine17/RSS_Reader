package com.example.yacinehc.mplrss;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = CustomCursorRecyclerViewAdapter.class.getSimpleName();
    public TextView title;
    public ImageView logo;

    public CustomViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "constructeur");
        title = itemView.findViewById(R.id.rss_title);
        logo = itemView.findViewById(R.id.rss_logo);
    }

    public void setData(Cursor cursor) {
        Log.d(TAG, "setData");
        title.setText(cursor.getString(cursor.getColumnIndex("title")));
        logo.setImageResource(R.drawable.rss_icon);
    }
}
