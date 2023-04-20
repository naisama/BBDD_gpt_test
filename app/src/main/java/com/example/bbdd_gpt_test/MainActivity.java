package com.example.bbdd_gpt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.text.similarity.JaroWinklerDistance;

public class MainActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mAgeEditText;
    private EditText mHeightEditText;
    private Button mSaveButton;
    private PersonDbHelper mDbHelper;
    private Button mViewButton;
    private Spinner mPersonSpinner;
    private Button mEditButton;
    private Button mDeleteButton;
    private ArrayAdapter<String> mSpinnerAdapter;
    private EditText mSearchNameEditText;
    private Button mSearchButton;
    private ListView mSearchResultsListView;
    private SimpleCursorAdapter mSearchResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameEditText = findViewById(R.id.nameEditText);
        mAgeEditText = findViewById(R.id.ageEditText);
        mHeightEditText = findViewById(R.id.heightEditText);
        mSaveButton = findViewById(R.id.saveButton);
        mDbHelper = new PersonDbHelper(this);
        mViewButton = findViewById(R.id.viewButton);
        mPersonSpinner = findViewById(R.id.personSpinner);
        mEditButton = findViewById(R.id.editButton);
        mDeleteButton = findViewById(R.id.deleteButton);
        mSearchNameEditText = findViewById(R.id.searchNameEditText);
        mSearchButton = findViewById(R.id.searchButton);
        mSearchResultsListView = findViewById(R.id.searchResultsListView);


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonData();
            }
        });

        mViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonListActivity.class);
                startActivity(intent);
            }
        });

        loadPersonNames();

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedName = (String) mPersonSpinner.getSelectedItem();
                if (selectedName != null) {
                    Intent intent = new Intent(MainActivity.this, EditPersonActivity.class);
                    intent.putExtra("selectedName", selectedName);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No person selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedName = (String) mPersonSpinner.getSelectedItem();
                if (selectedName != null) {
                    deletePerson(selectedName);
                } else {
                    Toast.makeText(MainActivity.this, "No person selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = mSearchNameEditText.getText().toString().trim();
                searchPerson(query);
            }
        });

        String[] fromColumns = {PersonContract.PersonEntry.COLUMN_NAME_NAME};
        int[] toViews = {android.R.id.text1};
        mSearchResultsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, fromColumns, toViews, 0);
        mSearchResultsListView.setAdapter(mSearchResultsAdapter);
    }

    private void savePersonData() {
        String name = mNameEditText.getText().toString().trim();
        String ageString = mAgeEditText.getText().toString().trim();
        String heightString = mHeightEditText.getText().toString().trim();

        if (name.isEmpty() || ageString.isEmpty() || heightString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNameAlreadyInDatabase(name)) {
            Toast.makeText(this, "Name already exists in the database", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageString);
        float height = Float.parseFloat(heightString);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PersonContract.PersonEntry.COLUMN_NAME_NAME, name);
        values.put(PersonContract.PersonEntry.COLUMN_NAME_AGE, age);
        values.put(PersonContract.PersonEntry.COLUMN_NAME_HEIGHT, height);

        long newRowId = db.insert(PersonContract.PersonEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNameAlreadyInDatabase(String nameToCheck) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PersonContract.PersonEntry._ID,
                PersonContract.PersonEntry.COLUMN_NAME_NAME
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

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME);
            if (nameColumnIndex >= 0) {
                do {
                    String name = cursor.getString(nameColumnIndex);
                    if (nameToCheck.equalsIgnoreCase(name)) {
                        cursor.close();
                        return true;
                    }
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return false;
    }

    private void loadPersonNames() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PersonContract.PersonEntry.COLUMN_NAME_NAME
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

        List<String> names = new ArrayList<>();
        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME);
            if (columnIndex >= 0)
                names.add(cursor.getString(columnIndex));
        }
        cursor.close();

        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPersonSpinner.setAdapter(mSpinnerAdapter);
    }

    private void deletePerson(String name) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = PersonContract.PersonEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {name};

        int deletedRows = db.delete(PersonContract.PersonEntry.TABLE_NAME, selection, selectionArgs);

        if (deletedRows > 0) {
            Toast.makeText(this, "Person deleted successfully", Toast.LENGTH_SHORT).show();
            loadPersonNames();
        } else {
            Toast.makeText(this, "Error deleting person", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchPerson(String query) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PersonContract.PersonEntry._ID,
                PersonContract.PersonEntry.COLUMN_NAME_NAME
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

        // A TreeMap will sort the entries based on their keys (in this case, the similarity scores)
        //TreeMap<Integer, String> sortedResults = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Double, String> sortedResults = new TreeMap<>(Comparator.reverseOrder());

        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME);
            if (columnIndex >= 0) {
                String name = cursor.getString(columnIndex);
                double similarity = calculateNameSimilarity(query, name);
                sortedResults.put(similarity, name);
            }
        }
        cursor.close();

        // Keep only the three most similar names
        if (sortedResults.size() > 3) {
            sortedResults.entrySet().removeIf(entry -> sortedResults.headMap(entry.getKey()).size() >= 3);
        }

        List<String> similarNames = new ArrayList<>(sortedResults.values());
        String selection = PersonContract.PersonEntry.COLUMN_NAME_NAME + " IN (?, ?, ?)";
        String[] selectionArgs = {"", "", ""};
        for (int i = 0; i < similarNames.size(); i++) {
            selectionArgs[i] = similarNames.get(i);
        }

        Cursor searchCursor = db.query(
                PersonContract.PersonEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        mSearchResultsAdapter.changeCursor(searchCursor);

        mSearchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (searchCursor.moveToPosition(position)) {
                    int columnIndex = searchCursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME);
                    if (columnIndex >= 0) {
                        String selectedName = searchCursor.getString(columnIndex);
                        Intent intent = new Intent(MainActivity.this, EditPersonActivity.class);
                        intent.putExtra("selectedName", selectedName);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    // Implement a simple similarity score function based on the number of equal characters
    /*private int calculateNameSimilarity(String query, String name) {
        int similarity = 0;
        query = query.toLowerCase();
        name = name.toLowerCase();

        for (int i = 0; i < Math.min(query.length(), name.length()); i++) {
            if (query.charAt(i) == name.charAt(i)) {
                similarity++;
            }
        }
        return similarity;
    }*/

    private double calculateNameSimilarity(String query, String name) {
        query = query.toLowerCase();
        name = name.toLowerCase();

        JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
        return jaroWinklerDistance.apply(query, name);
    }



}