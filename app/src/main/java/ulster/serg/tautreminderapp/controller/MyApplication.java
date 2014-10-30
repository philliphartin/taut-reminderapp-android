package ulster.serg.tautreminderapp.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.orm.SugarApp;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.HashMap;

/**
 * Created by philliphartin on 16/09/2014.
 */

public class MyApplication extends SugarApp {

    //MixPanel Analytics
    HashMap<TrackerName, MixpanelAPI> mMixpanels = new HashMap<TrackerName, MixpanelAPI>();

    //Google Analytics Hashmap
    //HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    @Override
    public void onCreate() {
        super.onCreate();

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


    //Mix Analytics
    public synchronized MixpanelAPI getMixPanelAnalyticsTracker(TrackerName trackerName) {
        if (!mMixpanels.containsKey(trackerName)) {

            //Setup MixPanel Instance
            String token = getString(ulster.serg.tautreminderapp.R.string.MIXPANEL_TOKEN_DEV);
            MixpanelAPI mMixpanel = MixpanelAPI.getInstance(this, token);
            mMixpanels.put(trackerName, mMixpanel);
        }
        return mMixpanels.get(trackerName);
    }


//    //Google Analytics
//    public synchronized Tracker getGoogleAnalyticsTracker(TrackerName trackerId) {
//        if (!mTrackers.containsKey(trackerId)) {
//
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            //analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
//
//            Tracker t = analytics.newTracker(R.xml.global_tracker);
//            mTrackers.put(trackerId, t);
//        }
//        return mTrackers.get(trackerId);
//    }

    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }


}
