package com.example.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.data.Step;

import java.util.List;

public class StepsActivity extends AppCompatActivity {

    public static final String STEP_ID = "step_id";
    private static int recipeId;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeId = getIntent().getIntExtra(RecipeListActivity.RecipeId, 0);
        setContentView(R.layout.activity_steps);
        setTitle(RecipeList.recipes.get(recipeId).getName());

        TextView ingredientsView = (TextView) findViewById(R.id.ingredients_view);
        ingredientsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StepsActivity.this, IngredientsActivity.class);
                intent.putExtra(RecipeListActivity.RecipeId, recipeId);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.steps_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        StepsAdapter stepsAdapter = new StepsAdapter(RecipeList.recipes.get(recipeId).getSteps());

        mRecyclerView.setAdapter(stepsAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

        private List<Step> mStepList;

        public class StepViewHolder extends RecyclerView.ViewHolder {
            public TextView stepView;

            public StepViewHolder(View view) {
                super(view);
                stepView = (TextView) view.findViewById(R.id.step_name);
            }
        }


        public StepsAdapter(List<Step> steps) {
            mStepList = steps;
        }

        @Override
        public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.steps_list_item, parent, false);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = mRecyclerView.getChildAdapterPosition(v);
                    Intent intent = new Intent(StepsActivity.this, StepDetailActivity.class);
                    Step step = mStepList.get(itemPosition);
                    intent.putExtra(STEP_ID, Integer.parseInt(step.getId()));
                    intent.putExtra(RecipeListActivity.RecipeId, recipeId);
                    startActivity(intent);
                }
            };
            itemView.setOnClickListener(listener);
            return new StepViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(StepViewHolder holder, int position) {
            Step step = mStepList.get(position);
            holder.stepView.setText(step.getShortDescription());
        }

        @Override
        public int getItemCount() {
            return mStepList.size();
        }
    }
}
