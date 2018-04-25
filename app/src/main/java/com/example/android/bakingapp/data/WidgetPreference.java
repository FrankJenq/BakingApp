package com.example.android.bakingapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.bakingapp.R;

public final class WidgetPreference {

    public static final int NUTELLA_PIE = 0;
    public static final int BROWNIES = 1;
    public static final int YELLOW_CAKE = 2;
    public static final int CHEESECAKE = 3;

    public static int getPreference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForWidgets = context.getString(R.string.pref_widget_key);
        String defaultWidget = context.getString(R.string.pref_widget_nutella_pie);
        String preferredWidget = sp.getString(keyForWidgets, defaultWidget);
        String nutellaPieWidget = context.getString(R.string.pref_widget_nutella_pie);
        String browniesWidget = context.getString(R.string.pref_widget_brownies);
        String yellowCakeWidget = context.getString(R.string.pref_widget_yellow_cake);
        if (nutellaPieWidget.equals(preferredWidget)) {
            return NUTELLA_PIE;
        } else if (browniesWidget.equals(preferredWidget)) {
            return BROWNIES;
        } else if (yellowCakeWidget.equals(preferredWidget)){
            return YELLOW_CAKE;
        }
        return CHEESECAKE;
    }
}
