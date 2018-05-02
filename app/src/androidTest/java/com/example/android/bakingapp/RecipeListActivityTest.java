package com.example.android.bakingapp;


import android.app.Activity;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.data.RecipeList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.app.Instrumentation.ActivityResult;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {
    //随机选取ListView中的一个位置
    public static final int RECIPE_ID = new Random().nextInt(4);
    public static final String STEP = "step_id";
    public static int stepId = 0;

    @Rule
    public IntentsTestRule<RecipeListActivity> mActivityTestRule = new IntentsTestRule<>(RecipeListActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }


    @Test
    public void clickViewItem_clickButtons() {
        //测试点击菜谱之后能否打开对应的步骤列表
        onData(anything()).inAdapterView(withId(R.id.recipe_list)).atPosition(RECIPE_ID).perform(click());
        intended(allOf(
                hasExtra(RecipeListActivity.RECIPE_ID, RECIPE_ID)));

        // 测试点击菜谱步骤能否打开步骤详情
        onView(withId(R.id.steps_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(stepId, click()));
        intended(allOf(
                hasExtra(STEP, stepId)));

        // 测试点击步骤详情中的前进和后退按钮能否打开正确的详情页面
        for (int i = 0; i < RecipeList.recipes.get(RECIPE_ID).getSteps().size()-1; i++) {
            onView(withId(R.id.next_button)).perform(click());
            intended(allOf(hasExtra(STEP, ++stepId)));
        }

        for (int i = RecipeList.recipes.get(RECIPE_ID).getSteps().size()-1; i > 0; i--) {
            onView(withId(R.id.pre_button)).perform(click());
            intended(allOf(hasExtra(STEP, --stepId)),times(2));
        }
    }
}
