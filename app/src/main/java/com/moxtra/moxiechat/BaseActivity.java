package com.moxtra.moxiechat;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.moxtra.moxiechat.common.PreferenceUtil;

/**
 * Created by blade on 3/26/15.
 */
public class BaseActivity extends ActionBarActivity {

    private static final String TAG = "BaseActivity";
    protected MenuItem miActionProgressItem;
    protected MenuItem miSettings;
    protected MenuItem miSearch;
    protected boolean isLoading;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        miSettings = menu.findItem(R.id.action_settings);
        miSearch = menu.findItem(R.id.action_search);
        if (isLoading) {
            miActionProgressItem.setVisible(true);
        }
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, ConferenceListActivity.class)));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void showProgressBar(boolean ifShow) {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(ifShow);
        }
    }

    protected void logout() {
        PreferenceUtil.removeUser(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
