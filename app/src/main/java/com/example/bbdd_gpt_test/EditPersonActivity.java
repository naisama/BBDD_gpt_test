package com.example.bbdd_gpt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPersonActivity extends AppCompatActivity {

    private EditText mEditNameEditText;
    private EditText mEditAgeEditText;
    private EditText mEditHeightEditText;
    private Button mUpdateButton;
    private PersonDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);

        mEditNameEditText = findViewById(R.id.editNameEditText);
        mEditAgeEditText = findViewById(R.id.editAgeEditText);
        mEditHeightEditText = findViewById(R.id.editHeightEditText);
        mUpdateButton = findViewById(R.id.updateButton);
        mDbHelper = new PersonDbHelper(this);

        String selectedName = getIntent().getStringExtra("selectedName");
        loadPersonData(selectedName);

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePersonData(selectedName);
            }
        });
    }

    private void loadPersonData(String name) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PersonContract.PersonEntry.COLUMN_NAME_NAME,
                PersonContract.PersonEntry.COLUMN_NAME_AGE,
                PersonContract.PersonEntry.COLUMN_NAME_HEIGHT
        };

        String selection = PersonContract.PersonEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {name};

        Cursor cursor = db.query(
                PersonContract.PersonEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int columnIndexName = cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME);
            int columnIndexAge = cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_AGE);
            int columnIndexHeight = cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_HEIGHT);
            if (columnIndexName >= 0 && columnIndexAge >= 0 && columnIndexHeight >= 0) {
                mEditNameEditText.setText(cursor.getString(columnIndexName));
                mEditAgeEditText.setText(String.valueOf(cursor.getInt(columnIndexAge)));
                mEditHeightEditText.setText(String.valueOf(cursor.getFloat(columnIndexHeight)));
            }
        }
        cursor.close();
    }

    private void updatePersonData(String oldName) {
        String newName = mEditNameEditText.getText().toString().trim();
        String newAgeString = mEditAgeEditText.getText().toString().trim();
        String newHeightString = mEditHeightEditText.getText().toString().trim();

        if (newName.isEmpty() || newAgeString.isEmpty() || newHeightString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PersonContract.PersonEntry.COLUMN_NAME_NAME, newName);
        values.put(PersonContract.PersonEntry.COLUMN_NAME_AGE, Integer.parseInt(newAgeString));
        values.put(PersonContract.PersonEntry.COLUMN_NAME_HEIGHT, Float.parseFloat(newHeightString));

        String selection = PersonContract.PersonEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {oldName};

        int updatedRows = db.update(PersonContract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);

        if (updatedRows > 0) {
            Toast.makeText(this, "Person updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating person", Toast.LENGTH_SHORT).show();
        }
    }
}

