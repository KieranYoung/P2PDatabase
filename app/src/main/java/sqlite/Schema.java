package sqlite;

import android.provider.BaseColumns;

final class Schema {
    private Schema() {}

    static class Entry implements BaseColumns {
        static final String TABLE_NAME = "message_table";
        static final String ANDROID_ID = "id";
        static final String ANDROID_ID_TYPE = "INTEGER NOT NULL";
        static final String DATE = "date";
        static final String DATE_TYPE = "TEXT NOT NULL";
        static final String FILE = "file";
        static final String FILE_TYPE = "BLOB NOT NULL";
    }
}
