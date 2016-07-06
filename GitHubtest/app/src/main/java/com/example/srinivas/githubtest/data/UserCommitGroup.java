package com.example.srinivas.githubtest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Srinivas on 06-06-2016.
 */
public class UserCommitGroup {

    private List<UserCommit> commits;

    public UserCommitGroup() {
        commits=new ArrayList<>();
    }

    public UserCommitGroup(List<UserCommit> commits) {
        if(commits==null) {
            commits=new ArrayList<>();
        }
        this.commits=commits;
    }

    public void setCommits(List<UserCommit> commits) {
        this.commits=commits;
    }

    public List<UserCommit> getCommits() {
        return commits;
    }

    public void addCommit(UserCommit commit) {
        commits.add(commit);
    }

    public void removeCommit(UserCommit commit) {
        commits.remove(commit);
    }

    public static List<UserCommitGroup> getCommitGroups(List<UserCommit> commits) {
        if(commits==null) return null;
        List<UserCommitGroup> groups=new ArrayList<>();
        HashMap<String,UserCommitGroup> groupMap=new HashMap<>();
        UserCommitGroup commitGroup;
        for(UserCommit commit:commits) {
            commitGroup=groupMap.get(commit.getEmail());
            if(commitGroup==null) {
                commitGroup=new UserCommitGroup();
                groupMap.put(commit.getEmail(),commitGroup);
            }
            commitGroup.addCommit(commit);
        }
        groups.addAll(groupMap.values());
        return groups;
    }
}
