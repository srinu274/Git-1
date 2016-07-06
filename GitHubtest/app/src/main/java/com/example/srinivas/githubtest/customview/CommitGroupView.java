package com.example.srinivas.githubtest.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.srinivas.githubtest.R;
import com.example.srinivas.githubtest.data.UserCommit;

import java.util.List;

/**
 * Created by Srinivas on 06-06-2016.
 */
public class CommitGroupView extends LinearLayout {

    private List<UserCommit> mCommits;

    public CommitGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(context.getResources().getConfiguration().getLayoutDirection()==LAYOUT_DIRECTION_RTL) {
            Log.i("rtl","is rtl");
            this.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            this.setGravity(Gravity.START);
        }
    }

    public void setCommits(Context context,List<UserCommit> commits) {
        removeAllViews();
        if(commits==null) return;
        if(commits.isEmpty()) return;
        mCommits=commits;
        String email=commits.get(0).getEmail();
        String name=commits.get(0).getName();
        addHeader(context,name,email);
        String date;
        String message;
        for(UserCommit commit:commits) {
            date=commit.getDate();
            message=commit.getMessage();
            addItem(context,date,message);
        }
        invalidate();
        requestLayout();
    }

    private void addHeader(Context context,String name,String email) {
        View view=LayoutInflater.from(context).inflate(R.layout.child_commit_group_header,this,false);
        TextView viewName=(TextView) view.findViewById(R.id.name);
        TextView viewEmail=(TextView) view.findViewById(R.id.email);
        viewName.setText(name);
        viewEmail.setText(email);
        addView(view,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
    }

    private void addItem(Context context,String date,String message) {
        View view=LayoutInflater.from(context).inflate(R.layout.child_commit_group_item,this,false);
        TextView viewMessage=(TextView) view.findViewById(R.id.message);
        TextView viewDate=(TextView) view.findViewById(R.id.date);
        String m=context.getResources().getString(R.string.message);
        String d=context.getResources().getString(R.string.date);
        viewMessage.setText(m+" : "+message);
        viewDate.setText(d+" : "+date);
        addView(view,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
    }
}
