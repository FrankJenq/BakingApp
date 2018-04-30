package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.data.WidgetPreference;
import com.example.android.bakingapp.data.WidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {
    private int recipeId;

    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {

        switch (WidgetPreference.getPreference(context)) {
            case WidgetPreference.NUTELLA_PIE:
                recipeId = WidgetPreference.NUTELLA_PIE;
                break;
            case WidgetPreference.BROWNIES:
                recipeId = WidgetPreference.BROWNIES;
                break;
            case WidgetPreference.YELLOW_CAKE:
                recipeId = WidgetPreference.YELLOW_CAKE;
                break;
            case WidgetPreference.CHEESECAKE:
                recipeId = WidgetPreference.CHEESECAKE;
                break;
        }

        final int numberOfWidgets = appWidgetIds.length;
        for (int i = 0; i < numberOfWidgets; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);
            remoteViews.setTextViewText(R.id.widget_title, RecipeList.recipes.get(recipeId).getName());
            Intent intent= new Intent(context, StepsActivity.class);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            intent.putExtra(RecipeListActivity.RecipeId, recipeId);
            PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.list_view_widget, clickPI);
            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(), R.layout.recipe_widget);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, WidgetService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);


        svcIntent.putExtra(RecipeListActivity.RecipeId, recipeId);
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(R.id.list_view_widget,
                svcIntent);

        remoteViews.setEmptyView(R.id.list_view_widget, R.id.empty_view);
        return remoteViews;
    }
}

