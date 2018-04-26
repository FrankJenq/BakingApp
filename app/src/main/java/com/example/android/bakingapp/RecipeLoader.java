package com.example.android.bakingapp;


import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeApiService;
import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.data.RetroClient;
import com.example.android.bakingapp.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

    private final String RECIPE_URL =
            "https://s3.cn-north-1.amazonaws.com.cn/static-documents/nd801/ProjectResources/Baking/baking-cn.json";
    Context mContext;


    RecipeLoader(Context c) {
        super(c);
        mContext = c;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Recipe> loadInBackground() {
        if (!NetworkUtils.isHttpsConnectionOk(mContext)) {
            return RecipeList.recipes;
        }
        return getRecipes();
    }

    private List<Recipe> getRecipes() {
            RecipeApiService api = RetroClient.getApiService();
            Call<ArrayList<Recipe>> recipesCall = api.getRecipesJSON(RECIPE_URL);
            try {
                RecipeList.recipes = recipesCall.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return RecipeList.recipes;
    }
}
