package com.example.android.bakingapp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Results {
    @SerializedName("")
    @Expose
    private List<Recipe> recipes = null;

    public List<Recipe> getRecipes(){
        return recipes;
    }
}
