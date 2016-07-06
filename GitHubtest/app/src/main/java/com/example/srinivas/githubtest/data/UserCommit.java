package com.example.srinivas.githubtest.data;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Created by Srinivas on 06-06-2016.
 */
public class UserCommit {

    private String name;
    private String email;
    private String message;
    private String date;
    private String url;

    public UserCommit() {

    }

    public UserCommit(Context context,String name,String email,String message,String date,String url) {
        this.name=name;
        this.email=email;
        this.message=message;
        this.url=url;
        try {
            parseDate(context,date);
        } catch (Exception e) {
            Log.i("date","ex");
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public void setMessage(String message) {
        this.message=message;
    }

    public void parseDate(Context context, String date) throws Exception {
        date=date.replace('T',' ');
        date=date.replace('Z',' ');
        Log.i("date",date);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date sDate=formatter.parse(date);
        Log.i("date s",sDate==null?"sdate is null":sDate.toString());
        //SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss",Locale.getDefault());
        date=DateFormat.format("yyyy.MM.dd 'at' HH:mm:ss",sDate.getTime()+TimeZone.getDefault().getRawOffset()).toString();
        Log.i("date d",date.toString());
        setDate(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {return url;}
}
