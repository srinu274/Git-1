package com.example.srinivas.githubtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.srinivas.githubtest.customview.CommitGroupView;
import com.example.srinivas.githubtest.data.UserCommit;
import com.example.srinivas.githubtest.data.UserCommitGroup;
import com.example.srinivas.githubtest.helper.GitHubJSONParser;
import com.example.srinivas.githubtest.helper.ThumbnailDownloader;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ThumbnailDownloader<ImageView> mThumbnailThread;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(new CommitListAdapter());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        new CommitGetTask().execute();
        mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(final ImageView imageView, final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.clearQueue();
        mThumbnailThread.quit();
    }

    private class CommitListAdapter extends RecyclerView.Adapter<CommitListHolder> {

        private List<UserCommit> list;

        @Override
        public CommitListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_commits, parent, false);
            return new CommitListHolder(v);
        }

        @Override
        public void onBindViewHolder(CommitListHolder holder, int position) {
            holder.setView(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        public void setList(List<UserCommit> commits) {
            this.list = commits;
            notifyDataSetChanged();
        }

        public List<UserCommit> getList() {
            return list;
        }
    }


    private class CommitListHolder extends RecyclerView.ViewHolder {

        TextView name, email, date, message;
        ImageView url;

        public CommitListHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            url = (ImageView) itemView.findViewById(R.id.imageView);
        }

        public void setView(UserCommit commit) {
            name.setText(commit.getName());
            email.setText(commit.getEmail());
            date.setText(commit.getDate());
            message.setText(commit.getMessage());
            mThumbnailThread.queueThumbnail(url, commit.getUrl());
        }
    }


    public List<UserCommit> getCommits() {
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://api.github.com/repos/rails/rails/commits" + "?since" + "2016-04-01T00:00:00Z");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
                int ch = -1;
                StringBuilder builder = new StringBuilder();
                while ((ch = stream.read()) != -1) {
                    builder.append((char) ch);
                }
                return GitHubJSONParser.toCommitList(this, builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private class CommitGetTask extends AsyncTask<Void, Void, List<UserCommit>> {

        @Override
        protected List<UserCommit> doInBackground(Void... params) {
            List<UserCommit> commits = getCommits();
            return commits;
        }

        @Override
        protected void onPostExecute(List<UserCommit> result) {
            Log.i("commits", result + "");
            ((CommitListAdapter) mRecyclerView.getAdapter()).setList(result);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_search));
            ((CommitListAdapter) mRecyclerView.getAdapter()).setList(null);
            new CommitGetTask().execute();
            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    doSearch(v.getText().toString());
                    return true;
                }
            });

            edtSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    doSearch(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

            isSearchOpened = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    private synchronized void doSearch(String text) {
        if (TextUtils.isEmpty(text)) ((CommitListAdapter) mRecyclerView.getAdapter()).setList(null);
        List<UserCommit> commits = ((CommitListAdapter) mRecyclerView.getAdapter()).getList();
        if (commits==null) {
            ((CommitListAdapter) mRecyclerView.getAdapter()).setList(null);
            return;
        }
        List<UserCommit> newCommits = new ArrayList<>();
        String message, name;
        for (int i = 0; i < commits.size(); i++) {
            message = commits.get(i).getMessage();
            name = commits.get(i).getName();
            if (message.contains(text) || name.contains(text)) {
                newCommits.add(commits.get(i));
            }
        }
        ((CommitListAdapter) mRecyclerView.getAdapter()).setList(newCommits);
    }

}
