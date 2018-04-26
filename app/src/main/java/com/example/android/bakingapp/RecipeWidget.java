package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.data.WidgetPreference;
import com.example.android.bakingapp.utils.RecipeImageUtils;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {

    public static int recipeId = 0;
    public static final String RECIPE = " Recipe";
    private final String NO_RECIPE = "No Recipe";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        switch (WidgetPreference.getPreference(context)) {
            case WidgetPreference.NUTELLA_PIE:
                recipeId = WidgetPreference.NUTELLA_PIE;
                break;
            case WidgetPreference.BROWNIES:
                recipeId = WidgetPreference.BROWNIES;
                break;
            case WidgetPreference.YELLOW_CAKE:
                recipeId = WidgetPreference.YELLOW_CAKE;
            case WidgetPreference.CHEESECAKE:
                recipeId = WidgetPreference.CHEESECAKE;
                break;
        }

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int widgetId : appWidgetIds) {
            Intent intent;
            PendingIntent pendingIntent;
            if (RecipeList.recipes != null) {
                intent = new Intent(context, StepsActivity.class);
                intent.putExtra(RecipeListActivity.RecipeId, recipeId);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                intent = new Intent(context,RecipeListActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            // Update image and text
            views.setImageViewResource(R.id.recipe_image, RecipeImageUtils.getPlantImgRes(context, recipeId));
            if (RecipeList.recipes != null) {
                views.setTextViewText(R.id.recipe_name, RecipeList.recipes.get(recipeId).getName() + RECIPE);
            } else {
                views.setTextViewText(R.id.recipe_name, NO_RECIPE);
            }
            // Widgets allow click handlers to only launch pending intents
            views.setOnClickPendingIntent(R.id.recipe_image, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
            int[] ids = intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else super.onReceive(context, intent);
    }
}

