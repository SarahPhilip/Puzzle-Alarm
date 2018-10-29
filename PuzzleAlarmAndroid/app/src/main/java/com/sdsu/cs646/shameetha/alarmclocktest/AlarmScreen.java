//package com.sdsu.cs646.shameetha.alarmclocktest;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Vibrator;
//import android.support.v7.app.ActionBarActivity;
//import android.telephony.PhoneStateListener;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//
//public class AlarmScreen extends ActionBarActivity implements View.OnClickListener {
//
//    private MediaPlayer mediaPlayer;
//
//    private StringBuilder answerBuilder = new StringBuilder();
//
//    private puzzleQuestions mQuestions;
//    private Vibrator vibrator;
//
//    private boolean alarmActive;
//
//    private TextView problemView;
//    private TextView answerView;
//    private String answerString;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle("Alarm");
//        final Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//
//        setContentView(R.layout.alarm_alert);
//
//        String name = getIntent().getStringExtra(AlarmBroadcastReceiver.NAME);
//        int difficulty = getIntent().getIntExtra(AlarmBroadcastReceiver.DIFFICULTY,0);
//
//        this.setTitle(name);
//
//        switch (difficulty) {
//            case 0:
//                mQuestions = new puzzleQuestions(3);
//                break;
//            case 1:
//                mQuestions = new puzzleQuestions(4);
//                break;
//            case 2:
//                mQuestions = new puzzleQuestions(5);
//                break;
//        }
//
//        answerString = String.valueOf(mQuestions.getResult());
//        if (answerString.endsWith(".0")) {
//            answerString = answerString.substring(0, answerString.length() - 2);
//        }
//
//        problemView = (TextView) findViewById(R.id.textView1);
//        problemView.setText(mQuestions.toString());
//        answerView = (TextView) findViewById(R.id.textView2);
//        answerView.setText("= ?");
//        TelephonyManager telephonyManager = (TelephonyManager) this
//                .getSystemService(Context.TELEPHONY_SERVICE);
//
//        PhoneStateListener phoneStateListener = new PhoneStateListener() {
//            @Override
//            public void onCallStateChanged(int state, String incomingNumber) {
//                switch (state) {
//                    case TelephonyManager.CALL_STATE_RINGING:
//                        Log.d(getClass().getSimpleName(), "Incoming call: "
//                                + incomingNumber);
//                        try {
//                            mediaPlayer.pause();
//                        } catch (IllegalStateException e) {
//
//                        }
//                        break;
//                    case TelephonyManager.CALL_STATE_IDLE:
//                        Log.d(getClass().getSimpleName(), "Call State Idle");
//                        try {
//                            mediaPlayer.start();
//                        } catch (IllegalStateException e) {
//
//                        }
//                        break;
//                }
//                super.onCallStateChanged(state, incomingNumber);
//            }
//        };
//
//        telephonyManager.listen(phoneStateListener,
//                PhoneStateListener.LISTEN_CALL_STATE);
//        startAlarm();
//
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        alarmActive = true;
//    }
//
//    private void startAlarm() {
//
////        if (alarm.getAlarmTonePath() != "") {
////            mediaPlayer = new MediaPlayer();
////            if (alarm.getVibrate()) {
////                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
////                long[] pattern = { 1000, 200, 200, 200 };
////                vibrator.vibrate(pattern, 0);
////            }
//        mediaPlayer = new MediaPlayer();
//            try {
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////                mPlayer.setDataSource(this, notification);
////                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
////                mPlayer.setLooping(true);
////                mPlayer.prepare();
////                mPlayer.start();
//                mediaPlayer.setVolume(1.0f, 1.0f);
////                mediaPlayer.setDataSource(this,
////                        Uri.parse(alarm.getAlarmTonePath()));
//                mediaPlayer.setDataSource(this, notification);
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//                mediaPlayer.setLooping(true);
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//
//            } catch (Exception e) {
//                mediaPlayer.release();
//                alarmActive = false;
//            }
//        }
//
////    }
//
//    @Override
//    public void onBackPressed() {
//        if (!alarmActive)
//            super.onBackPressed();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        StaticWakeLock.lockOff(this);
//    }
//
//    @Override
//    protected void onDestroy() {
//        try {
//            if (vibrator != null)
//                vibrator.cancel();
//        } catch (Exception e) {
//
//        }
//        try {
//            mediaPlayer.stop();
//        } catch (Exception e) {
//
//        }
//        try {
//            mediaPlayer.release();
//        } catch (Exception e) {
//
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (!alarmActive)
//            return;
//        String button = (String) v.getTag();
////		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//        if (button.equalsIgnoreCase("clear")) {
//            if (answerBuilder.length() > 0) {
//                answerBuilder.setLength(answerBuilder.length() - 1);
//                answerView.setText(answerBuilder.toString());
//            }
//        } else if (button.equalsIgnoreCase(".")) {
//            if (!answerBuilder.toString().contains(button)) {
//                if (answerBuilder.length() == 0)
//                    answerBuilder.append(0);
//                answerBuilder.append(button);
//                answerView.setText(answerBuilder.toString());
//            }
//        } else if (button.equalsIgnoreCase("-")) {
//            if (answerBuilder.length() == 0) {
//                answerBuilder.append(button);
//                answerView.setText(answerBuilder.toString());
//            }
//        } else {
//            answerBuilder.append(button);
//            answerView.setText(answerBuilder.toString());
//            if (isAnswerCorrect()) {
//                alarmActive = false;
//                if (vibrator != null)
//                    vibrator.cancel();
//                try {
//                    mediaPlayer.stop();
//                } catch (IllegalStateException ILException) {
//
//                }
//                try {
//                    mediaPlayer.release();
//                } catch (Exception e) {
//
//                }
//                this.finish();
//            }
//        }
//        if (answerView.getText().length() >= answerString.length()
//                && !isAnswerCorrect()) {
//            answerView.setTextColor(Color.RED);
//        } else {
//            answerView.setTextColor(Color.BLACK);
//        }
//    }
//
//    public boolean isAnswerCorrect() {
//        boolean correct = false;
//        try {
//            correct = mQuestions.getResult() == Float.parseFloat(answerBuilder
//                    .toString());
//        } catch (NumberFormatException e) {
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return correct;
//    }
//}
