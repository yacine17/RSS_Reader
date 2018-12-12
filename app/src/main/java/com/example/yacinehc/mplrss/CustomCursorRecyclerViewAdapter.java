package com.example.yacinehc.mplrss;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.db.AccesDonnees;
import com.example.yacinehc.mplrss.model.RSS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CustomCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter implements Observer, Serializable {
    private List<RSS> checkedItems;
    private MyOnClickListener myOnClickListener;
    private MyOnLongClickListener myOnLongClickListener;
    private MyObsarvable myObsarvable;
    private Context context;
    private RssListFragment parent;

    public CustomCursorRecyclerViewAdapter(Context context, Cursor cursor, RssListFragment parent) {
        super(context, cursor);
        this.context = context;
        checkedItems = new ArrayList<>();
        myOnClickListener = new MyOnClickListener();
        myOnLongClickListener = new MyOnLongClickListener();
        myObsarvable = new MyObsarvable();
        this.parent = parent;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rss_item_recyclerview, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        CustomViewHolder holder = (CustomViewHolder) viewHolder;
        CheckedLinearLayout checkedLinearLayout = (CheckedLinearLayout) holder.itemView;
        holder.itemView.setOnClickListener(myOnClickListener);
        holder.itemView.setOnLongClickListener(myOnLongClickListener);

        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);

        if (!checkedItems.contains(checkedLinearLayout.getRss())) {
            checkedLinearLayout.setBackground(context.getDrawable(R.drawable.ripple));
        } else {
            checkedLinearLayout.setBackground(context.getDrawable(R.drawable.ripple_checked));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public ArrayList<RSS> getSelectedItemsList() {
        return (ArrayList<RSS>) checkedItems;
    }

    public void setSelectedItemsList(ArrayList<RSS> list) {
        checkedItems = list;
    }

    private void toggleCheckItem(View view) {
        CheckedLinearLayout checkedLinearLayout = (CheckedLinearLayout) view;
        checkedLinearLayout.toggle();

        if (checkedLinearLayout.isChecked()) {
            checkedItems.add(checkedLinearLayout.getRss());
            checkedLinearLayout.setBackground(view.getContext().getDrawable(R.drawable.ripple_checked));
        } else {
            checkedItems.remove(checkedLinearLayout.getRss());
            checkedLinearLayout.setBackground(view.getContext().getDrawable(R.drawable.ripple));
        }

        myObsarvable.setChanged();
        myObsarvable.notifyObservers(checkedItems.size());
    }

    public MyObsarvable getMyObsarvable() {
        return myObsarvable;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MainActivity.MyObservable) {
            if (arg instanceof String) {
                if (arg.equals("deleteAll")) {
                    AccesDonnees accesDonnees = new AccesDonnees(context);
                    accesDonnees.removeItems(checkedItems);

                    parent.getLoaderManager().restartLoader(0, null, parent);

                    checkedItems.clear();
                    myObsarvable.setChanged();
                    myObsarvable.notifyObservers(checkedItems.size());
                }
            }
        }
    }

    private class MyOnClickListener implements View.OnClickListener, Serializable {
        @Override
        public void onClick(View v) {
            if (checkedItems.size() == 0) {
                //TODO Afficher les détails du flux
            } else {
                toggleCheckItem(v);
            }
        }
    }

    private class MyOnLongClickListener implements View.OnLongClickListener, Serializable {
        @Override
        public boolean onLongClick(View v) {
            toggleCheckItem(v);
            return true;
        }
    }

    public class MyObsarvable extends Observable implements Serializable {
        public void setChanged() {
            super.setChanged();
        }
    }
}
