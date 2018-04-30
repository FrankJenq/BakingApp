package com.example.android.bakingapp.data;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeListActivity;

import java.util.List;

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;
    private int mAppWidgetId;
    private int mRecipeId;
    private List<Ingredient> ingredientList;

    public WidgetListProvider(Context context, Intent intent) {
        this.context = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mRecipeId = intent.getIntExtra(RecipeListActivity.RecipeId, 0);
        ingredientList = RecipeList.recipes.get(mRecipeId).getIngredients();
    }

    @Override
    public int getCount() {
        return ingredientList.size();
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.ingredients_list_item);
        Ingredient ingredient = ingredientList.get(position);
        remoteView.setTextViewText(R.id.ingredients_name, ingredient.getIngredient());
        remoteView.setTextViewText(R.id.ingredients_unit, ingredient.getQuantity() + ingredient.getMeasure());

        Intent fillInIntent = new Intent();
        // Make it possible to distinguish the individual on-click
        // action of a given item
        remoteView.setOnClickFillInIntent(R.id.ingredients_name, fillInIntent);
        return remoteView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }
}
