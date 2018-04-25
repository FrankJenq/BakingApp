package com.example.android.bakingapp.utils;

import android.content.Context;

public class RecipeImageUtils {
    private static final int NUTELLA_PIE = 0;
    private static final int BROWNIES = 1;
    private static final int YELLOW_CAKE = 2;
    private static final int CHEESECAKE = 3;

    public static int getPlantImgRes(Context context, int recipeId) {
        String resName = "";
        if (recipeId == NUTELLA_PIE) {
            resName += "nutella_pie";
        } else if (recipeId == BROWNIES) {
            resName += "brownies";
        } else if (recipeId == YELLOW_CAKE) {
            resName += "yellow_cake";
        } else if (recipeId == CHEESECAKE) {
            resName += "cheese_cake";
        }
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }
}
