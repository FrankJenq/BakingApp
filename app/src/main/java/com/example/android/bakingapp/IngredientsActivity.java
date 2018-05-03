package com.example.android.bakingapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.data.Step;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private final String NUMBER_OF_RECIPES = "number_of_recipes";
    int mRecipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecipeId = getIntent().getIntExtra(RecipeListActivity.RECIPE_ID, 0);
        setContentView(R.layout.activity_ingredients);

        //假如RecipeList.recipes为空，则尝试从Bundle中恢复数据
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_RECIPES)) {
            if (RecipeList.recipes == null || RecipeList.recipes.size() == 0) {
                int numberOfRecipes = savedInstanceState.getInt(NUMBER_OF_RECIPES);
                for (int i = 0; i < numberOfRecipes; i++) {
                    ArrayList<Ingredient> ingredients = savedInstanceState.getParcelableArrayList(RecipeList.sIngredientsLabel + i);
                    String name = savedInstanceState.getString(RecipeList.sNamesLabel + i);
                    ArrayList<Step> steps = savedInstanceState.getParcelableArrayList(RecipeList.sStepsLabel + i);
                    Recipe recipe = new Recipe(ingredients, name, steps);
                    RecipeList.recipes.add(recipe);
                }
            }
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRecipeId = savedInstanceState.getInt(RecipeListActivity.RECIPE_ID);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RecipeListActivity.RECIPE_ID,mRecipeId);
        outState.putInt(NUMBER_OF_RECIPES, RecipeList.recipes.size());
        for (int i = 0; i < RecipeList.recipes.size(); i++) {
            outState.putParcelableArrayList(RecipeList.sIngredientsLabel + i, RecipeList.recipes.get(i).getIngredients());
            outState.putString(RecipeList.sNamesLabel + i, RecipeList.recipes.get(i).getName());
            outState.putParcelableArrayList(RecipeList.sStepsLabel + i, RecipeList.recipes.get(i).getSteps());
        }
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
