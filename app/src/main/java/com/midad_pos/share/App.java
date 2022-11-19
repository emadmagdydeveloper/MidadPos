package com.midad_pos.share;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import com.midad_pos.language.Language;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.uis.activity_receipts.ReceiptsActivity;
import com.midad_pos.uis.activity_settings.SettingsActivity;
import com.midad_pos.uis.activity_shift.ShiftActivity;
import com.midad_pos.uis.activity_support.SupportActivity;

import java.util.HashSet;
import java.util.Set;


public class App extends MultiDexApplication {

    private Set<Activity> runningActivities = new HashSet<>();
    public static boolean navigate = false;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Language.updateResources(newBase,"ar"));
    }


    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                if (activity instanceof ReceiptsActivity||activity instanceof ItemsActivity||activity instanceof SettingsActivity||activity instanceof SupportActivity||activity instanceof ShiftActivity){
                    runningActivities.add(activity);
                }




            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }


    public void killAllActivities(){
        for (Activity activity:runningActivities){
            activity.finish();
            activity.overridePendingTransition(0,0);
        }
    }
}

