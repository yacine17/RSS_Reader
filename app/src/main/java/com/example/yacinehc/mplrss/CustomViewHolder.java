package com.example.yacinehc.mplrss;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yacinehc.mplrss.model.RSS;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = CustomCursorRecyclerViewAdapter.class.getSimpleName();
    public TextView title;
    public TextView description;
    public ImageView logo;
    private CheckedLinearLayout checkedLinearLayout;


    public CustomViewHolder(View itemView) {
        super(itemView);
        checkedLinearLayout = (CheckedLinearLayout) itemView;
        title = itemView.findViewById(R.id.rss_title);
        logo = itemView.findViewById(R.id.rss_logo);
        description = itemView.findViewById(R.id.description);
    }

    public void setData(Cursor cursor) {
        String titleValue = cursor.getString(cursor.getColumnIndex("title"));
        String descriptionValue = cursor.getString(cursor.getColumnIndex("description"));
        String linkValue = cursor.getString(cursor.getColumnIndex("_id"));

        title.setText(titleValue);
        description.setText(descriptionValue);
        logo.setImageResource(R.drawable.rss_icon);

        checkedLinearLayout.setRss(new RSS(linkValue, titleValue, descriptionValue));

    }
}
