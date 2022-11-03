package com.example.bikefinderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;

public class BikeDatabaseController extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BikeRE";
    private static final String TABLE_BIKES = "BikeInfo";
    private static final String KEY_ID = "id";
    private static final String KEY_CHAISE_NUMBER = "chaiseNum";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_ACCURACY = "accuracy";


    public BikeDatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_BIKES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CHAISE_NUMBER + " TEXT,"
                + KEY_LAT + " TEXT," + KEY_LNG + " TEXT,"
                + KEY_ACCURACY + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKES);
        onCreate(db);
    }



    void addBike(Bike bike) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHAISE_NUMBER, bike.getChaiseNo());
        values.put(KEY_LAT, bike.getLatitude());
        values.put(KEY_LNG, bike.getLongitude());
        values.put(KEY_ACCURACY, bike.getAccuracy());

        db.insert(TABLE_BIKES, null, values);
        db.close();
    }

    void addOrUpdateBike(Bike bike) {
        SQLiteDatabase db = this.getWritableDatabase();

        Bike fetchBike=getBike(bike.getChaiseNo());
        if (fetchBike == null){
            ContentValues values = new ContentValues();
            values.put(KEY_CHAISE_NUMBER, bike.getChaiseNo());
            values.put(KEY_LAT, bike.getLatitude());
            values.put(KEY_LNG, bike.getLongitude());
            values.put(KEY_ACCURACY, bike.getAccuracy());
            db.insert(TABLE_BIKES, null, values);
        }else{
            bike.setChaiseNo(fetchBike.getChaiseNo());
            updateBike(bike);
        }

        db.close();
    }

    Bike getBike(String chaiseNo) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BIKES, new String[] {
                        KEY_CHAISE_NUMBER, KEY_LAT, KEY_LNG,KEY_ACCURACY }, KEY_CHAISE_NUMBER + "=?",
                new String[] { chaiseNo }, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount()>0) {
                cursor.moveToFirst();

                Bike bike = new Bike(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3));
                return bike;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }


    public List<Bike> getAllBikes() {
        List<Bike> contactList = new ArrayList<Bike>();
        String selectQuery = "SELECT  * FROM " + TABLE_BIKES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Bike contact = new Bike();
                contact.setChaiseNo(cursor.getString(1));
                contact.setLatitude(cursor.getString(2));
                contact.setLongitude(cursor.getString(3));
                contact.setAccuracy(cursor.getString(4));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }


    public int updateBike(Bike bike) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHAISE_NUMBER, bike.getChaiseNo());
        values.put(KEY_LAT, bike.getLatitude());
        values.put(KEY_LNG, bike.getLongitude());
        values.put(KEY_ACCURACY, bike.getAccuracy());

        return db.update(TABLE_BIKES, values, KEY_CHAISE_NUMBER + " = ?",
                new String[] { String.valueOf(bike.getChaiseNo()) });
    }

    public void deleteBike(Bike bike) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BIKES, KEY_CHAISE_NUMBER + " = ?",
                new String[] { bike.getChaiseNo() });
        db.close();
    }


    public int getBikesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_BIKES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

}
