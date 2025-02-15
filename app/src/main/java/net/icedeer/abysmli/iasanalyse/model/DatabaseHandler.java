package net.icedeer.abysmli.iasanalyse.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.icedeer.abysmli.iasanalyse.model.ComponentDataStruct;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PMS_APP";

    // Table name
    private static final String TABLE = "components";

    // components Table Columns names
    private static final String KEY_ID = "component_id";
    private static final String KEY_NAME = "component_name";
    private static final String KEY_SERIES = "series";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESC = "component_desc";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_componentS_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_SERIES + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_DESC + " TEXT" + ")";
        db.execSQL(CREATE_componentS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new component
    public void addComponent(ComponentDataStruct component) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, component.get_component_name());
        values.put(KEY_SERIES, component.get_series());
        values.put(KEY_TYPE, component.get_type());
        values.put(KEY_DESC, component.get_component_description());

        // Inserting Row
        db.insert(TABLE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single component
    public ComponentDataStruct getComponent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE, new String[]{KEY_ID,
                        KEY_NAME, KEY_SERIES, KEY_TYPE, KEY_DESC}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        // return component
        assert cursor != null;
        ComponentDataStruct componentDataStruct = new ComponentDataStruct(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
        db.close();
        return componentDataStruct;
    }

    // Getting All components
    public List<ComponentDataStruct> getAllComponents() {
        List<ComponentDataStruct> componentList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ComponentDataStruct component = new ComponentDataStruct();
                component.set_component_id(Integer.parseInt(cursor.getString(0)));
                component.set_component_name(cursor.getString(1));
                component.set_series(cursor.getString(2));
                component.set_type(cursor.getString(3));
                component.set_component_description(cursor.getString(4));
                // Adding component to list
                componentList.add(component);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return component list
        return componentList;
    }

    // Updating single component
    @SuppressWarnings("unused")
    public int updateComponent(ComponentDataStruct component) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, component.get_component_name());
        values.put(KEY_SERIES, component.get_series());
        values.put(KEY_TYPE, component.get_type());
        values.put(KEY_DESC, component.get_component_description());
        // updating row
        int result = db.update(TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(component.get_component_id())});
        db.close();
        return result;
    }

    // Deleting single component
    @SuppressWarnings("unused")
    public void deleteComponent(ComponentDataStruct component) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, KEY_ID + " = ?",
                new String[]{String.valueOf(component.get_component_id())});
        db.close();
    }

    // Deleting all components
    @SuppressWarnings("unused")
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }


    // Getting components Count
    public int getComponentsCount() {
        String countQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }

}