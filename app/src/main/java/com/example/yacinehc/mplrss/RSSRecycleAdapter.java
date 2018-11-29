package com.example.yacinehc.mplrss;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yacinehc.mplrss.model.RSS;

import java.util.ArrayList;
import java.util.List;

public class RSSRecycleAdapter extends RecyclerView.Adapter<RSSRecycleAdapter.ViewHolder> {
    private List<RSS> checkedItems;
    private List<RSS> all;
    private MyOnClickListener myOnClickListener;
    private MyLongClickListener myLongClickListener;


    public RSSRecycleAdapter(List<RSS> all) {
        this.all = all;
        this.checkedItems = new ArrayList<>();
        myOnClickListener = new MyOnClickListener();
        myLongClickListener = new MyLongClickListener();
    }

    public void removeChecked() {
        this.all.removeAll(checkedItems);
        checkedItems.clear();
        notifyDataSetChanged();
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (getCheckedCount() == 0) {
                //TODO afficher les détails du flux
            } else {
                toggleCheckItem(v);
            }
        }
    }

    private class MyLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            toggleCheckItem(v);
            return true;
        }
    }

    private void toggleCheckItem(View v) {
        CheckedLinearLayout checkedRSSView = (CheckedLinearLayout) v;
        checkedRSSView.toggle();

        if (checkedRSSView.isChecked()) {
            checkedItems.add(checkedRSSView.getRss());
            checkedRSSView.setBackground(v.getContext().getDrawable(R.drawable.ripple_checked));
        } else {
            checkedItems.remove(checkedRSSView.getRss());
            checkedRSSView.setBackground(v.getContext().getDrawable(R.drawable.ripple));
        }
    }

    public RSSRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rss_item_recyclerview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        RSS rss = all.get(i);

        viewHolder.titre.setText(rss.getTitre());
        viewHolder.logo.setImageResource(android.R.drawable.ic_menu_send);
        viewHolder.itemView.setOnLongClickListener(myLongClickListener);
        viewHolder.itemView.setOnClickListener(myOnClickListener);
    }

    @Override
    public int getItemCount() {
        return this.all.size();
    }

    public int getCheckedCount() {
        return checkedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView logo;
        public TextView titre;

        public ViewHolder(View view) {
            super(view);
            logo = view.findViewById(R.id.rss_logo);
            titre = view.findViewById(R.id.rss_title);
        }
    }
}
