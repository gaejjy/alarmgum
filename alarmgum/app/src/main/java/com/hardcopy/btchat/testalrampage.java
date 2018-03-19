package com.hardcopy.btchat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hardcopy.btchat.utils.Logs;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by MoonJeein on 2015-11-22.
 */
public class testalrampage extends Activity {

    TextView resulttext;
    TextView productname;
    TextView devicename;
    Button closeButton;
    ImageView devicephoto;


    String mRingTone ="";
    MediaPlayer mMediaPlayer = null;
    Vibrator vibe = null;
    PowerManager.WakeLock wl = null;
    PowerManager pm;
    private NotificationManager nm = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testalramactivity);
        resulttext = (TextView) findViewById(R.id.receivedtext);
        productname =(TextView)findViewById(R.id.receivedproductname);
        devicename = (TextView)findViewById(R.id.receiveddevicename);
        closeButton = (Button)findViewById(R.id.finishibutton);
        devicephoto =(ImageView)findViewById(R.id.testalarmactivityimageview);


        pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) { // ��ũ���� ���� ���� ������ �Ҵ�
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AlramGum");
            wl.acquire();
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                     WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        }




        Intent intent=new Intent(this.getIntent());
        String s=intent.getStringExtra("inputval");
        int vibrate = getIntent().getIntExtra("vibeon", 0);
        String receiveproname = intent.getStringExtra("productname");
        String receivedevname = intent.getStringExtra("devicename");
        ////////////////////////////////
        Bitmap receiveddevpic = intent.getParcelableExtra("imagebyte");


        ////////////////////////////////
        productname.setText(receiveproname);
        devicename.setText(receivedevname);
        resulttext.setText(s);
       // devicephoto.setImageResource(R.drawable.doors);

        closeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaPlayer == null) {

                } else {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    nm.cancel(12345);
                }
                if (vibe == null) {

                } else {
                    vibe.cancel();
                    nm.cancel(12345);
                }

                nm.cancel(12345);
                finish();
            }
        });


        if (mRingTone == null || mRingTone.equals("")) {
            mRingTone = RingtoneManager.getValidRingtoneUri(this).toString();
            if (mRingTone == null) mRingTone = "";
        }

        showNotification(R.drawable.notification_image, "알람이 왔습니다!!", mRingTone, vibrate);

        clearAlarm();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }
    //
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager nm = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void showNotification(int statusBarIconID,
                                  String statusBatTextID, String ringtone, int vibrate){
        // Notification ��ü ����/����
        nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, alarmCancel.class);
        PendingIntent theappIntent = PendingIntent.getActivity(this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notifi;

        CharSequence from = "알람이 왔습니다.!";
        CharSequence message = "AlarmGum";

        notifi = new Notification.Builder(this)//안드로이드 최신버전을 위한 notification 생성자.
                .setContentTitle(from)
                .setContentText(message)
                .setTicker(null)
                .setSmallIcon(R.drawable.notification_image)
                .setContentIntent(theappIntent)
                .build();


        //����ڰ� ���� �� ���� ��� �︮���ϴ� FLAG��
//		notifi.flags |= Notification.FLAG_INSISTENT;

        playSound(Uri.parse(mRingTone));

        if (vibrate == 1) {	// ������ �����Ǿ� ������ ?
            vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {200, 2000, 100, 1700, 200, 2000, 100, 1700, 200, 2000, 100, 1700};          //  ������, ���� ���̴�.
            vibe.vibrate(pattern, 2);                                 // ������ �����ϰ� �ݺ�Ƚ���� ����  ���� 2�� ��� �ݺ��̴�.
        }





        nm.notify(12345, notifi);


    }

    private void playSound(Uri alert) {
        if (mMediaPlayer != null) return;

        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
            }
        } catch (IOException e) {

        }
    }

    private void clearAlarm() {
        Handler mHandler = new Handler();
		/*
		 Intro 화면을 보여주고, 실제 Main 화면으로 이동할 경우,
		 보통의 경우 Handler에 postDelayed(Runnable r , long delayMillis) 란 메서드를 활용 하는데요.
		 Handler를 사용한 방법 말고도 Timer와 TimerTask를 이용하여 화면을 이동 할 수 가 있습니다.
		 */
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run()   {
                if (mMediaPlayer != null) mMediaPlayer.stop();
                mMediaPlayer = null;
                //if (wl != null) wl.release();
                vibe.cancel();
                nm.cancel(12345);
                finish();
            }
        }, 200000);
    }



}
