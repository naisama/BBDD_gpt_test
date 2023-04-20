package com.example.bbdd_gpt_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class PersonListActivity extends AppCompatActivity {

    private PersonDbHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private PersonAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        mDbHelper = new PersonDbHelper(this);
        mRecyclerView = findViewById(R.id.personRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPersonData();
    }

    private void loadPersonData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PersonContract.PersonEntry._ID,
                PersonContract.PersonEntry.COLUMN_NAME_NAME,
                PersonContract.PersonEntry.COLUMN_NAME_AGE,
                PersonContract.PersonEntry.COLUMN_NAME_HEIGHT
        };

        Cursor cursor = db.query(
                PersonContract.PersonEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        mAdapter = new PersonAdapter(this, cursor);
        mRecyclerView.setAdapter(mAdapter);
    }
}