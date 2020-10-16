package com.avinash.requestresource;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avinash.requestresource.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Requests> mValues;

    public MyItemRecyclerViewAdapter(List<Requests> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("Quantity: " + Integer.toString(mValues.get(position).getQuantity()));
        holder.mContentView.setText(mValues.get(position).getTitle());
        holder.mDescView.setText(mValues.get(position).getDescription());

        if(mValues.get(position).isCompleted())
            holder.mIsCompleted.setText("Done:Yes");
        else
            holder.mIsCompleted.setText("Done:No");
    }

    @Override
    public int getItemCount() {
        if( mValues == null)
            return 0;
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDescView;
        public final TextView mIsCompleted;
        public Requests mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDescView = (TextView) view.findViewById(R.id.item_description);
            mIsCompleted = (TextView) view.findViewById(R.id.item_completed);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}