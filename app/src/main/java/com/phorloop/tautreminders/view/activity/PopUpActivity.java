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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Phillip J Hartin on 20/10/13.
 */

public class PopUpActivity extends Activity {
    private final static String LOG = "PopUpActivity";
    //Screen delay before auto closing

    private final int delayTime = 60000;   //6ti0secs
    //Statistics to log
    private long timeToAcknowledge;
    //Components
    private MediaPlayer voicePlayer;
    private MediaPlayer notificationPlayer;
    private AudioManager audio;
    private Vibrator vib;

    //Reminder
    private Reminder reminder;
    //Initialise Log Object
    private Acknowledgement acknowledgement = new Acknowledgement();
    //Instance tracking
    private long unixTimePopUpDelivered;
    private int listenCount;
    private Boolean voicePlayerListened = false;
    private Boolean userInteraction = false;
    //Timers
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            userInteraction = false;
            finish();
        }
    };
    private Timer timer = new Timer();

    private long getTimeToAcknowledge() {
        return timeToAcknowledge;
    }

    private void setTimeToAcknowledge(long timeToAcknowledge) {
        this.timeToAcknowledge = timeToAcknowledge;
    }

    //Getter Setters
    private long getUnixTimePopUpDelivered() {
        return unixTimePopUpDelivered;
    }

    private void setUnixTimePopUpDelivered(long unixTimePopUpDelivered) {
        this.unixTimePopUpDelivered = unixTimePopUpDelivered;
    }

    private int getListenCount() {
        return listenCount;
    }

    private void setListenCount(int listenCount) {
        this.listenCount = listenCount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Record time that popup delivered
        setUnixTimePopUpDelivered(getCurrentUnixTime());

        //TIMER METHOD
        timer.schedule(timerTask, delayTime);

        //Get reminder object from intent
        Bundle extras = getIntent().getExtras();
        String reminderIdentifierExtra = extras.getString("reminderIdentifier");
        long reminderIdentifier = Long.parseLong(reminderIdentifierExtra);
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

        //START SOUND AND VIBRATION;
        startPopUpSoundAndVibration();


        //Establish type of reminder
        ReminderHelper reminderHelper = new ReminderHelper(this);

        if (reminderHelper.isVoiceReminder(reminder)) {
            //Initialise VoicePlayer;
            voicePlayer = new MediaPlayer();
            voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
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

    //Common methods
    private long getCurrentUnixTime() {
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        return time;
    }

    private long getTimeDifference(long startTime, long endTime) {
        long difference = (endTime - startTime);
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
        return batteryLevel;
    }

    private void increaseListenCount() {
        setListenCount(getListenCount() + 1);
    }

    private void startPopUpSoundAndVibration() {
        //Sound
        notificationPlayer = MediaPlayer.create(this, R.raw.sound);
        notificationPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
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

//    //Closes activity after 60 seconds of inactivity
//    public void onUserInteraction() {
//        timer.cancel();
////        myHandler.removeCallbacks(closeScreen);
////        myHandler.postDelayed(closeScreen, delayTime);
//    }

    private void stopSoundVibrationVoice() {
        stopPopUpSoundAndVibration();
        stopVoiceReminder();
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

        //Log Acknowledgement
        AcknowledgementHelper acknowledgementHelper = new AcknowledgementHelper(this);
        acknowledgement.setTimetoacknowledge(getTimeToAcknowledge());
        acknowledgement.setBatterylevel((int) getBatteryLevel());
        acknowledgement.setListencount(getListenCount());
        acknowledgementHelper.saveAcknowledgmentLogforReminder(reminder, acknowledgement, userInteraction);
        //Send details to rescheduler
        ScheduleHelper scheduleHelper = new ScheduleHelper(this);
        scheduleHelper.rescheduleReminder(reminder);
    }

    @Override
    public void onBackPressed() {
        //Override default onBackPressed functionality to avoid acknowledgements being ignored
    }
}