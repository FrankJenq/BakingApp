package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.utils.RecipeImageUtils;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {

    public static int recipeId = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int widgetId : appWidgetIds) {
            Intent intent = new Intent(context, StepsActivity.class);
            intent.putExtra(RecipeListActivity.RecipeId, recipeId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            // Update image and text
            views.setImageViewResource(R.id.recipe_image, RecipeImageUtils.getPlantImgRes(context, recipeId));
            views.setTextViewText(R.id.recipe_name, RecipeList.recipes.get(recipeId).getName());
            // Widgets allow click handlers to only launch pending intents
            views.setOnClickPendingIntent(R.id.recipe_image, pendingIntent);
            // Add the next recipe click handler
            Intent nextIntent = new Intent(context, RecipeWidget.class);
            nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            nextIntent.putExtra(RecipeListActivity.RecipeId, recipeId);
            PendingIntent nextRecipePendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.next_recipe_button, nextRecipePendingIntent);
            if (recipeId < RecipeList.recipes.size() - 1) {
                recipeId++;
            } else {
                recipeId = 0;
            }
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
}

