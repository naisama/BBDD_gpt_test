package com.example.bbdd_gpt_test;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public PersonAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.person_list_item, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        int nameColumnIndex = mCursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME);
        int ageColumnIndex = mCursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_AGE);
        int heightColumnIndex = mCursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_HEIGHT);

        if (nameColumnIndex >= 0 && ageColumnIndex >= 0 && heightColumnIndex >= 0) {
            String name = mCursor.getString(nameColumnIndex);
            int age = mCursor.getInt(ageColumnIndex);
            float height = mCursor.getFloat(heightColumnIndex);

            holder.nameTextView.setText(name);
            holder.ageTextView.setText(String.valueOf(age));
            holder.heightTextView.setText(String.format("%.2f", height));
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView ageTextView;
        TextView heightTextView;

        public PersonViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            heightTextView = itemView.findViewById(R.id.heightTextView);
        }
    }
}