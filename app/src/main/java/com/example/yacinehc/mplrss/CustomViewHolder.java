package com.example.yacinehc.mplrss;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = CustomCursorRecyclerViewAdapter.class.getSimpleName();
    public TextView title;
    public TextView description;
    public ImageView logo;

    public CustomViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.rss_title);
        logo = itemView.findViewById(R.id.rss_logo);
        description = itemView.findViewById(R.id.description);
    }

    public void setData(Cursor cursor) {
        title.setText(cursor.getString(cursor.getColumnIndex("title")));
        description.setText(cursor.getString(cursor.getColumnIndex("description")));
        logo.setImageResource(R.drawable.rss_icon);
    }
}
