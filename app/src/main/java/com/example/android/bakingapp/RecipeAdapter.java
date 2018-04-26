package com.example.android.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.bakingapp.data.Recipe;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    public RecipeAdapter(Context c, List<Recipe> recipes) {
        super(c, 0, recipes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.recipe_list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Recipe currentRecipe = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.recipe_name_view);
        // Get the earthquakeLocation from the current Book object and
        // set this text on the earthquakeLocation TextView TextView
        titleTextView.setText(currentRecipe.getName());

        // Return the whole list item layout
        // so that it can be shown in the ListView
        return listItemView;
    }
}
