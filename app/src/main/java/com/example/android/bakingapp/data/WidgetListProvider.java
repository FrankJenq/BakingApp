package com.example.android.bakingapp.data;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeListActivity;
import com.example.android.bakingapp.StepsActivity;

import java.util.List;

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;
    private int mAppWidgetId;
    private int mRecipeId;
    private List<Step> mStepList;

    public WidgetListProvider(Context context, Intent intent) {
        this.context = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mRecipeId = intent.getIntExtra(RecipeListActivity.RecipeId, 0);
        mStepList = RecipeList.recipes.get(mRecipeId).getSteps();
    }

    @Override
    public int getCount() {
        return mStepList.size();
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_list_item);
        if (position == 0) {
            remoteView.setTextViewText(R.id.widget_step, "Ingredients");
        } else if (position == 1) {
            Step step = mStepList.get(position);
            remoteView.setTextViewText(R.id.widget_step, "Introduction" + ": " + step.getShortDescription());
        } else if (position > 1) {
            Step step = mStepList.get(position);
            int stepId = position - 1;
            remoteView.setTextViewText(R.id.widget_step, "Step " + stepId + ": " + step.getShortDescription());
        }

        Bundle extras = new Bundle();
        extras.putInt(StepsActivity.STEP_ID, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        remoteView.setOnClickFillInIntent(R.id.widget_step, fillInIntent);

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
