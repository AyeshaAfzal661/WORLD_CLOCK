package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Helper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Selected_clocks.db";
        public DB_Helper(Context context){ super(context,DATABASE_NAME,null,DATABASE_VERSION);}
        public void onCreate(SQLiteDatabase db){
            String sql = "CREATE TABLE Selected_clocks (Name TEXT, " +
                  "hours INTEGER," +
                   "minutes INTEGER," +
                    "day TEXT," +
                    "difference INTEGER," +
                    "temp_ TEXT,"+
                    "gmt INTEGER)";

            db.execSQL(sql);


            String sql1 = "CREATE TABLE clocks (Name TEXT, " +
                    "gmt INTEGER)";

            db.execSQL(sql1);



        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Selected_clocks");
            db.execSQL("DROP TABLE IF EXISTS clocks");
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db,oldVersion,newVersion);
        }
    }

