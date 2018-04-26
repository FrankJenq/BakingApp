package com.example.android.bakingapp.data;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RecipeApiService {

    @GET
    Call<ArrayList<Recipe>> getRecipesJSON(
            @Url String url
    );
}

