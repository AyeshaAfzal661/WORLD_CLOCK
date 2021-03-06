package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Clock_DB_DAO_2 implements Interface_ClockDAO {
    private Context context;

    public Clock_DB_DAO_2(Context ctx){
        context = ctx;
    }

    @Override
    public void save(Hashtable<String,String> attributes)
    {
        DB_Helper dbHelper = new DB_Helper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        Enumeration<String> keys = attributes.keys();
        while (keys.hasMoreElements()){
            String key = keys.nextElement();
            content.put(key,attributes.get(key));
        }

        db.insert("clocks",null,content);
    }

    @Override
    public void save(ArrayList<Hashtable<String,String>> objects)
    {
        for(Hashtable<String,String> obj : objects){
            save(obj);
        }
    }

    @Override
    public ArrayList<Hashtable<String,String>> load()
    {
        DB_Helper dbHelper = new DB_Helper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM clocks";
        Cursor cursor = db.rawQuery(query,null);

        ArrayList<Hashtable<String,String>> objects = new ArrayList<Hashtable<String, String>>();
        while(cursor.moveToNext()){
            Hashtable<String,String> obj = new Hashtable<String, String>();
            String [] columns = cursor.getColumnNames();
            for(String col : columns){
                obj.put(col.toLowerCase(),cursor.getString(cursor.getColumnIndex(col)));
            }
            objects.add(obj);
        }

        return objects;
    }

    @Override
    public Hashtable<String,String> load(String id)
    {
        return null;
    }

}
