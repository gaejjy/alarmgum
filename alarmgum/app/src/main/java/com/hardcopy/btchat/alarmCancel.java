package com.hardcopy.btchat;

/**
 * Created by MoonJeein on 2015-12-16.
 */


import java.util.Calendar;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class alarmCancel extends Activity {

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
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_cancel);
        resulttext = (TextView) findViewById(R.id.receivedtext);
        productname =(TextView)findViewById(R.id.receivedproductname);
        devicename = (TextView)findViewById(R.id.receiveddevicename);
        closeButton = (Button)findViewById(R.id.finishibutton);
        devicephoto =(ImageView)findViewById(R.id.testalarmactivityimageview);
        ////////////////////////////////////////
        Intent intent=new Intent(this.getIntent());
        byte[]  receiveddevpic = intent.getByteArrayExtra("imagebyte");
        Bitmap bitmap= BitmapFactory.decodeByteArray(receiveddevpic, 0, receiveddevpic.length);
        devicephoto.setImageBitmap(bitmap);






        /////////////////////////////////////

        closeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();
                if(mMediaPlayer == null ){

                } else {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    nm.cancel(1234);
                }
                if(vibe == null){

                } else{
                    vibe.cancel();
                    nm.cancel(1234);
                }
                nm.cancel(1234);
                finish();
            }
        });
        //Toast.makeText(context, R.string.app_name, Toast.LENGTH_SHORT).show();

        //int vibrate = getIntent().getIntExtra("vibrate", 0);

    }

    @Override
    public void onDestroy() {
		 /*
		 onDestroy 라는 API가 있다. Activity 가 종료될 때 호출되는 콜백 API 이다.
		 구글 문서를 보면, Activity 종료 시 마지막으로 호출되는 함수 임으로, 사용하지 않는 자원을 모두 해제해야함.
		  */

        NotificationManager nm = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);

        //getSystemService()메서드는 manager접미어가 붙는 관리 매니저 객체를 반환한다.
        //.NOTIFICATION_SERVICE는 백그라운드 이벤트를 알려준다.
        nm.cancelAll();//노티피케이션 바에 뜬 알림 을 지우게 해준다.
        super.onDestroy();
    }


    @Override
    protected void onResume() { //액티비티가 재개될때 뜨는 메서드이다.
        super.onResume();
        Toast.makeText(getBaseContext(), "알람캔슬로 넘어옴.", Toast.LENGTH_LONG).show();
    }
    ////////////////////////////////////////////////////////////////////////////////



}
