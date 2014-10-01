package com.phorloop.tautreminders.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.phorloop.tautreminders.R;
import com.phorloop.tautreminders.controller.helpers.AcknowledgementHelper;
import com.phorloop.tautreminders.controller.helpers.ReminderHelper;
import com.phorloop.tautreminders.controller.helpers.ScheduleHelper;
import com.phorloop.tautreminders.model.sugarorm.Acknowledgement;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Phillip J Hartin on 20/10/13.
 */

public class PopUpActivity extends Activity {
    private final static String LOG = "PopUpActivity";

    //Components
    private static MediaPlayer voicePlayer;
    private static MediaPlayer notificationPlayer;
    private static AudioManager audio;
    private static Vibrator vib;
    private Handler myHandler = new Handler();

    //Screen delay before auto closing
    private final int delayTime = 60000;   //60secs

    //Reminder
    private static Reminder reminder;

    //Initialise Log Object
    private static Acknowledgement acknowledgement = new Acknowledgement();

    //Instance tracking
    private static long unixTimePopUpDelivered;
    private static int listenCount;
    private static Boolean voicePlayerListened = false;
    private static Boolean userInteraction = false;

    //Statistics to log
    private static long timeToAcknowledge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Record time that popup delivered
        setUnixTimePopUpDelivered(getCurrentUnixTime());

        //Get reminder object from intent
        Bundle extras = getIntent().getExtras();
        String reminderIdentifierExtra = extras.getString("reminderIdentifier");
        long reminderIdentifier = Long.parseLong(reminderIdentifierExtra);
        //String reminderAsJSON = extras.getString("reminder");
        //reminder = new Gson().fromJson(reminderAsJSON, Reminder.class);
        reminder = Reminder.findById(Reminder.class, reminderIdentifier);

        //Flags to turn screen on and attempt to unlock keyguard
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.activity_reminderpopup);

        //Init UI elements
        TextView tv_desc = (TextView) findViewById(R.id.textView_dialog_reminderpopup_desc);
        TextView tv_type = (TextView) findViewById(R.id.textView_dialog_reminderpopup_type);
        ImageView imageView_reminderType = (ImageView) findViewById(R.id.popupImage);

        //Establish type of reminder

        ReminderHelper reminderHelper = new ReminderHelper(this);

        if (reminderHelper.isVoiceReminder(reminder)) {
            //Initialise VoicePlayer;
            voicePlayer = new MediaPlayer();
            voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    Log.i(LOG, "VoicePlayer: Playback Complete");
                }
            });

            enableOKButton(false); //Disable acknowledgment button until listened

            //Remove imageView and replace with play button
            imageView_reminderType.setVisibility(View.GONE);

            //Create and init voice reminder playback button
            setListenCount(0);

            Button playReminderBtn = new Button(this);
            playReminderBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            playReminderBtn.setGravity(Gravity.CENTER_HORIZONTAL);
            playReminderBtn.setTextSize(20);
            playReminderBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.audio_listen, 0);
            playReminderBtn.setBackgroundResource(R.drawable.button_all);
            long audioDurationInSeconds = reminder.getAudioduration() / 1000;
            playReminderBtn.setText("Listen to Voice Reminder" + '\n' + "(" + audioDurationInSeconds + "secs)");
            playReminderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG, "Playing Voice Reminder");
                    //Enable OK Button
                    enableOKButton(true);
                    //Stop Notification Sounds
                    stopPopUpSoundAndVibration();
                    //Check if voice has been listened to before.
                    if (!voicePlayerListened) { //If not, log the time elapsed from popup to first listen.
                        setTimeToAcknowledge(getTimeDifference(getUnixTimePopUpDelivered(), getCurrentUnixTime()));
                        voicePlayerListened = true; //update flag
                    }

                    playVoiceReminder(reminder.getAudiofilepath());
                }
            });

            //Get FrameLayout
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.layout_popupImage);
            frameLayout.addView(playReminderBtn);
        } else {
            //Is a basic reminder
            enableOKButton(true);
        }

        //START SOUND AND VIBRATION;
        startPopUpSoundAndVibration();

        //TIMER METHOD
        myHandler.postDelayed(closeScreen, delayTime);

        //Display reminder details
        String type = reminder.getType();
        String description = reminder.getDescription();
        try {
            tv_desc.setText(description);
            tv_type.setText(type);
        } catch (Exception e) {
            Log.e(LOG, "Unable to set elements text");
        }

        try {
            imageView_reminderType.setImageResource(getImageForReminderType(type));
        } catch (Exception e) {
            Log.e(LOG, "Unable to set Image Resources for drawables");
        }

        //Button OnClick listener
        Button button_acknowledge = (Button) findViewById(R.id.button_dialog_reminderpopup_ok);
        button_acknowledge.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setTimeToAcknowledge(getTimeDifference(getUnixTimePopUpDelivered(), getCurrentUnixTime()));
                userInteraction = true;
                finish(); //Will call onDestroy method
            }
        });
    }


    private void rescheduleReminderIfNeeded() {
        ScheduleHelper scheduleHelper = new ScheduleHelper(this);
        if (scheduleHelper.needsRescheduled(reminder)) {
            scheduleHelper.rescheduleReminder(reminder);
        }
    }


    //Common methods
    private long getCurrentUnixTime() {
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        Log.d(LOG, "Current unixtime: " + time + "ms");

        return time;
    }

    private long getTimeDifference(long startTime, long endTime) {
        long difference = (endTime - startTime);
        Log.d(LOG, "Time difference: " + difference + "ms");
        return difference;
    }

    private float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        float batteryLevel = ((long) level / (float) scale) * 100.0f;
        Log.d(LOG, "Battery Level: " + batteryLevel);

        return batteryLevel;
    }


    private void increaseListenCount() {
        setListenCount(getListenCount() + 1);
    }

    private void startPopUpSoundAndVibration() {
        //Sound
        notificationPlayer = MediaPlayer.create(this, R.raw.sound); //FIXME: Change sound for release
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

    private void stopPopUpSoundAndVibration() {
        try {
            if (notificationPlayer.isPlaying()) {
                notificationPlayer.stop();
                notificationPlayer.reset();
                notificationPlayer.release();
                Log.d(LOG, "notificationPlayer STOPPED");
            }
        } catch (IllegalStateException e) {
            Log.e(LOG, "notificationPlayer error: " + e);
        }
    }

    private void playVoiceReminder(String audioFilePath) {
        // If it is playing, stop and reset the player
        try {
            if (voicePlayer.isPlaying()) {
                stopVoiceReminder();
            }
        } catch (IllegalStateException e) {
            Log.e(LOG, "isPlaying error: " + e);
        }

        //Setup the player
        try {
            voicePlayer.setVolume(1.0f, 1.0f);
            audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, (audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
            voicePlayer.reset();
            voicePlayer.setDataSource(audioFilePath);
            voicePlayer.prepare();
            voicePlayer.start();
            increaseListenCount();
            Log.d(LOG, "Voice reminder playback started");
        } catch (IOException e) {
            Log.e(LOG, "setDataSource failed:" + e);
        }
    }

    private void stopVoiceReminder() {
        try {
            voicePlayer.stop();
            voicePlayer.reset();
            voicePlayer.release();
            Log.d(LOG, "Voice reminder playback stopped");
        } catch (Exception e) {
            Log.w(LOG, "Unable to stop voiceplayer");
        }
    }

    private void enableOKButton(boolean isEnable) {
        Button button = (Button) findViewById(R.id.button_dialog_reminderpopup_ok);
        button.setEnabled(isEnable);
    }

    private int getImageForReminderType(String type) {
        if ("Medication".equals(type)) {
            return R.drawable.medication_big;
        } else if ("Appointment".equals(type)) {
            return R.drawable.clock_big;
        } else if ("Meal".equals(type)) {
            return R.drawable.teachers_day_big;
        } else if ("Drink".equals(type)) {
            return R.drawable.plastic_bottle_big;
        } else if ("Personal Hygiene".equals(type)) {
            return R.drawable.hygiene_big;
        } else if ("Charge Phone".equals(type)) {
            return R.drawable.charge_big;
        } else if ("Other".equals(type)) {
            return R.drawable.info_big;
        } else {
            return R.drawable.info_big;
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


    private void stopSoundVibrationVoice() {
        stopPopUpSoundAndVibration();
        stopVoiceReminder();
    }

    //Getter Setters
    public static long getUnixTimePopUpDelivered() {
        return unixTimePopUpDelivered;
    }

    public static void setUnixTimePopUpDelivered(long unixTimePopUpDelivered) {
        PopUpActivity.unixTimePopUpDelivered = unixTimePopUpDelivered;
    }

    public static int getListenCount() {
        return listenCount;
    }

    public static void setListenCount(int listenCount) {
        PopUpActivity.listenCount = listenCount;
        Log.d(LOG, "listenCount: " + listenCount);
    }

    public static long getTimeToAcknowledge() {
        return timeToAcknowledge;
    }

    public static void setTimeToAcknowledge(long timeToAcknowledge) {

        PopUpActivity.timeToAcknowledge = timeToAcknowledge;
    }

    private int convertBooleanToInt(Boolean bool) {
        return bool ? 1 : 0;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSoundVibrationVoice();

        AcknowledgementHelper acknowledgementHelper = new AcknowledgementHelper(this);

        //Set acknowledgment properties before passing to helper
        acknowledgement.setTimetoacknowledge(getTimeToAcknowledge());
        acknowledgement.setBatterylevel((int) getBatteryLevel());
        acknowledgement.setListencount(getListenCount());

        acknowledgementHelper.saveAcknowledgmentLogforReminder(reminder, acknowledgement, userInteraction);

        rescheduleReminderIfNeeded();
    }

    @Override
    public void onBackPressed() {
        //Override default onBackPressed functionality to avoid acknowledgements being ignored
    }
}