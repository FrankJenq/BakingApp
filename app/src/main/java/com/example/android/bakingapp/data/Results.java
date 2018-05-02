package com.example.android.bakingapp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Results {
    @SerializedName("")
    @Expose
    private ArrayList<Recipe> recipes = null;

    public ArrayList<Recipe> getRecipes(){
        return recipes;
    }
}
