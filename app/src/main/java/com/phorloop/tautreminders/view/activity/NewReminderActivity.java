package com.phorloop.tautreminders.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.johnpersano.supertoasts.SuperToast;
import com.phorloop.tautreminders.R;
import com.phorloop.tautreminders.controller.schedule.ReminderHelper;
import com.phorloop.tautreminders.model.sugarorm.Reminder;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewReminderActivity extends Activity {
    private static final String LOGa = "NewReminderActivity";

    //FragmentTags
    private final static String fTAG_adltype = "fTAG_adltype";
    private final static String fTAG_checkuser = "fTAG_checkuser";
    private final static String fTAG_confirmdetails = "fTAG_confirmdetails";
    private final static String fTAG_formatchoice = "fTAG_formatchoice";
    private final static String fTAG_pickdate = "fTAG_pickdate";
    private final static String fTAG_recordvoice = "fTAG_recordvoice";
    private final static String fTAG_repeatfrequency = "fTAG_repeatfrequency";

    //Objects
    private static Reminder reminder = new Reminder();

    //RepeatDays
    private static boolean repeatMon;
    private static boolean repeatTue;
    private static boolean repeatWed;
    private static boolean repeatThu;
    private static boolean repeatFri;
    private static boolean repeatSat;
    private static boolean repeatSun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FormatChoiceFragment())
                    .commit();
        }

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Create New Reminder");
    }

    /**
     * Fragment to choose reminder format
     */
    public static class FormatChoiceFragment extends Fragment {
        private static final String LOGf = "FormatChoiceFragment";

        public FormatChoiceFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_reminder_formatchoice, container, false);

            //Init UI elements
            Button button_basicReminder = (Button) rootView.findViewById(R.id.button_createbasic);
            button_basicReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reminder.setFormat("basic");
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new PickDateFragment(), fTAG_pickdate)
                            .addToBackStack(null)
                            .commit();
                }
            });


            Button button_voiceReminder = (Button) rootView.findViewById(R.id.button_createvoice);
            button_voiceReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reminder.setFormat("voice");
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new RecordVoiceFragment(), fTAG_recordvoice)
                            .addToBackStack(null)
                            .commit();
                }
            });

            return rootView;
        }
    }

    /**
     * Fragment to record voice
     */
    public static class RecordVoiceFragment extends Fragment {
        private static final String LOGf = "RecordVoiceFragment";
        private String currentFragment = fTAG_recordvoice;

        //Method Specific items
        private static final String LOG = "New Reminder Audio";
        private static final String AUDIO_RECORDER_FOLDER = "TAUT/VoiceReminders";
        private static String rec_filepath = null;

        private static MediaPlayer player = null;
        private static AudioManager audio;
        private MediaRecorder recorder = null;
        private static int rec_duration = 0;

        //TODO: RECORD AUDIO AND SAVE TO FILE
        public RecordVoiceFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_new_reminder_recordvoice, container, false);
            Button button_next = (Button) rootView.findViewById(R.id.button_new_reminder_next);
            button_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    rec_duration = getRec_duration();
                    Log.d(LOG, "Recording Length (ms): " + rec_duration);

                    if (rec_duration == 0) {
                        createToastWithText("Please record a message", getActivity());
                    } else {

                        //TODO: Store recording filePath and duration to reminder object
                        Log.d(LOG, "Recording Filename: " + rec_filepath);
                        getFragmentManager().beginTransaction()
                                .hide(getFragmentManager().findFragmentByTag(currentFragment))
                                .replace(R.id.container, new PickDateFragment(), fTAG_pickdate)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

            //Recording Buttons
            Button button_start = (Button) rootView.findViewById(R.id.btnStart);
            Button button_stop = (Button) rootView.findViewById(R.id.btnStop);
            Button button_play = (Button) rootView.findViewById(R.id.btnPlay);

            button_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playButton(true, rootView);
                    enableButtons(true, rootView);
                    startRecording();
                }
            });
            button_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enableButtons(false, rootView);
                    stopRecording();
                }
            });
            button_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playBack();
                }
            });

            enableButtons(false, rootView);
            playButton(false, rootView);
            player = new MediaPlayer();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    Log.i(LOG, "Recording: Playback Complete");
                }
            });
            return rootView;
        }

        private void enableButton(int id, boolean isEnable, View view) {
            Button button = (Button) view.findViewById(id);
            button.setEnabled(isEnable);
        }

        private void playButton(boolean recorded, View view) {
            enableButton(R.id.btnPlay, recorded, view);
        }

        private void enableButtons(boolean isRecording, View view) {
            enableButton(R.id.btnStart, !isRecording, view);
            enableButton(R.id.btnPlay, !isRecording, view);
            enableButton(R.id.btnStop, isRecording, view);
        }

        private String getFilename() {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, AUDIO_RECORDER_FOLDER);

            if (!file.exists()) {
                file.mkdirs();
            }

            rec_filepath = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4";
            Log.d(LOG, "Recorded File:" + rec_filepath);
            return (rec_filepath);
        }

        private void startRecording() {
            recorder = new MediaRecorder();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(16);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(getFilename());
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);

            try {
                recorder.prepare();
                recorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ;

        private void stopRecording() {
            if (null != recorder) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        }

        private void playBack() {
            if (player.isPlaying()) {
                player.stop();
                player.reset();
                Log.i(LOG, "Playback STOPPED by User");
            } else {
                try {
                    player.setVolume(1.0f, 1.0f);
                    audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, (audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
                    player.setDataSource(rec_filepath);
                    player.prepare();
                    player.start();
                    Log.i(LOG, "Playback STARTED by User");


                } catch (IOException e) {
                    Log.e(LOG, "prepare() failed");
                }
            }
        }

        private int getRec_duration() {
            try {
                if (rec_filepath != null) {
                    MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(rec_filepath));
                    int rec_duration = mp.getDuration();
                    return rec_duration;

                } else {
                    Log.e(LOG, "No recording filepath");
                    return rec_duration = 0;
                }
            } catch (Exception e) {
                Log.e(LOG, "Get Recording length failed");
                return rec_duration = 0;
            }
        }

        private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                createToastWithText("Error: " + what + ", " + extra, getActivity());
            }
        };
        private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                createToastWithText("Warning: " + what + ", " + extra, getActivity());
            }
        };

    }

    /**
     * Fragment to pick date and time
     */

    public static class PickDateFragment extends Fragment {
        private static final String LOGf = "PickDateFragment";
        private String currentFragment = fTAG_pickdate;

        //UI elements
        private Button button_pickDate;
        private Button button_pickTime;

        //Complete flags
        private Boolean timeChosen = false;
        private Boolean dateChosen = false;

        //Method Specific items
        Calendar cal_selected = Calendar.getInstance();

        public PickDateFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_reminder_pickdate, container, false);

            Button button_next = (Button) rootView.findViewById(R.id.button_new_reminder_next);
            button_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean complete[] = {dateAndTimeSelected(dateChosen, timeChosen), timeIsInFuture(cal_selected)};

                    if (allComplete(complete)) {

                        reminder.setUnixtime(cal_selected.getTimeInMillis());
                        reminder.setDate(DateForSave(cal_selected));
                        reminder.setTime(TimeForSave(cal_selected));

                        getFragmentManager().beginTransaction()
                                .hide(getFragmentManager().findFragmentByTag(currentFragment))
                                .add(R.id.container, new ADLTypeFragment(), fTAG_adltype)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

            //Init buttons for date and time picking
            button_pickDate = (Button) rootView.findViewById(R.id.button_newreminder_DATE);
            button_pickDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(getActivity(), date, cal_selected
                            .get(Calendar.YEAR), cal_selected.get(Calendar.MONTH),
                            cal_selected.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.setTitle("Pick a DATE for your reminder");
                    datePickerDialog.getDatePicker().setCalendarViewShown(false);
                    datePickerDialog.getDatePicker().setMinDate((new Date().getTime()) - 1000);
                    datePickerDialog.show();
                }
            });

            button_pickTime = (Button) rootView.findViewById(R.id.button_newreminder_TIME);
            button_pickTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog;
                    timePickerDialog = new TimePickerDialog(getActivity(), time, cal_selected
                            .get(Calendar.HOUR_OF_DAY), cal_selected.get(Calendar.MINUTE), false);
                    timePickerDialog.setTitle("Pick a TIME for your reminder");
                    timePickerDialog.show();
                }
            });

            return rootView;
        }

        private String DateForSave(Calendar calendar) {
            //Create Formats for display
            String saveFormat = "MM-dd-yyyy"; //In which you need put here
            SimpleDateFormat sdf_save = new SimpleDateFormat(saveFormat, Locale.US);
            return sdf_save.format(cal_selected.getTime());
        }

        private String TimeForSave(Calendar calendar) {
            //Create Formats for display
            String saveFormat = "HH:mm";
            SimpleDateFormat sdf_save = new SimpleDateFormat(saveFormat, Locale.US);
            return sdf_save.format(cal_selected.getTime());
        }

        //DATEPICKER
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //Set Calendar Values
                cal_selected.set(Calendar.YEAR, year);
                cal_selected.set(Calendar.MONTH, monthOfYear);
                cal_selected.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Create Formats for display
                String humanFormat = "MMMM dd, yyyy"; //In which you need put here
                SimpleDateFormat sdf_human = new SimpleDateFormat(humanFormat, Locale.US);
                button_pickDate.setText(sdf_human.format(cal_selected.getTime()));

                dateChosen = true;
            }
        };

        //TIMEPICKER
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cal_selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal_selected.set(Calendar.MINUTE, minute);

                //Create Formats for display
                String humanFormat = "h:mm aa"; //In which you need put here
                SimpleDateFormat sdf_human = new SimpleDateFormat(humanFormat, Locale.US);
                button_pickTime.setText(sdf_human.format(cal_selected.getTime()));

                timeChosen = true;
            }
        };

        private boolean dateAndTimeSelected(Boolean dateChosen, Boolean timeChosen) {
            if (!dateChosen && !timeChosen) {
                createToastWithText("Ensure both date and time are selected", getActivity());
                return false;
            } else if (!timeChosen) {
                createToastWithText("Please select a time", getActivity());
                return false;
            } else if (!dateChosen) {
                createToastWithText("Please select a date", getActivity());
                return false;
            } else {
                return true;
            }
        }

        private boolean timeIsInFuture(Calendar chosenTime) {
            Calendar now = Calendar.getInstance();
            long unix_now = now.getTimeInMillis() / 1000L;
            long unix_chosen = chosenTime.getTimeInMillis() / 1000L;

            if (unix_chosen <= unix_now) {
                createToastWithText("Ensure date/time chosen is in the future", getActivity());
                return false;
            } else {
                return true;
            }
        }

    }

    /**
     * Fragment to set reminder ADL types
     */
    public static class ADLTypeFragment extends Fragment {
        private static final String LOGf = "ADLTypeFragment";
        private String currentFragment = fTAG_adltype;

        //UI elements
        Spinner spinnerADLType;
        EditText editTextDescription;

        //Method Specific items

        //TODO: RECORD ADL TYPE AND DESCRIPTIONS

        public ADLTypeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_reminder_adltype, container, false);

            //Navigate to
            Button button_next = (Button) rootView.findViewById(R.id.button_new_reminder_next);
            button_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (descriptionEntered()) {

                        finaliseADLType();
                        getFragmentManager().beginTransaction()
                                .hide(getFragmentManager().findFragmentByTag(currentFragment))
                                .add(R.id.container, new RepeatFrequencyFragment(), fTAG_repeatfrequency)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        createToastWithText("Please enter a description to continue", getActivity());
                        editTextDescription.setError("Enter description here");
                    }

                }
            });

            spinnerADLType = (Spinner) rootView.findViewById(R.id.spinner_fragment_new_reminder_adltype);
            editTextDescription = (EditText) rootView.findViewById(R.id.edittext_fragment_new_reminder_adltype);
            return rootView;
        }

        private boolean descriptionEntered() {
            if (editTextDescription.getText().length() > 2) {
                return true;
            } else {
                return false;
            }
        }

        private void finaliseADLType() {
            reminder.setType(spinnerADLType.getSelectedItem().toString());
            reminder.setDescription(editTextDescription.getText().toString().trim());
        }
    }

    /**
     * Fragment to set reminder ADL types
     */
    public static class RepeatFrequencyFragment extends Fragment {
        private static final String LOGf = "RepeatFrequencyFragment";
        private String currentFragment = fTAG_repeatfrequency;

        //Complete flags
        private Boolean repeatFrequencyChosen = false;
        private Boolean customrepeatFrequencyChosen = false; //Set to true if normal chosen

        //UI elements
        LinearLayout layout_customOptions;
        RadioGroup radioGroup;
        RadioButton radioButton_weekly, radioButton_daily, radioButton_custom;
        CheckBox checkBox_mon, checkBox_tue, checkBox_wed, checkBox_thu, checkBox_fri, checkBox_sat, checkBox_sun;
        int checkboxesCount = 0;

        //Save details
        String repeat;

        //TODO: RECORD FREQ TYPE AND DESCRIPTIONS
        public RepeatFrequencyFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_reminder_repeatfrequency, container, false);

            //Init UI elements
            layout_customOptions = (LinearLayout) rootView.findViewById(R.id.layout_custom_hide);

            radioButton_custom = (RadioButton) rootView.findViewById(R.id.radioButton_new_reminder2_custom);
            radioButton_daily = (RadioButton) rootView.findViewById(R.id.radioButton_new_reminder2_everyday);
            radioButton_weekly = (RadioButton) rootView.findViewById(R.id.radioButton_new_reminder2_weekly);
            radioButton_weekly.setText("Weekly " + "(" + getRepeatDay() + ")");

            checkBox_mon = (CheckBox) rootView.findViewById(R.id.checkBox_mon);
            checkBox_tue = (CheckBox) rootView.findViewById(R.id.checkBox_tue);
            checkBox_wed = (CheckBox) rootView.findViewById(R.id.checkBox_wed);
            checkBox_thu = (CheckBox) rootView.findViewById(R.id.checkBox_thu);
            checkBox_fri = (CheckBox) rootView.findViewById(R.id.checkBox_fri);
            checkBox_sat = (CheckBox) rootView.findViewById(R.id.checkBox_sat);
            checkBox_sun = (CheckBox) rootView.findViewById(R.id.checkBox_sun);

            CompoundButton.OnCheckedChangeListener incrementCheckBoxesCount = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkboxesCount++;
                        Log.d(LOGf, "Checkbox Count: " + checkboxesCount);
                    } else {
                        checkboxesCount--;
                        Log.d(LOGf, "Checkbox Count: " + checkboxesCount);
                    }
                }
            };


            //OnCheckChange listener to keep track of number selected
            checkBox_mon.setOnCheckedChangeListener(incrementCheckBoxesCount);
            checkBox_tue.setOnCheckedChangeListener(incrementCheckBoxesCount);
            checkBox_wed.setOnCheckedChangeListener(incrementCheckBoxesCount);
            checkBox_thu.setOnCheckedChangeListener(incrementCheckBoxesCount);
            checkBox_fri.setOnCheckedChangeListener(incrementCheckBoxesCount);
            checkBox_sat.setOnCheckedChangeListener(incrementCheckBoxesCount);
            checkBox_sun.setOnCheckedChangeListener(incrementCheckBoxesCount);

            //RADIOGROUP LISTENER
            radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup_new_reminder2_rg);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radioButton_new_reminder2_never:
                            repeat = "Never";
                            layout_customOptions.setVisibility(View.GONE);
                            break;
                        case R.id.radioButton_new_reminder2_everyday:
                            repeat = "Everyday";
                            layout_customOptions.setVisibility(View.GONE);
                            break;
                        case R.id.radioButton_new_reminder2_weekly:
                            repeat = "Weekly";
                            layout_customOptions.setVisibility(View.GONE);
                            break;
                        case R.id.radioButton_new_reminder2_custom:
                            repeat = "Custom";
                            layout_customOptions.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });

            //Navigate to
            Button button_next = (Button) rootView.findViewById(R.id.button_new_reminder_next);
            button_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (repeatSelected()) {
                        if (customRepeatSelected()) {
                            if (customRepeatSelected() && customDaysSelected()) {
                                // If custom has been selected , Check at least one custom day has been selected.
                                // If so, assign.
                                assignCustomDays();
                                finaliseDetails();
                                navigateToNextScreen();
                            }
                        } else {
                            finaliseDetails();
                            navigateToNextScreen();
                        }
                    }
                }
            });

            return rootView;
        }

        private void navigateToNextScreen() {
            getFragmentManager().beginTransaction()
                    .hide(getFragmentManager().findFragmentByTag(currentFragment))
                    .add(R.id.container, new UserCheckFragment(), fTAG_checkuser)
                    .addToBackStack(null)
                    .commit();
        }

        private boolean repeatSelected() {
            if (repeat != null) {
                return true;
            } else {
                createToastWithText("Please select a repeat type", getActivity());
                return false;
            }
        }


        private boolean customRepeatSelected() {
            if (repeat.contains("Custom")) {
                return true;
            } else {
                return false;
            }
        }

        private boolean customDaysSelected() {
            if (checkboxesCount > 0) {
                return true;
            } else {
                createToastWithText("Please select day/s ", getActivity());
                return false;
            }

        }

        private String getRepeatDay() {
            long timeOfReminder = reminder.getUnixtime();
            Instant selectedInstantReminder = new Instant(timeOfReminder);
            DateTime selectedDateTimeReminder = selectedInstantReminder.toDateTime();

            return selectedDateTimeReminder.dayOfWeek().getAsText();
        }

        private void assignCustomDays() {
            if (checkBox_mon.isChecked()) {
                repeatMon = true;
            } else {
                repeatMon = false;
            }

            if (checkBox_tue.isChecked()) {
                repeatTue = true;
            } else {
                repeatTue = false;
            }

            if (checkBox_wed.isChecked()) {
                repeatWed = true;
            } else {
                repeatWed = false;
            }

            if (checkBox_thu.isChecked()) {
                repeatThu = true;
            } else {
                repeatThu = false;
            }

            if (checkBox_fri.isChecked()) {
                repeatFri = true;
            } else {
                repeatFri = false;
            }

            if (checkBox_sat.isChecked()) {
                repeatSat = true;
            } else {
                repeatSat = false;
            }

            if (checkBox_sun.isChecked()) {
                repeatSun = true;
            } else {
                repeatSun = false;
            }
        }

        private void finaliseDetails() {
            reminder.setRepeatfreq(repeat);
        }
    }

    /**
     * Fragment to check user type
     */
    public static class UserCheckFragment extends Fragment {
        private static final String LOGf = "UserCheckFragment";
        private String currentFragment = fTAG_checkuser;

        public UserCheckFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_reminder_checkuser, container, false);

            //Init UI elements
            Button button_participant = (Button) rootView.findViewById(R.id.button_new_reminder_checkuser_participant);
            button_participant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reminder.setCreatedby("user");
                    navigateToNextScreen();
                }
            });


            Button button_carer = (Button) rootView.findViewById(R.id.button_new_reminder_checkuser_carer);
            button_carer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO: Get carer details and assign to string array
                    String[] users = {"bill", "ben"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Please select who you are")
                            .setItems(users, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    //TODO: Set Carer ID based on 'which' position
                                    reminder.setCreatedby("carer");
                                    reminder.setCreatedbyid(1234); //FIXME: FIX DIS YO
                                    navigateToNextScreen();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
            return rootView;
        }

        private void navigateToNextScreen() {
            getFragmentManager().beginTransaction()
                    .hide(getFragmentManager().findFragmentByTag(currentFragment))
                    .add(R.id.container, new ConfirmDetailsFragment(), fTAG_confirmdetails)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Fragment to confirm details
     */
    public static class ConfirmDetailsFragment extends Fragment {
        private static final String LOGf = "ConfirmDetailsFragment";
        private static Context context;

        //UI elements
        LinearLayout customDaysLayout;
        TextView customDaysText;

        public ConfirmDetailsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_reminder_confirmdetails, container, false);

            context = getActivity();
            //Init UI Elements
            customDaysLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout_customdays);
            customDaysText = (TextView) rootView.findViewById(R.id.textView_customdays);
            checkCustomDaysLayout();

            TextView tv_date = (TextView) rootView.findViewById(R.id.textView_save_date);
            TextView tv_time = (TextView) rootView.findViewById(R.id.textView_save_time);
            TextView tv_type = (TextView) rootView.findViewById(R.id.textView_save_type);
            TextView tv_repeat = (TextView) rootView.findViewById(R.id.textView_save_repeat);

            tv_date.setText(reminder.getDate());
            tv_time.setText(reminder.getTime());
            tv_type.setText(reminder.getType());
            tv_repeat.setText(reminder.getRepeatfreq());

            Button button_save = (Button) rootView.findViewById(R.id.button_new_reminder_save_confirm);
            button_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: SAVE THE REMINDER
                    saveReminder();
                    returnToHomeScreen();
                }
            });

            Button button_cancel = (Button) rootView.findViewById(R.id.button_new_reminder_save_cancel);
            button_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure you want to discard details?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Do your coding here. For Positive button
                                            returnToHomeScreen();
                                        }
                                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
            return rootView;
        }

        private void checkCustomDaysLayout() {
            if (reminder.getRepeatfreq().contains("Custom")) {
                customDaysLayout.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                if (repeatMon == true) {
                    sb.append("Monday ");
                }
                if (repeatTue == true) {
                    sb.append("Tuesday ");
                }
                if (repeatWed == true) {
                    sb.append("Wednesday ");
                }
                if (repeatThu == true) {
                    sb.append("Thursday ");
                }
                if (repeatFri == true) {
                    sb.append("Friday ");
                }
                if (repeatSat == true) {
                    sb.append("Saturday ");
                }
                if (repeatSun == true) {
                    sb.append("Sunday ");
                }
                customDaysText.setText(sb.toString());
            } else {
                customDaysLayout.setVisibility(View.GONE);
            }
        }

        private void returnToHomeScreen() {
            Intent i = new Intent(getActivity(), HomeScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        private void saveReminder() {

            if (reminder.getRepeatfreq().contains("Custom")) {
                saveCustomReminder();
            } else {
                //Save normal reminder
                Instant selectedInstantReminder = new Instant(reminder.getUnixtime());
                DateTime selectedDateTimeReminder = selectedInstantReminder.toDateTime();
                String dayasText = selectedDateTimeReminder.dayOfWeek().getAsText();

                Log.i(LOGf,
                        "" + dayasText + " Y:" + selectedDateTimeReminder.getYear()
                                + " M:" + selectedDateTimeReminder.getMonthOfYear()
                                + " D:" + selectedDateTimeReminder.getDayOfMonth()
                                + " H:" + selectedDateTimeReminder.getHourOfDay()
                                + " M:" + selectedDateTimeReminder.getMinuteOfHour());

                reminder.setDayofweek(dayasText);
                reminder.setActive(1);

                //Save Reminder
                ReminderHelper reminderHelper = new ReminderHelper(context);
                reminderHelper.saveReminder(reminder);
            }
        }

        private void saveCustomReminder() {

            //Create Joda Instant (from reminderTime Millseconds)
            Instant selectedInstantReminder = new Instant(reminder.getUnixtime());
            DateTime selectedDateTimeReminder = selectedInstantReminder.toDateTime();

            boolean[] repeatArrayDays = produceCustomRepeatArray();

            //Iterate through array to check if a reminder is required
            ReminderHelper reminderHelper = new ReminderHelper(context);

            for (int i = 0; i < repeatArrayDays.length; i++) {
                if (repeatArrayDays[i]) {

                    //Add 1 to i to make it equal to days of the week
                    int day = i + 1;
                    DateTime reminderDate = calcNextDate(selectedDateTimeReminder, day);
                    String dayasText = reminderDate.dayOfWeek().getAsText();

                    Log.i(LOGf,
                            "" + dayasText + " Y:" + reminderDate.getYear()
                                    + " M:" + reminderDate.getMonthOfYear()
                                    + " D:" + reminderDate.getDayOfMonth()
                                    + " H:" + reminderDate.getHourOfDay()
                                    + " M:" + reminderDate.getMinuteOfHour());

                    //FORMAT DATE TO STRINGS FOR DB
                    long newTime = reminderDate.getMillis();
                    Log.d(LOGf, dayasText + " NewTimeforDB: " + newTime);

                    DateTimeFormatter fmt_date = DateTimeFormat.forPattern("MM-dd-yyyy");
                    String new_date = fmt_date.print(newTime);

                    //Set reminder object details
                    reminder.setRepeatfreq("Weekly");
                    reminder.setDayofweek(dayasText);
                    reminder.setDate(new_date);
                    reminder.setUnixtime(newTime);
                    reminder.setActive(1);
                    //Save Reminder
                    reminderHelper.saveReminder(reminder);
                }
            }
        }

        private boolean[] produceCustomRepeatArray() {

            boolean[] repeatArrayDays = new boolean[7];
            repeatArrayDays[0] = repeatMon;
            repeatArrayDays[1] = repeatTue;
            repeatArrayDays[2] = repeatWed;
            repeatArrayDays[3] = repeatThu;
            repeatArrayDays[4] = repeatFri;
            repeatArrayDays[5] = repeatSat;
            repeatArrayDays[6] = repeatSun;

            return repeatArrayDays;
        }

    }

    //Common Methods
    private static boolean allComplete(boolean[] values) {
        for (boolean value : values) {
            if (!value)
                return false;
        }
        return true;
    }

    private static void createToastWithText(String text, Activity activity) {
        SuperToast superToast = new SuperToast(activity);
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setText(text);
        superToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
        superToast.setGravity(Gravity.BOTTOM, 0, 50);
        superToast.show();
    }

    private static DateTime calcNextDate(DateTime d, int day) {
        if (d.getDayOfWeek() > day) {
            d = d.plusWeeks(1);
        }
        return d.withDayOfWeek(day);
    }

    //Convert long to int method
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
