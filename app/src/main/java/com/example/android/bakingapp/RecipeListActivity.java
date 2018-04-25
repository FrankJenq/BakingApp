package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.data.Results;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    ListView mRecipeListView;
    public static final String RecipeId = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecipeListView = findViewById(R.id.recipe_list);
        RecipeList.recipes=getRecipes(this);
        RecipeAdapter recipeAdapter = new RecipeAdapter(this, RecipeList.recipes);
        mRecipeListView.setAdapter(recipeAdapter);

        mRecipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecipeListActivity.this,StepsActivity.class);
                intent.putExtra(RecipeId,(int)id);
                startActivity(intent);
            }
        });
    }

    private class RecipeAdapter extends ArrayAdapter<Recipe> {
        public RecipeAdapter(Context c, List<Recipe> recipes) {
            super(c, 0, recipes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Check if the existing view is being reused, otherwise inflate the view
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.recipe_list_item, parent, false);
            }

            // Get the {@link AndroidFlavor} object located at this position in the list
            Recipe currentRecipe = getItem(position);

            TextView titleTextView = (TextView) listItemView.findViewById(R.id.recipe_name);
            // Get the earthquakeLocation from the current Book object and
            // set this text on the earthquakeLocation TextView TextView
            titleTextView.setText(currentRecipe.getName());

            // Return the whole list item layout
            // so that it can be shown in the ListView
            return listItemView;
        }
    }

    private List<Recipe> getRecipes(Context context) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("recipes.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Results results = gson.fromJson(json, Results.class);
        return results.getRecipes();
    }
}
