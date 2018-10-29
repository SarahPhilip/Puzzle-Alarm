package com.sdsu.cs646.shameetha.alarmclocktest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class AlarmAlertActivity extends ActionBarActivity {

    private MediaPlayer mediaPlayer;
    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);

    private AlarmModel alarm;
    private puzzleQuestions mPuzzleQuestions;
    private Vibrator vibrator;

    private boolean alarmActive;
    private Uri tone;
    AlertDialog dialog;
    Button positiveButton;
    int numberOfQuestions;
    int difficulty;
    private boolean alarmVibrate;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Alarm");
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        setContentView(R.layout.activity_alert);

        String name = getIntent().getStringExtra(AlarmBroadcastReceiver.NAME);
        difficulty = getIntent().getIntExtra(AlarmBroadcastReceiver.DIFFICULTY, 0);
        numberOfQuestions = getIntent().getIntExtra(AlarmBroadcastReceiver.QUESTIONS, 1);
        numberOfQuestions *= 2;
        alarmVibrate = getIntent().getBooleanExtra(AlarmBroadcastReceiver.VIBRATE, false);
        this.setTitle(name);
        id = getIntent().getLongExtra(AlarmBroadcastReceiver.ID, -1);
        tone = Uri.parse(getIntent().getExtras().getString(AlarmBroadcastReceiver.TONE));
        Log.d("CCC","abc "+ tone);
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(getClass().getSimpleName(), "Incoming call: "
                                + incomingNumber);
                        try {
                            mediaPlayer.pause();
                        } catch (IllegalStateException e) {

                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(getClass().getSimpleName(), "Call State Idle");
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {

                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        startAlarm();
        final EditText input = new EditText(this);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
            switch (difficulty) {
                case 0:
                    mPuzzleQuestions = new puzzleQuestions(3);
                    break;
                case 1:
                    mPuzzleQuestions = new puzzleQuestions(4);
                    break;
                case 2:
                    mPuzzleQuestions = new puzzleQuestions(5);
                    break;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(AlarmAlertActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle(mPuzzleQuestions.toString());
            builder.setMessage("?");
            builder.setView(input);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    if (checkAnswer(input.getText().toString())) {
                        if (numberOfQuestions > 0) {
                            dialog.dismiss();
                            callQuestion();
                        } else {
                            dialog.dismiss();
                            alarmActive = false;
                            try {
                                mediaPlayer.stop();
                            } catch (IllegalStateException ILException) {
                            }
                            try {
                                mediaPlayer.release();
                            } catch (Exception e) {

                            }
                            alarm = dbHelper.getAlarm(id);
                            if (!alarm.repeatWeekly && !alarmRepeating())
                                alarm.isEnabled = false;
                            dbHelper.offAlarm(alarm);
                            setResult(RESULT_OK);
                            AlarmAlertActivity.this.finish();
                        }
                    } else {
                        dialog.dismiss();
                        callQuestion();
                    }
                }
            });
            builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    dialog.dismiss();
                    callQuestion();
                }
            });

            dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.setCancelable(false);
            input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    input.post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            });
            input.requestFocus();
            dialog.show();
            positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    Log.v("afterEditText", input.getText().toString());
                    checkAnswer(input.getText().toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });



    }
    @Override
    protected void onResume() {
        super.onResume();
        alarmActive = true;

    }

    private boolean alarmRepeating() {
        int numberOfRepeatingDays = 0;
        if (alarm.getRepeatingDay(alarm.SUNDAY)) {
            numberOfRepeatingDays++;
        }
        if (alarm.getRepeatingDay(alarm.MONDAY)) {
            numberOfRepeatingDays++;
        }
        if (alarm.getRepeatingDay(alarm.TUESDAY)) {
            numberOfRepeatingDays++;
        }
        if (alarm.getRepeatingDay(alarm.WEDNESDAY)) {
            numberOfRepeatingDays++;
        }
        if (alarm.getRepeatingDay(alarm.THURSDAY)) {
            numberOfRepeatingDays++;
        }
        if (alarm.getRepeatingDay(alarm.FRIDAY)) {
            numberOfRepeatingDays++;
        }
        if (alarm.getRepeatingDay(alarm.SATURDAY)) {
            numberOfRepeatingDays++;
        }
        if(numberOfRepeatingDays >= 2)
            return true;
        else
            return false;
    }

    private void startAlarm() {

            if (alarmVibrate) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = { 1000, 200, 200, 200 };
                vibrator.vibrate(pattern, 0);
            }
        mediaPlayer = new MediaPlayer();
        try {
            if(getIntent().getExtras().getString(AlarmBroadcastReceiver.TONE) == "")
                tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setVolume(1.0f, 1.0f);
//            mediaPlayer.setDataSource(this, notification);
            mediaPlayer.setDataSource(this,tone);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            mediaPlayer.release();
            alarmActive = false;
        }
    }

//    }

    @Override
    public void onBackPressed() {
        if (!alarmActive) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // StaticWakeLock.lockOff(this);
    }

    @Override
    protected void onDestroy() {
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.stop();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }
        super.onDestroy();
    }


    public boolean isAnswerCorrect(String answer) {
        Log.d("isAnswerCorrect", "isAnswerCorrect");
        boolean correct = false;
        try {
            correct = mPuzzleQuestions.getResult() == Float.parseFloat(answer);
        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return correct;
    }

    public boolean checkAnswer(String answer) {
        Log.d("checkAnswer","checkAnswer");
        if (!alarmActive)
            return false;
        if (isAnswerCorrect(answer)) {
            Log.d("correct", "correct");
            positiveButton.setEnabled(true);

            numberOfQuestions --;
            return true;
        }
        else {
            positiveButton.setEnabled(false);
            return false;
        }
    }

    public void callQuestion() {
        final EditText input = new EditText(this);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        switch (difficulty) {
            case 0:
                mPuzzleQuestions = new puzzleQuestions(3);
                break;
            case 1:
                mPuzzleQuestions = new puzzleQuestions(4);
                break;
            case 2:
                mPuzzleQuestions = new puzzleQuestions(5);
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmAlertActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(mPuzzleQuestions.toString());
        builder.setMessage("?");
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(checkAnswer(input.getText().toString())) {
                    if (numberOfQuestions > 0) {
                        dialog.dismiss();
                        callQuestion();
                    } else {
                        dialog.dismiss();
                        alarmActive = false;
                        try {
                            mediaPlayer.stop();
                        } catch (IllegalStateException ILException) {
                        }
                        try {
                            mediaPlayer.release();
                        } catch (Exception e) {

                        }
                        alarm = dbHelper.getAlarm(id);
                        alarm.isEnabled = false;
                        dbHelper.offAlarm(alarm);
                        setResult(RESULT_OK);
                        AlarmAlertActivity.this.finish();
                    }
                }
                else {
                    dialog.dismiss();
                    callQuestion();
                }
            }
        });
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                dialog.dismiss();
                callQuestion();
            }
        });

        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCancelable(false);
        dialog.show();
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.v("afterEditText", input.getText().toString());
                checkAnswer(input.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }
}
