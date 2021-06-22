package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class clock_list extends Activity {
    ArrayList<clock> clocks;
    ArrayList<clock> selected_clocks;
    ListView view;
    EditText text;
    Thread thread;
    ClockListAdapter adapter;
    Handler handler = new Handler();
    private boolean mActive;
    public boolean h=false;
    Interface_ClockDAO dao= new Clock_DB_DAO_2(this);
    private final Handler mHandler = new Handler();
    boolean db_check=true;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_list);

        DB_Helper dhelper = new DB_Helper(this);
        SQLiteDatabase db = dhelper.getReadableDatabase();

        String query = "SELECT * FROM clocks";
       Cursor c =db.rawQuery(query,null);
        if(c.getCount()==0)
        {
            c.close();
            db_check=false;

       }

        clocks = new ArrayList<clock>();

        if(db_check==false) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    load();
                    h = true;
                }
            });
            thread.start();
            while (h == false) {

            }
//            thread.stop();
            createView();
        }
        else
        {
            clocks = clock.load_2(dao);
            for( int i = 0 ; i < clocks.size() ; i++)
            {
                LocalTime t = LocalTime.now(ZoneId.of("GMT"));
                t=t.plusSeconds(clocks.get(i).gmt);
                int h = t.getHour();
                int m = t.getMinute();
                int s= t.getSecond();
                clocks.get(i).hours=h;
                clocks.get(i).minutes=m;
                clocks.get(i).seconds=s;
            }
            createView();
        }

        mActive = true;
       mHandler.post(mRunnable);
    }


    private void createView(){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);


        layout.addView(create_text());
        layout.addView(create_list());

        setContentView(layout);


    }
public EditText create_text()
{
    text = new EditText(this);
    text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    text.setHint("Search");
    text.addTextChangedListener(new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            adapter.getFilter().filter(text.toString());
        }
    });
    return text;
}
public ListView create_list()
{
    view = new ListView(this);
    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


    adapter = new ClockListAdapter(this,clocks);
    view.setAdapter(adapter);



    return view;
}


    private void showMessage(String message){
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        selected_clocks = new ArrayList<clock>();
        for(int i =0 ; i < clocks.size() ; i++)
        {
            if(clocks.get(i).flag==true) {
                clock clock_ = new clock(clocks.get(i).Name, clocks.get(i).hours, clocks.get(i).minutes,clocks.get(i).difference,clocks.get(i).temp,clocks.get(i).seconds, clocks.get(i).gmt);
                selected_clocks.add(clock_);

            }
        }

        intent.putExtra("list",selected_clocks);
        setResult(RESULT_OK, intent);

       // thread.stop();
        super.onBackPressed();

    }


    private final Runnable mRunnable = new Runnable() {
      @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            if (mActive) {
                for(clock c : clocks) {
                   c.setTime(getTime(c).getSecond(),getTime(c).getMinute(),getTime(c).getHour());
                    adapter.notifyDataSetChanged();
                }

                mHandler.postDelayed(mRunnable, 800);
           }
      }
   };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalTime getTime(clock c) {
        LocalTime t = LocalTime.now(ZoneId.of("GMT"));
        t = t.plusSeconds(c.gmt);
        return t;
    }






    @RequiresApi(api = Build.VERSION_CODES.O)
    private void load(){

        String line = "";
        TextView view = (TextView) findViewById(R.id.text);
        boolean a = isInternetConnected(this);

        try{

            URL url = new URL("https://api.timezonedb.com/v2.1/list-time-zone?key=UZBWLOPXFK1R&format=xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();
            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );


            while( (line = reader.readLine()) != null ){
                content.append(line);
            }

            line = content.toString();
            parse(line);
            line = "";
        } catch(Exception ex) {
            line = ex.getMessage();
            ex.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void parse(String xml){

       // showMessage("parseeeeee");
        String category = "";
        clocks = new ArrayList<clock>();

        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(xml));

            int event = parser.getEventType();
            while(event != XmlPullParser.END_DOCUMENT){

                if(event == XmlPullParser.START_TAG &&
                        parser.getName().equals("zones") ) {

                    category = parser.getAttributeValue(null,"name");
                }

                if(event == XmlPullParser.START_TAG &&
                        parser.getName().equals("zone") ){
                    parser.next();
                    parser.next();
                    String countryCode = parser.getText();
                    parser.next();
                    parser.next();
                    parser.next();
                    String country_name = parser.getText();
                    parser.next();
                    parser.next();
                    parser.next();
                    String zone_name = parser.getText();
                    parser.next();
                    parser.next();
                    parser.next();
                    String gmt = parser.getText();
                    parser.next();
                    parser.next();
                    parser.next();
                    int gmtt=Integer.parseInt(gmt);
                    LocalTime t_ = LocalTime.now(ZoneId.of("GMT"));
                    LocalTime t = t_.plusSeconds(gmtt);

                    String time_stamp = parser.getText();
                    int tt2 = Integer.parseInt(time_stamp);


                    int hr = t.getHour();
                    int min = t.getMinute();
                    int sec = t.getSecond();


                    String name = country_name + " "+ zone_name ;

                   //calculate difference
                    int difference = 0;
                    String temp=null;
                    LocalDate d = LocalDate.now(ZoneId.of("GMT"));
                    int date_ = d.getDayOfMonth();
                    int h = t.getHour();
                    int m = t.getMinute();
                    int s=t.getSecond();

                    LocalDate d2 = LocalDate.now();
                    LocalTime t2 = LocalTime.now();
                    int date_2 = d2.getDayOfMonth();
                    int h2 = t2.getHour();
                    int m2 = t2.getMinute();

                    if(date_2 > date_ )
                    {
                        difference = date_2 - date_;
                        temp = "hours behind";
                    }
                    if(date_ > date_2 )
                    {
                        difference = date_ - date_2;
                        temp = "hours ahead";
                    }
                    if(date_ == date_2)
                    {
                        if(h > h2)
                        {
                            difference = h - h2;
                            temp = "hours ahead";
                        }
                        if(h2 > h)
                        {
                            difference = h2 - h;
                            temp = "hours behind";
                        }
                        if(h2 == h)
                        {
                            if(m2 > m) {
                                difference = m2 - m;
                                temp = "minutes behind";
                            }
                            if(m > m2)
                            {
                                difference = m - m2;
                                temp = "minutes ahead";
                            }
                            if(m == m2)
                            {
                                difference = 0;
                                temp = "time difference";
                            }
                        }
                    }
                    clock c = new clock(name,hr,min,difference,temp,sec,gmtt);
                    c.set_dao(dao);
                    if(db_check==false)
                   {
                        c.save_2();
                    }

                    clocks.add(c);

                }

                event = parser.next();
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception ex){
           ex.printStackTrace();
        }
    }







    public static boolean isInternetConnected(Context mContext) {

        try {
            ConnectivityManager connect = null;
            connect = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connect != null) {
                NetworkInfo resultMobile = connect
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                NetworkInfo resultWifi = connect
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if ((resultMobile != null && resultMobile
                        .isConnectedOrConnecting())
                        || (resultWifi != null && resultWifi
                        .isConnectedOrConnecting())) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


}


