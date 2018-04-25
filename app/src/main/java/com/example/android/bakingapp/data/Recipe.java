package com.example.android.bakingapp.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {
    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("servings")
    @Expose
    private String servings;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("steps")
    @Expose
    private List<Step> steps;

    public List<Ingredient> getIngredients ()
    {
        return ingredients;
    }

    public void setIngredients (List<Ingredient> ingredients)
    {
        this.ingredients = ingredients;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getServings ()
    {
        return servings;
    }

    public void setServings (String servings)
    {
        this.servings = servings;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public List<Step> getSteps ()
    {
        return steps;
    }

    public void setSteps (List<Step> steps)
    {
        this.steps = steps;
    }
}
