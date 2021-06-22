package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class clock implements Serializable {

    public String Name;
    public String Day;
    public int hours;
    public int minutes;
    public int seconds;
    public boolean flag;
    public int difference;
    public String temp;
    int gmt;
   private transient Interface_ClockDAO dao = null;
    public clock(String s,int h, int m, int diff, String t , int sec , int gm) {
        Name = s;
        hours = h;
        minutes = m;
        Day = "Today";
        flag = false;
        difference=diff;
        temp=t;
        seconds=sec;
        gmt = gm;
    }
    public void setTime(int s , int m , int h)
    {
       seconds=s;
       minutes=m;
       hours=h;
    }

    public void set_dao(Interface_ClockDAO dao)
    {
        this.dao=dao;
    }



    public void save(){

        if (dao != null){

            Hashtable<String,String> data = new Hashtable<String, String>();

            data.put("Name",Name);
            data.put("hours", String.valueOf(hours));
            data.put("minutes", String.valueOf(minutes));
            data.put("day",Day);
            data.put("difference", String.valueOf(difference));
            data.put("temp_",temp);
            data.put("gmt", String.valueOf(gmt));
            dao.save(data);
        }
    }

    public void load(Hashtable<String, String> data){


        Name = data.get("name");
        hours = Integer.parseInt(data.get("hours"));
        minutes = Integer.parseInt(data.get("minutes"));
        Day = data.get("day");
        difference = Integer.parseInt(data.get("difference"));
        temp=data.get("temp_");
        gmt = Integer.parseInt(data.get("gmt"));
    }

    public static ArrayList<clock> load(Interface_ClockDAO dao){
        ArrayList<clock> clock_ = new ArrayList<clock>();
        if(dao != null){

            ArrayList<Hashtable<String,String>> objects = dao.load();
            for(Hashtable<String,String> obj : objects){
                clock c = new clock("",1,1,1,"",1,1);
                c.set_dao(dao);
                c.load(obj);
                clock_.add(c);
            }
        }
        return clock_;
    }












    public void save_2(){

        if (dao != null){

            Hashtable<String,String> data = new Hashtable<String, String>();

            data.put("Name",Name);
            data.put("gmt", String.valueOf(gmt));
            dao.save(data);
        }
    }

    public void load_2(Hashtable<String, String> data){


        Name = data.get("name");
        gmt = Integer.parseInt(data.get("gmt"));
    }

    public static ArrayList<clock> load_2(Interface_ClockDAO dao){
        ArrayList<clock> clock_ = new ArrayList<clock>();
        if(dao != null){

            ArrayList<Hashtable<String,String>> objects = dao.load();
            for(Hashtable<String,String> obj : objects){
                clock c = new clock("",1,1,1,"",1,1);
                c.set_dao(dao);
                c.load_2(obj);
                clock_.add(c);
            }
        }
        return clock_;
    }
}
