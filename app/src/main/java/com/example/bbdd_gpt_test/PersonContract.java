package com.example.bbdd_gpt_test;

import android.provider.BaseColumns;

public final class PersonContract {

    private PersonContract() {}

    public static class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "person";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_AGE = "age";
        public static final String COLUMN_NAME_HEIGHT = "height";
    }
}
