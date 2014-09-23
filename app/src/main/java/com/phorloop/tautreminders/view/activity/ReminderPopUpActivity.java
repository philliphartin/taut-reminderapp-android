package com.phorloop.tautreminders.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.phorloop.tautreminders.R;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Phillip J Hartin on 20/10/13.
 */

public class ReminderPopUpActivity extends Activity {
    private static MediaPlayer voicePlayer;
    private static MediaPlayer notificationPlayer;
    private static AudioManager audio;
    private static Reminder reminder;
    private Vibrator vib;
    private Handler myHandler = new Handler();

    //Voice Reminders
    private static String filepath;
    private int listenCount;
    private final String LOG = "ReminderPopUpActivity";

    //HANDLER
    private final int delayTime = 60000;   //60secs

    //Stats
    long unixStartTime;
    long unixPressTime;
    int timeElapsed;
    int batterylevel;
    int voiceduration = 0;
    Boolean userInteraction = false;
    SharedPreferences sharedPreferences;
    private Boolean voiceListened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Intialise VoicePlayer;
        voicePlayer = new MediaPlayer();
        voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                Log.i(LOG, "VoicePlayer: Playback Complete");
            }
        });

        //resetListenCount
        listenCount = 0;

        //FLAGS TO TURN SCREEN ON AND UNLOCK SCREEN
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.activity_reminderpopup);

        //GET INTENT FROM LAUNCH
        //TODO: GET BUNDLE FROM INTENT AND REBUILD REMINDER OBJECT
        Bundle extras = getIntent().getExtras();
        String reminderAsJSON = extras.getString("reminder");
        reminder = new Gson().fromJson(reminderAsJSON, Reminder.class);

//        //Update ActiveReminders to active=0
//        db.acknowledgeReminder(id);
//        db.closeDB();

        //UI ELEMENTS
        TextView tv_desc = (TextView) findViewById(R.id.textView_dialog_reminderpopup_desc);
        TextView tv_type = (TextView) findViewById(R.id.textView_dialog_reminderpopup_type);
        ImageView imgV = (ImageView) findViewById(R.id.popupImage);

        //Get VOICE User
        if (reminder.getFormat().contains("voice")) {
            Log.w(LOG, "VOICE REMINDER");

            enableOKButton(false);
//            /*filepath = db.getVoiceReminder(intentID).getFilepath();
//            voiceduration = db.getVoiceReminder(intentID).getDuration();*/

            //Set Image as GONE
            imgV.setVisibility(View.GONE);

            //Create New Button
            Button playReminderBtn = new Button(this);
            playReminderBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            playReminderBtn.setGravity(Gravity.CENTER_HORIZONTAL);
            playReminderBtn.setTextSize(20);
            playReminderBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.audio_listen, 0);
            playReminderBtn.setBackgroundResource(R.drawable.button_all);
            int humanlength = voiceduration / 1000;

            playReminderBtn.setText("Listen to Voice Reminder" + '\n' + "(" + humanlength + "secs)");
            playReminderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG, "Playing Voice Reminder");
                    //Enable OK Button
                    enableOKButton(true);
                    //Stop Notification Sounds
                    stopSoundVib();
                    //Play Voice Reminder and record time elapsed if first play
                    if (!voiceListened) {
                        voiceListened = true;
                        setTimeElapsed();
                        playVoiceReminder();
                    } else if (voiceListened) {
                        playVoiceReminder();
                    }
                }
            });

            //Get FrameLayout
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.layout_popupImage);
            frameLayout.addView(playReminderBtn);
        } else {
            Log.w(LOG, "BASIC REMINDER");
            enableOKButton(true);
        }

        //Get Battery Percentage
        getBatteryPercentage();

        //GET CURRENT DATE AS UNIX TIME
        Calendar calendarCurrent = Calendar.getInstance();
        unixStartTime = calendarCurrent.getTimeInMillis();

        //START SOUND AND VIBRATION;
        startSoundVib();

        //TIMER METHOD
        myHandler.postDelayed(closeScreen, delayTime);

        //Display text
        String type = reminder.getType();
        String description = reminder.getDescription();
        try {
            tv_desc.setText(description);
            tv_type.setText(type);
        } catch (Exception e) {
            Log.e(LOG, "Unable to set elements text");
        }

        try {
            if ("Medication".equals(type)) {
                imgV.setImageResource(R.drawable.medication_big);
            } else if ("Appointment".equals(type)) {
                imgV.setImageResource(R.drawable.clock_big);
            } else if ("Meal".equals(type)) {
                imgV.setImageResource(R.drawable.teachers_day_big);
            } else if ("Drink".equals(type)) {
                imgV.setImageResource(R.drawable.plastic_bottle_big);
            } else if ("Personal Hygiene".equals(type)) {
                imgV.setImageResource(R.drawable.hygiene_big);
            } else if ("Charge Phone".equals(type)) {
                imgV.setImageResource(R.drawable.charge_big);
            } else if ("Other".equals(type)) {
                imgV.setImageResource(R.drawable.info_big);
            }
        } catch (Exception e) {
            Log.e(LOG, "Unable to set Image Resources for drawables");
        }

        //LISTENERS
        Button okBtn = (Button) findViewById(R.id.button_dialog_reminderpopup_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!voiceListened) {
                    setTimeElapsed();
                } else {
                    //Do Nothing
                }
                userInteraction = true;
                finish();
            }
        });
    }

    private void setTimeElapsed() {
        //TIME LAPSE SETUP
        Calendar calendarPress = Calendar.getInstance();
        unixPressTime = calendarPress.getTimeInMillis();
        long difference = (unixPressTime - unixStartTime);
        timeElapsed = (int) difference;
        Log.d(LOG, "TimeElapsed: " + timeElapsed);
    }

    private void getBatteryPercentage() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                }
                batterylevel = level;
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    public void onDestroy() {
        super.onDestroy();
        stopSoundVib();
        stopVoiceReminder();

        //TODO: Fix all this shite
//        ScheduleHelper scheduleHelper = new ScheduleHelper(this);
//
//        if (userInteraction == true) {
//            //Do Nothing as already logged
//            scheduleHelper.reScheduleReminder(id, 1, timeElapsed, batterylevel, voiceduration, listenCount);
//        } else {
//            scheduleHelper.reScheduleReminder(id, 0, timeElapsed, batterylevel, voiceduration, listenCount);
//        }
    }

    private void increaseListenCount() {
        listenCount++;
        Log.d(LOG, "Num of times listened: " + listenCount);
    }

    private void startSoundVib() {
        //Sound
        notificationPlayer = MediaPlayer.create(this, R.raw.sound);
        notificationPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(LOG, "NotificationPlayer: Playback Complete");
                mp.reset();
            }
        });
        notificationPlayer.start();

        //Vibrating
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        vib.vibrate(pattern, -1);
    }

    private void stopSoundVib() {
        try {
            if (notificationPlayer.isPlaying()) {
                notificationPlayer.stop();
                notificationPlayer.reset();
                notificationPlayer.release();
                Log.i(LOG, "notificationPlayer STOPPED");
            } else {
                //Do Nothing
            }
        } catch (IllegalStateException e) {
            Log.e(LOG, "notificationPlayer error: " + e);
        }
    }

    private void playVoiceReminder() {
        try {
            if (voicePlayer.isPlaying()) {
                voicePlayer.stop();
                voicePlayer.reset();
                Log.i(LOG, "Playback RESET");
            } else {
                //Do Nothing
            }
        } catch (IllegalStateException e) {
            Log.e(LOG, "isPlaying error: " + e);
        }

        try {
            voicePlayer.setVolume(1.0f, 1.0f);
            audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, (audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
            voicePlayer.reset();
            voicePlayer.setDataSource(filepath);
            voicePlayer.prepare();
            voicePlayer.start();
            increaseListenCount();
            Log.i(LOG, "Playback STARTED");
        } catch (IOException e) {
            Log.e(LOG, "setDataSource failed:" + e);
        }
    }

    private void stopVoiceReminder() {
        try {
            voicePlayer.stop();
            voicePlayer.reset();
            voicePlayer.release();
        } catch (Exception e) {
            Log.w(LOG, "Unable to stop voiceplayer");
        }
    }

    //Closes activity after 60 seconds of inactivity
    public void onUserInteraction() {
        myHandler.removeCallbacks(closeScreen);
        myHandler.postDelayed(closeScreen, delayTime);
    }

    private Runnable closeScreen = new Runnable() {
        public void run() {
            userInteraction = false;
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        //Do Nothing
    }

    private void enableOKButton(boolean isEnable) {
        ((Button) findViewById(R.id.button_dialog_reminderpopup_ok)).setEnabled(isEnable);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // The rest of your onStart() code.
//        //Google Analytics Code
//        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        // The rest of your onStop() code.
//        //Google Analytics Code
//        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
//    }
}