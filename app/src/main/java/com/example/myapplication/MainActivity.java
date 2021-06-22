package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class MainActivity extends Activity {


    private boolean mActive;

    private final Handler mHandler = new Handler();


    private static final int REQUEST_CODE = 3 ;

    ArrayList<clock> selected_clocks;
    ArrayList<clock> hahah;
    MainActivityAdapter adapter;

    Interface_ClockDAO dao;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }
        else
            {
            setContentView(R.layout.activity_main_l);
        }

        selected_clocks = new ArrayList<clock>();
      //  mHandler = new Handler();
        ListView view = (ListView)findViewById(R.id.list);
        adapter = new MainActivityAdapter(this,selected_clocks);
        view.setAdapter(adapter);
        registerForContextMenu(view);
        dao = new Clock_DB_DAO(this);
        mActive = true;
        mHandler.post(mRunnable);



    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }
        else
        {
            setContentView(R.layout.activity_main_l);
        }
       ListView view = (ListView)findViewById(R.id.list);
        registerForContextMenu(view);
        adapter = new MainActivityAdapter(this,selected_clocks);
        view.setAdapter(adapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart(){
        super.onStart();
        showMessage("Start");


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume(){
        super.onResume();
        selected_clocks = clock.load(dao);
       for( int i = 0 ; i < selected_clocks.size() ; i++)
        {
      LocalTime t = LocalTime.now(ZoneId.of("GMT"));
      t=t.plusSeconds(selected_clocks.get(i).gmt);
            int h = t.getHour();
           int m = t.getMinute();
           int s= t.getSecond();
            selected_clocks.get(i).hours=h;
           selected_clocks.get(i).minutes=m;
           selected_clocks.get(i).seconds=s;
        }
         ListView view = (ListView)findViewById(R.id.list);
        registerForContextMenu(view);
         adapter = new MainActivityAdapter(this,selected_clocks);
        view.setAdapter(adapter);

        //adapter.notifyDataSetChanged();
        showMessage("load");
        showMessage("Resume");


    }

    public void onPause(){
        super.onPause();
        showMessage("Pause");

    }

    public void onStop(){
        super.onStop();
        showMessage("Stop");
    }
    public void onDestroy(){
        super.onDestroy();
        showMessage("Destroy");
    }
    private void showMessage(String message){
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }
    public boolean contains_zone(String n)
    {
        for(int j =0;j < selected_clocks.size() ; j++) {
            if (selected_clocks.get(j).Name.equals(n)) {
                return true;
            }
        }
          return false;
    }


       public void buttonClick(View v){

        if(v.getId() == R.id.floatingActionButton3) {
            next_screen();
        }

        }

    private void next_screen() {
        Intent intent = new Intent(this,clock_list.class);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
            if(resultCode == RESULT_OK) {
                hahah = new ArrayList<clock>();
                hahah = (ArrayList<clock>) data.getSerializableExtra("list");
                for( int i = 0 ; i < hahah.size() ; i++)
                {
                    if((contains_zone(hahah.get(i).Name))==false) {
                        hahah.get(i).set_dao(dao);
                        hahah.get(i).save();
                        selected_clocks.add(hahah.get(i));
                    }
                }

               adapter.notifyDataSetChanged();


                showMessage("saved");
            }

    }




    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }

    private void deleteClock(int position){
        String n = selected_clocks.get(position).Name;
        selected_clocks.remove(position);
        adapter.notifyDataSetChanged();
        DB_Helper dbHelper = new DB_Helper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sq = "DELETE FROM " + "Selected_clocks" + " WHERE "+"Name"+"='"+n+"'";
        db.execSQL(sq);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.delete_item_menu:
                deleteClock(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private final Runnable mRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            if (mActive) {
                for(clock c :selected_clocks) {
                   c.setTime(getTime(c).getSecond(),getTime(c).getMinute(),getTime(c).getHour());
                    adapter.notifyDataSetChanged();
                }

                mHandler.postDelayed(mRunnable, 1000);
          }
       }
   };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalTime getTime(clock c) {
        LocalTime t = LocalTime.now(ZoneId.of("GMT"));
        t=t.plusSeconds(c.gmt);
        return t;
    }


}