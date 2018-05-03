package com.example.android.bakingapp.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Recipe {

    @SerializedName("ingredients")
    @Expose
    private ArrayList<Ingredient> ingredients;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("steps")
    @Expose
    private ArrayList<Step> steps;

    public Recipe(ArrayList<Ingredient> ingredients,String name,ArrayList<Step>steps){
        this.ingredients = ingredients;
        this.name = name;
        this.steps = steps;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }
}
