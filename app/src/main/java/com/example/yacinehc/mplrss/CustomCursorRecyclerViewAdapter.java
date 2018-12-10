package com.example.yacinehc.mplrss;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacinehc.mplrss.db.AccesDonnees;
import com.example.yacinehc.mplrss.model.RSS;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CustomCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter implements Observer{
    public static final String TAG = CustomCursorRecyclerViewAdapter.class.getSimpleName();
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
        holder.itemView.setOnClickListener(myOnClickListener);
        holder.itemView.setOnLongClickListener(myOnLongClickListener);
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void removeChechecked() {

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
        Log.d(TAG, "myObsarvable = " + myObsarvable.countObservers());
        myObsarvable.setChanged();
        myObsarvable.notifyObservers(checkedItems.size());
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (checkedItems.size() == 0) {
                //TODO Afficher les détails du flux
            } else {
                toggleCheckItem(v);
            }
        }
    }

    private class MyOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            toggleCheckItem(v);
            return true;
        }
    }

    public MyObsarvable getMyObsarvable() {
        return myObsarvable;
    }


    public class MyObsarvable extends Observable {
        MyObsarvable() {
            super();
        }

        public void setChanged() {
            super.setChanged();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MainActivity.MyObservable) {
            if (arg instanceof String) {
                if (arg.equals("deleteAll")) {
                    AccesDonnees accesDonnees = new AccesDonnees(context);
                    accesDonnees.removeItems(checkedItems);
                    checkedItems.clear();
                    notifyDataSetChanged();
                    parent.getLoaderManager().restartLoader(0, null, parent);
                }
            }
        }
    }
}
