package com.example.android.bakingapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.RecipeList;

import java.util.List;

public class IngredientsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int recipeId = getIntent().getIntExtra(RecipeListActivity.RecipeId, 0);
        setContentView(R.layout.activity_ingredients);

        // 根据菜谱设置标题
        String recipeName = getString(R.string.ingredients_act_label) + " " + RecipeList.recipes.get(recipeId).getName();
        setTitle(recipeName);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ingredients_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(RecipeList.recipes.get(recipeId).getIngredients());
        recyclerView.setAdapter(ingredientsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

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
}
