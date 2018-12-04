package com.example.yacinehc.mplrss;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public static final String TAG = CursorRecyclerViewAdapter.class.getSimpleName();
    protected Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver dataSetObserver;


    public CursorRecyclerViewAdapter(Context mContext, Cursor mCursor) {
        Log.d(TAG, "constructeur");
        this.mContext = mContext;
        this.mCursor = mCursor;
        mDataValid = mCursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        dataSetObserver = new NotifyingDataSetObserver(this);
        if (mCursor != null) {
            mCursor.registerDataSetObserver(dataSetObserver);
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder");
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder");
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        Log.d(TAG, "swapCursor");
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        if (oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver);
        }

        mCursor = newCursor;
        if (mCursor != null) {
            if (dataSetObserver != null) {
                mCursor.registerDataSetObserver(dataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void setDataValid(boolean mDataValid) {
        this.mDataValid = mDataValid;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        private RecyclerView.Adapter adapter;

        public NotifyingDataSetObserver(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onChanged() {
            super.onChanged();
            ((CursorRecyclerViewAdapter) adapter).setDataValid(true);
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            ((CursorRecyclerViewAdapter) adapter).setDataValid(false);
        }
    }
}
