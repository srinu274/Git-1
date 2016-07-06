package com.example.srinivas.githubtest.helper;

import android.content.Context;
import android.util.Log;

import com.example.srinivas.githubtest.data.UserCommit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Srinivas on 06-06-2016.
 */
public class GitHubJSONParser {

    public static List<UserCommit> toCommitList(Context context,String jsonArray) {
        try {
            List<UserCommit> commits = new ArrayList<>();
            JSONArray array = new JSONArray(jsonArray);
            JSONObject object;
            UserCommit commit;
            for (int i = 0; i < array.length(); i++) {
                object = array.getJSONObject(i);
                commit=toUserCommit(context,object);
                if(commit!=null) commits.add(commit);
            }
            return commits;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserCommit toUserCommit(Context context,JSONObject jsonObject) {
        try {
            JSONObject author=jsonObject.getJSONObject("commit").getJSONObject("author");
            Log.i("author",author.toString());
            String name=author.getString("name");
            String email=author.getString("email");
            String date=author.getString("date");
            String message=jsonObject.getJSONObject("commit").getString("message");
            String url=jsonObject.getJSONObject("author").getString("avatar_url");
            Log.i("url",""+url);
            return new UserCommit(context,name,email,message,date,url);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getImageBytes(String serverurl) {
        HttpURLConnection connection = null;
        BufferedInputStream input = null;
        FileOutputStream stream=null;
        try {
            Log.i("Network", "send Loc");
            URL url = new URL(serverurl + "service/getimage");
            connection = (HttpURLConnection) url.openConnection();
            OutputStream output = null;
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            Log.i("response code", connection.getResponseCode() + "");
            //connection.setDoInput(true);
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                input = new BufferedInputStream(connection.getInputStream());
                Log.i("response code", connection.getResponseCode() + "");
                Log.i("String", (input != null) ? input + " is there" : "input null");
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                byte[] bytes=new byte[1024];
                int byteCount=0;
                while ((byteCount = input.read(bytes)) != -1) {
                    outputStream.write(bytes,0,byteCount);
                }
                Log.i("Val", "accepted");
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {

                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {

                }
            }
        }
        Log.i("Val", "failed");
        return null;
    }

}
