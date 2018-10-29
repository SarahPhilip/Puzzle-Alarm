package com.sdsu.cs646.shameetha.alarmclocktest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class AlarmDetailsActivity extends ActionBarActivity {

    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
    private AlarmModel alarmDetails;
    private TimePicker timePicker;
    private EditText edtName;
    private AlarmToggleButton chkWeekly;
    private AlarmToggleButton vibrate;
    private TextView txtToneSelection;
    private ArrayList<Integer> mSelectedItems;
    boolean checked[] = new boolean[7];
    int difficulty;
    int questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_details);

        getSupportActionBar().setTitle("Create New Alarm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
        edtName = (EditText) findViewById(R.id.alarm_details_name);
        chkWeekly = (AlarmToggleButton) findViewById(R.id.alarm_details_repeat_weekly);
        vibrate = (AlarmToggleButton) findViewById(R.id.alarm_details_vibrate);
        txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);

        long id = getIntent().getExtras().getLong("id");

        if (id == -1) {
            alarmDetails = new AlarmModel();
        }
        else {
            alarmDetails = dbHelper.getAlarm(id);

            timePicker.setCurrentMinute(alarmDetails.timeMinute);
            timePicker.setCurrentHour(alarmDetails.timeHour);

            edtName.setText(alarmDetails.name);

            chkWeekly.setChecked(alarmDetails.repeatWeekly);
            vibrate.setChecked(alarmDetails.vibration);
            txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
        }

        final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                if (alarmDetails.alarmTone != null) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alarmDetails.alarmTone);
                } else {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                }
                startActivityForResult(intent, 1);
            }
        });

        final RelativeLayout repeatDaysContainer = (RelativeLayout) findViewById(R.id.repeat_days_container);
        Arrays.fill(checked, Boolean.FALSE);
        if(alarmDetails.getRepeatingDay(alarmDetails.SUNDAY)){
            checked[alarmDetails.SUNDAY] = true;
        }
        if(alarmDetails.getRepeatingDay(alarmDetails.MONDAY)){
            checked[alarmDetails.MONDAY] = true;
        }
        if(alarmDetails.getRepeatingDay(alarmDetails.TUESDAY)){
            checked[alarmDetails.TUESDAY] = true;
        }
        if(alarmDetails.getRepeatingDay(alarmDetails.WEDNESDAY)){
            checked[alarmDetails.WEDNESDAY] = true;
        }
        if(alarmDetails.getRepeatingDay(alarmDetails.THURSDAY)){
            checked[alarmDetails.THURSDAY] = true;
        }
        if(alarmDetails.getRepeatingDay(alarmDetails.FRIDAY)){
            checked[alarmDetails.FRIDAY] = true;
        }
        if(alarmDetails.getRepeatingDay(alarmDetails.SATURDAY)){
            checked[alarmDetails.SATURDAY] = true;
        }
        if(areAllFalse(checked)) {
            checked[(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)] = true;
            alarmDetails.setRepeatingDay((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1),true);
        }
        setRepeatingDays();
        repeatDaysContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectedItems = new ArrayList<Integer>();

                if(alarmDetails.getRepeatingDay(alarmDetails.SUNDAY)){
                    mSelectedItems.add(alarmDetails.SUNDAY);
                }
                if(alarmDetails.getRepeatingDay(alarmDetails.MONDAY)){
                    mSelectedItems.add(alarmDetails.MONDAY);
                }
                if(alarmDetails.getRepeatingDay(alarmDetails.TUESDAY)){
                    mSelectedItems.add(alarmDetails.TUESDAY);
                }
                if(alarmDetails.getRepeatingDay(alarmDetails.WEDNESDAY)){
                    mSelectedItems.add(alarmDetails.WEDNESDAY);
                }
                if(alarmDetails.getRepeatingDay(alarmDetails.THURSDAY)){
                    mSelectedItems.add(alarmDetails.THURSDAY);
                }
                if(alarmDetails.getRepeatingDay(alarmDetails.FRIDAY)){
                    mSelectedItems.add(alarmDetails.FRIDAY);
                }
                if(alarmDetails.getRepeatingDay(alarmDetails.SATURDAY)){
                    mSelectedItems.add(alarmDetails.SATURDAY);
                }
                if(areAllFalse(checked)) {
                    mSelectedItems.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Choose the days")
                        .setMultiChoiceItems(R.array.week_days, checked, new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    mSelectedItems.add(which);
                                } else {
                                    if (mSelectedItems.size() > 1) {
                                        mSelectedItems.remove(Integer.valueOf(which));
                                    }
                                    else {
                                        ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                                        checked[which] = true;
                                    }
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, checked[AlarmModel.SUNDAY]);
                                alarmDetails.setRepeatingDay(AlarmModel.MONDAY, checked[AlarmModel.MONDAY]);
                                alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, checked[AlarmModel.TUESDAY]);
                                alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, checked[AlarmModel.WEDNESDAY]);
                                alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, checked[AlarmModel.THURSDAY]);
                                alarmDetails.setRepeatingDay(AlarmModel.FRIDAY, checked[AlarmModel.FRIDAY]);
                                alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, checked[AlarmModel.SATURDAY]);
                                setRepeatingDays();
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
        });

        final LinearLayout difficultyContainer = (LinearLayout) findViewById(R.id.difficulty_container);
        difficulty = alarmDetails.getDifficulty();
        setDifficultyString();
        difficultyContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectedItems = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Choose the level")
                        .setSingleChoiceItems(R.array.difficulty_levels, alarmDetails.getDifficulty(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                difficulty = which;
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                alarmDetails.setDifficulty(difficulty);
                                setDifficultyString();
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
        });
        final LinearLayout questionsContainer = (LinearLayout) findViewById(R.id.questions_container);
        questions = alarmDetails.getNumberOfQuestions();
        TextView questionSelection = (TextView) findViewById(R.id.question_numbers);
        questionSelection.setText(String.valueOf(questions));
        questionsContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Choose the number of Questions")
                        .setSingleChoiceItems(R.array.questions, alarmDetails.getNumberOfQuestions()-1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                questions = which+1;
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                alarmDetails.setNumberOfQuestions(questions);
                                TextView questionSelection = (TextView) findViewById(R.id.question_numbers);
                                questionSelection.setText(String.valueOf(questions));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    TextView txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
                    txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));

                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_save_alarm_details: {
                updateModelFromLayout();
                setDay();
                AlarmBroadcastReceiver.cancelAlarms(this);

                if (alarmDetails.id < 0) {
                    dbHelper.createAlarm(alarmDetails);
                } else {
                    dbHelper.updateAlarm(alarmDetails);
                }

                AlarmBroadcastReceiver.setAlarms(this);

                setResult(RESULT_OK);
                finish();
                break;
            }
            case R.id.action_delete_alarm_details: {
                final long alarmId = alarmDetails.id;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please confirm")
                        .setTitle("Delete alarm?")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlarmBroadcastReceiver.cancelAlarms(AlarmDetailsActivity.this);
                                dbHelper.deleteAlarm(alarmId);
                                AlarmBroadcastReceiver.setAlarms(AlarmDetailsActivity.this);
                                setResult(RESULT_OK);
                                finish();
                            }
                        }).show();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateModelFromLayout() {

        TimePicker timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
        alarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
        alarmDetails.timeHour = timePicker.getCurrentHour().intValue();

        alarmDetails.name = edtName.getText().toString();
        alarmDetails.repeatWeekly = chkWeekly.isChecked();
        alarmDetails.vibration = vibrate.isChecked();
        alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, checked[AlarmModel.SUNDAY]);
        alarmDetails.setRepeatingDay(AlarmModel.MONDAY, checked[AlarmModel.MONDAY]);
        alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, checked[AlarmModel.TUESDAY]);
        alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, checked[AlarmModel.WEDNESDAY]);
        alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, checked[AlarmModel.THURSDAY]);
        alarmDetails.setRepeatingDay(AlarmModel.FRIDAY, checked[AlarmModel.FRIDAY]);
        alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, checked[AlarmModel.SATURDAY]);
        alarmDetails.isEnabled = true;
        alarmDetails.repeatOnce = false;
        alarmDetails.setDifficulty(difficulty);
        alarmDetails.setNumberOfQuestions(questions);
    }

    private void setDay() {
        Boolean result = false;
        for (int i = 0; i < 7; ++i) {
            if(alarmDetails.getRepeatingDay(i))
                result = true;
            alarmDetails.setRepeatingDay(i, alarmDetails.getRepeatingDay(i));
        }
        if(!result)
            alarmDetails.setRepeatingDay((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1), true);
    }

    public static boolean areAllFalse(boolean[] array)
    {
        for(boolean b : array)
            if(b)
                return false;
        return true;
    }

    public void setDifficultyString () {
        String difficultyString = "EASY";
        if(alarmDetails.getDifficulty() == 0){
            difficultyString = "EASY";
        }
        else if (alarmDetails.getDifficulty() == 1) {
            difficultyString = "MEDIUM";
        }
        else if (alarmDetails.getDifficulty() == 2) {
            difficultyString = "HARD";
        }

        TextView difficultySelection = (TextView) findViewById(R.id.difficulty_level);
        difficultySelection.setText(difficultyString);
    }

    public void setRepeatingDays() {
        updateTextColor((TextView) findViewById(R.id.alarm_item_sunday), alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_monday), alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_tuesday), alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_wednesday), alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_thursday), alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_friday), alarmDetails.getRepeatingDay(AlarmModel.FRIDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_saturday), alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));

    }
    private void updateTextColor(TextView view, boolean isOn) {
        if (isOn) {
            view.setTextColor(Color.GREEN);
        } else {
            view.setTextColor(Color.BLACK);
        }
    }
}
