package ulster.serg.tautreminderapp.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.orm.SugarApp;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.HashMap;

import ulster.serg.tautreminderapp.BuildConfig;
import ulster.serg.tautreminderapp.R;

/**
 * Created by philliphartin on 16/09/2014.
 */

public class MyApplication extends SugarApp {

    private static MyApplication instance;

    //Analytics Trackers
    HashMap<TrackerName, MixpanelAPI> mMixpanels = new HashMap<TrackerName, MixpanelAPI>();     //MixPanel Analytics
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();     //Google Analytics Hashmap

    public MyApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(getClass().getSimpleName(), "BuildType: : " + BuildConfig.BUILD_TYPE);

        JodaTimeAndroid.init(this);

        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {

                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    //MixPanel Analytics
    public synchronized MixpanelAPI getMixPanelAnalyticsTracker(TrackerName trackerName) {
        if (!mMixpanels.containsKey(trackerName)) {

            //Get Tracker Token for build
            String mixpanel_token = BuildConfig.MIXPANEL_TOKEN;

            //Setup MixPanel Instance
            MixpanelAPI mMixpanel = MixpanelAPI.getInstance(this, mixpanel_token);
            mMixpanels.put(trackerName, mMixpanel);
            mMixpanel.logPosts();
        }
        return mMixpanels.get(trackerName);
    }


    //Google Analytics
    public synchronized Tracker getGoogleAnalyticsTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            //analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }


}
