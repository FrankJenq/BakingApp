package com.example.android.bakingapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.android.bakingapp.data.RecipeList;

public class IngredientsActivity extends AppCompatActivity {

    int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recipeId = getIntent().getIntExtra(RecipeListActivity.RecipeId, 0);
        setContentView(R.layout.activity_ingredients);

        // 根据菜谱设置标题
        String recipeName = getString(R.string.ingredients_act_label) + " " + RecipeList.recipes.get(recipeId).getName();
        setTitle(recipeName);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ingredients_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(RecipeList.recipes.get(recipeId).getIngredients());
        recyclerView.setAdapter(ingredientsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(IngredientsActivity.this, StepsActivity.class);
                intent.putExtra(RecipeListActivity.RecipeId, recipeId);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
