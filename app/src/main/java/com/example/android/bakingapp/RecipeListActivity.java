package com.example.android.bakingapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;

public class RecipeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    ListView mRecipeListView;
    RecipeAdapter mRecipeAdapter;
    View emptyContentView;
    View noInternetConnectionView;
    ProgressBar progressBar;
    LoaderManager loaderManager;
    public static final String RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecipeListView = findViewById(R.id.recipe_list);

        // 无网络或无内容的empty view
        emptyContentView = findViewById(R.id.empty_content);
        noInternetConnectionView = findViewById(R.id.no_internet_connection);
        progressBar = findViewById(R.id.indeterminateBar);
        mRecipeListView.setEmptyView(emptyContentView);

        mRecipeAdapter = new RecipeAdapter(this, new ArrayList<Recipe>());
        loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int i, Bundle bundle) {
        return new RecipeLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> recipes) {

        mRecipeAdapter.clear();
        mRecipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecipeListActivity.this, StepsActivity.class);
                intent.putExtra(RECIPE_ID, (int) id);
                startActivity(intent);
            }
        });
        progressBar.setVisibility(View.GONE);
        if (NetworkUtils.isHttpsConnectionOk(this) || recipes != null) {
            noInternetConnectionView.setVisibility(View.GONE);
        }
        SettingsFragment.updateWidgets(this);
        mRecipeListView.setAdapter(mRecipeAdapter);
        if (recipes != null) {
            mRecipeAdapter.addAll(recipes);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {
        mRecipeAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_refresh:
                if (!NetworkUtils.isHttpsConnectionOk(this)) {
                    Toast.makeText(this, R.string.no_network_connection_info, Toast.LENGTH_SHORT).show();
                    break;
                }
                progressBar.setVisibility(View.VISIBLE);
                loaderManager.destroyLoader(0);
                loaderManager.initLoader(0, null, this);
                RecipeList.recipes.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
