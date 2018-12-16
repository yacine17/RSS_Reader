package com.example.yacinehc.mplrss.rssItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yacinehc.mplrss.R;
import com.example.yacinehc.mplrss.model.RssItem;

import java.util.ArrayList;

public class RssItemsAdapter extends RecyclerView.Adapter<RssItemsAdapter.ViewHolder> {

    private ArrayList<RssItem> rssItems;
    private OnRssItemSelected onRssItemSelected;


    public RssItemsAdapter(ArrayList<RssItem> rssItems) {
        this.rssItems = rssItems;
    }

    @NonNull
    @Override
    public RssItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_recyclerview, viewGroup, false);
        view.setBackground(viewGroup.getContext().getDrawable(R.drawable.ripple));
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RssItemsAdapter.ViewHolder viewHolder, int i) {
        final RssItem rssItem = rssItems.get(i);

        viewHolder.title.setText(rssItem.getTitle());
        viewHolder.description
                .setText(rssItem.getDescription().length() > 40
                        ? rssItem.getDescription().substring(0, 40)
                        : rssItem.getDescription());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRssItemSelected != null) {
                    System.out.println("rssItem = " + rssItem.getLink());
                    onRssItemSelected.selectItem(rssItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rssItems.size();
    }

    public void setOnRssItemSelected(OnRssItemSelected onRssItemSelected) {
        this.onRssItemSelected = onRssItemSelected;
    }

    public interface OnRssItemSelected {
        void selectItem(RssItem rssItem);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView link;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.rssItemListTitle);
            description = view.findViewById(R.id.rssItemListDescription);
            link = view.findViewById(R.id.rssItemListLink);
        }
    }

}
