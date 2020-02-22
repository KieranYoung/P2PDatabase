package sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static android.widget.Toast.LENGTH_LONG;

public class SQL {

    private DbHelper dbHelper;
    private long android_id;
    private Context context;

    // Call constructor first always (expensive call)
    public SQL(long android_id, Context context) {
        this.android_id = android_id;
        this.context = context;
        dbHelper = new DbHelper(context);

        deleteOldFiles();
    }

    // Call this when you want to remove the database and be done with SQL
    public void onDestroy() {
        dbHelper.close();
    }

    private SQLiteDatabase getWriteDb() {
        return dbHelper.getWritableDatabase();
    }

    private SQLiteDatabase getReadDb() {
        return dbHelper.getReadableDatabase();
    }

    private String getDate() {
        // Input
        Date date = new Date(System.currentTimeMillis());

        // Conversion
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        return sdf.format(date);
    }

    private void errorToast(String errorMessage) {
        Toast.makeText(context, errorMessage, LENGTH_LONG).show();

    }

    private byte[] objectToByteArray(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        byte[] byteArray = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byteArray = bos.toByteArray();
        } catch (IOException e) {
            errorToast(e.toString());
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                errorToast(e.toString());
                e.printStackTrace();
            }
        }
        return byteArray;
    }

    private Object byteArrayToObject(byte[] byteArray) {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        ObjectInput in = null;
        Object object = null;
        try {
            in = new ObjectInputStream(bis);
            object = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            errorToast(e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                errorToast(e.toString());
                e.printStackTrace();
            }
        }
        return object;
    }

    // Call to insert file
    public boolean insertFile(Object file) {
        SQLiteDatabase db = getWriteDb();

        ContentValues values = new ContentValues();
        values.put(Schema.Entry.ANDROID_ID, android_id);
        values.put(Schema.Entry.DATE, getDate());
        values.put(Schema.Entry.FILE, objectToByteArray(file));

        long newRowId = db.insert(Schema.Entry.TABLE_NAME, null, values);

        return newRowId != -1;
    }

    private int deleteOldFiles() {
        SQLiteDatabase db = getWriteDb();

        String selection = Schema.Entry.DATE + " LIKE ?";
        String[] selectionArgs = { Schema.Entry.DATE + " <= date('now', '-1 day')" };
        int deleteRows = db.delete(Schema.Entry.TABLE_NAME, selection, selectionArgs);

        errorToast(deleteRows + " old files deleted");
        return deleteRows;
    }

    // Call to get files
    public ArrayList getFiles(long android_id) {
        SQLiteDatabase db = getReadDb();

        String[] projection = {
                BaseColumns._ID,
                Schema.Entry.ANDROID_ID,
                Schema.Entry.DATE,
                Schema.Entry.FILE
        };

        String selection = Schema.Entry.ANDROID_ID + " = ?";
        String[] selectionArgs = { Long.toString(android_id) };

        String sortOrder = Schema.Entry.DATE + " DESC";

        Cursor cursor = db.query(
                Schema.Entry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        ArrayList objects = new ArrayList<>();
        while (cursor.moveToNext()) {
            objects.add(byteArrayToObject(cursor.getBlob(cursor.getColumnIndexOrThrow(Schema.Entry.FILE))));
        }
        cursor.close();

        return objects;
    }

    static final String CREATE_TABLE = "CREATE TABLE " + Schema.Entry.TABLE_NAME + " ("
            + Schema.Entry.ANDROID_ID + " " + Schema.Entry.ANDROID_ID_TYPE + ", "
            + Schema.Entry.DATE + " " + Schema.Entry.DATE_TYPE + ", "
            + Schema.Entry.FILE + " " + Schema.Entry.FILE_TYPE + ", "
            + " PRIMARY KEY (" + Schema.Entry.ANDROID_ID + ", " + Schema.Entry.DATE + "));";

    static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + Schema.Entry.TABLE_NAME;
}
