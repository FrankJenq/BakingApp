package com.example.android.bakingapp;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.data.Ingredient;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private List<Ingredient> mIngredientList;

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientView;
        TextView unitView;

        public IngredientViewHolder(View view) {
            super(view);
            ingredientView = (TextView) view.findViewById(R.id.ingredients_name);
            unitView = (TextView) view.findViewById(R.id.ingredients_unit);
        }
    }


    public IngredientsAdapter(List<Ingredient> ingredients) {
        mIngredientList = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredients_list_item, parent, false);

        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient ingredient = mIngredientList.get(position);
        holder.ingredientView.setText(ingredient.getIngredient());
        holder.unitView.setText(ingredient.getQuantity() + ingredient.getMeasure());
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }
}
