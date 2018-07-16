package com.example.redheats.nytimes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
//import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter articleArrayAdapter;

    MenuItem filterAction, searchAction;
    SearchView searchView;

    static RequestParams params = new RequestParams();

    //public RequestParams getParams() {
    //    return params;
    //}

    int page = 0;
    String search = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isOnline()){
            setContentView(R.layout.activity_search);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            articles = new ArrayList<>();
            articleArrayAdapter = new ArticleArrayAdapter(this, articles);

            gvResults= findViewById(R.id.gvResults);
            gvResults.setNumColumns(2);

            gvResults.setAdapter(articleArrayAdapter);

            gvResults.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if(firstVisibleItem + visibleItemCount >= totalItemCount){
                        if(firstVisibleItem + visibleItemCount >= totalItemCount){
                            page++;
                            if(!TextUtils.isEmpty(search)){
                                onArticleSearch(search, page);
                            }
                        }

                    }
                }
            });
            gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Article article = articles.get(i);
                    //intent.putExtra("article", Parcels.wrap(article));
                    //startActivity(intent);

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(ContextCompat.getColor(SearchActivity.this, R.color.colorAccent));
                    builder.addDefaultShareMenuItem();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(SearchActivity.this, Uri.parse(article.getWeb_url()));
                }
            });
        }
        else{
            //AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setCancelable(false);
            //builder.setTitle("Connection Error");
            //builder.setMessage("You should check your connection and try again");
            //builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            //    @Override
            //    public void onClick(DialogInterface dialogInterface, int i) {
            //        finish();
            //    }
            //});
            //builder.show();
            Toast.makeText(this, "No connection detected \n Check your network and restart the app", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        filterAction = menu.findItem(R.id.action_settings);
        searchAction = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchAction);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                searchView.clearFocus();

                articles.clear();
                page = 0;
                onArticleSearch(s, page);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(SearchActivity.this, Settings.class));
                return true;
            case R.id.action_search:
                searchAction.expandActionView();
                searchView.requestFocus();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(String string, int page) {
        AsyncHttpClient client= new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        if(params.toString().isEmpty()){
            params = new RequestParams();
            params.put("api-key", "7e9e308adad54701bda5ebd303d976d0");
            params.put("page", page);
            params.put("q", string);
        }
        else {
            params.put("api-key","7e9e308adad54701bda5ebd303d976d0");
            params.put("page",page);
            params.put("q",string);
        }
        if (!params.toString().contains("sort")){
            SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
            if (sharedPreferences.getInt("order", 0) == 0){
                params.put("sort", "newest");
            }
            else {
                params.put("sort", "oldest");
            }
        }

        Log.d("REQUESTPARAMS", params.toString());


        client.get(url,params,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArray;
                try {
                    jsonArray = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(jsonArray));
                    articleArrayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
