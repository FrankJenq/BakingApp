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

    int mRecipeId;
    private final String IS_FROM_INGREDIENTS_ACTIVITY= "is_from_ingredients_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecipeId = getIntent().getIntExtra(RecipeListActivity.RECIPE_ID, 0);
        setContentView(R.layout.activity_ingredients);

        if (RecipeList.recipes==null||RecipeList.recipes.size()==0){
            return;
        }
        // 根据菜谱设置标题
        String recipeName = getString(R.string.ingredients_act_label) + " " + RecipeList.recipes.get(mRecipeId).getName();
        setTitle(recipeName);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ingredients_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(RecipeList.recipes.get(mRecipeId).getIngredients());
        recyclerView.setAdapter(ingredientsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onResume() {
        super.onResume();

        // 如果菜谱数据为空，则返回主Activity重新加载数据，然后再恢复现有的状态
        if (RecipeList.recipes==null||RecipeList.recipes.size()==0){
            Intent intent = new Intent(IngredientsActivity.this,RecipeListActivity.class);
            intent.putExtra(RecipeListActivity.RECIPE_ID,mRecipeId);
            intent.putExtra(IS_FROM_INGREDIENTS_ACTIVITY,true);
            startActivity(intent);
            return;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRecipeId = savedInstanceState.getInt(RecipeListActivity.RECIPE_ID);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RecipeListActivity.RECIPE_ID,mRecipeId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(IngredientsActivity.this, StepsActivity.class);
                intent.putExtra(RecipeListActivity.RECIPE_ID, mRecipeId);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
