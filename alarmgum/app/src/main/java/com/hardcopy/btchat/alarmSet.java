package com.hardcopy.btchat;


//정식 알람등록에서 알람 추가버튼을 누르면 실행되는 액티비티이다.

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.content.DialogInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class alarmSet extends Activity {

	EditText productnameedittext;
	EditText devicenameedittext;
	TextView productnametextview;
	TextView Devicenametextview;
	ImageView devicephoto;
	// 알람 관련 멤버 변수 ////
	//ringtone 변수
	   private String strRingTone = "";

	   private String mDeviceName;
	   private String mProductName;
	//진동
		private int mVibrate = 0;


	    private long db_id = -1;
	///////////////////// notification member value(통지 관련 멤버 변수) ///////////////////////
		private NotificationManager mNotification;
	/////////////////////////////////////////////////사진기능 관련
	private Uri cropedImageUri;
	private Button photobutton;
	private static final int CAMERA_REQUEST = 1888;
	Bitmap photo;
	///////////////////////////////////////////
		
		
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);

			//통지 매니저를 흭득
			mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

			setContentView(R.layout.alarmset);
			//
		    productnametextview = (TextView)findViewById(R.id.product_name_textview);
			Devicenametextview= (TextView)findViewById(R.id.devicenametextview);
			productnameedittext =(EditText)findViewById(R.id.product_name_edittext);
			devicenameedittext =(EditText)findViewById(R.id.devicenameedittext);
			devicephoto =(ImageView)findViewById(R.id.itemphoto);

			((TextView) findViewById(R.id.ringtone)).setOnClickListener(ringSelectBtnListener);

			//셋 버튼을 등록 (확인 버튼)
			Button button = (Button)findViewById(R.id.set);
			button.setOnClickListener(new OnClickListener(){
				
				public void onClick(View v) {
					setAlarm();
				}
			});

			/////////////////////////////////////카메라 관련 기능
			cropedImageUri = null;


			photobutton = (Button)findViewById(R.id.setphoto);
			photobutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, CAMERA_REQUEST);
				}
			});
            //////////////////////////////////////////

			//진동모드나 사일런트 모드일 때도 벨 울리게
			AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT //://: 사일런트 모드일 경우(값0)
					|| mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){ //://: 진동모드일 경우(값1))
				int maxVolume =  mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

			}
			
			//mTime.setOnTimeChangedListener(timeChangedListeners);


			
			db_id = getIntent().getLongExtra("id", -1);// alarm 액티비티에서 보낼때 같이 보낸 값으로 확인 만약 원래 있던 알람을  수정하는 것이라면 alarm 액티비티에서 보낼때 같이 보낸 값으로 확인
			if(db_id != -1){
				mDeviceName = getIntent().getStringExtra("devname");
				mProductName = getIntent().getStringExtra("proname");
				mVibrate = getIntent().getIntExtra("vib", 0);
				strRingTone = getIntent().getStringExtra("ring");

				// 시간설정


				//진동
				if (mVibrate == 0) {
		        	((CheckBox)findViewById(R.id.alarm_set_vibrate)).setChecked(false);
		        } else {
		        	((CheckBox)findViewById(R.id.alarm_set_vibrate)).setChecked(true);
		        }
				// ring tone
				if (strRingTone != null) {
					showRingTone(Uri.parse(strRingTone));//밑에 링톤 고르는부분 나오는다.
				}

			}

		}

/////////////////////////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////

		protected void onDestroy() {
	    	super.onDestroy();
	    }



		private void setAlarm() {
			// TODO Auto-generated method stub

			//진동 설정
			if (((CheckBox)findViewById(R.id.alarm_set_vibrate)).isChecked()) {
	    		mVibrate = 1;
	    	} else{
	    		mVibrate = 0;
	    	}
			//////////////////요일////////////////


			mDeviceName = devicenameedittext.getText().toString();
			mProductName = productnameedittext.getText().toString();
			if (mDeviceName==""||mProductName=="")
			{	   
				Toast.makeText(getBaseContext(), "이름을 설정하지 않으셨습니다.", Toast.LENGTH_LONG).show();
				return;
			}
			//


			///// db에 저장 /////
	        DBAdapter db = new DBAdapter(alarmSet.this);
	        db.open();
			// 알람 저장
			//db id가 -1이면 새로 설정 아니면 기존에 있던것을 수정 이건 알람 액티비티에서 가져온 값으로 판단한다!
	        if (db_id == -1) {
	        	db.addAlarm(1, mProductName, mDeviceName, mVibrate, strRingTone);
	        } else {
	        	db.modifyAlarm(db_id, 1, mProductName, mDeviceName, mVibrate, strRingTone);
	        }
	        
	        db.close();
			////////////////////////////////////////
			///////////////////////////////////////
			///imagedb에 저장

			Imagehelper help=new Imagehelper(alarmSet.this);
			help.open();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			help.insert(byteArray,mDeviceName);
			Toast.makeText(getBaseContext(), "이미지설정완료.", Toast.LENGTH_LONG).show();
            help.close();


			////////////////////////////////////

			finish();

		}


	/*
	ringtone 은 실제 전화벨소리, 알림소리, 알람소리 타입들이 있습니다.

다음같이 하시면 알림 사운드 리스트를 가져올 수 있구요.
RingtoneManager rm = new RingtoneManager(this);
rm.setType(RingtoneManager.TYPE_NOTIFICATION);
cursor = rm.getCursor();

다음같이 하시면 설정된 알림사운드를 가져와서 재생할 수 있습니다.
Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_NOTIFICATION);
Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
ringtone.play();
	 */
	// Ringtone Manager 에게 선택 메세지 전송
	    private OnClickListener ringSelectBtnListener = new OnClickListener() {
	    	
			public void onClick(View v) {
	            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
	            startActivityForResult(intent, 123);
	        }
	    };
	//ringtone manager 벨소리 선택 메니져
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {

			if (requestCode == 123 && resultCode == RESULT_OK) {
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (uri != null) {
					strRingTone = uri.toString();
					// ringtone 이름을 표시
					showRingTone(uri);
				}
			}

			if (requestCode == CAMERA_REQUEST) {
				photo = (Bitmap) data.getExtras().get("data");
				devicephoto.setImageBitmap(photo);

			}
		}
		
		private void showRingTone(Uri uri) {
			Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
			if (ringtone != null) {
				String value = "기본벨소리" + "\n" +  ringtone.getTitle(this);
				SpannableStringBuilder ssb = new SpannableStringBuilder();
				ssb.append(value);
				ssb.setSpan(new ForegroundColorSpan(0xFFf4A460), 3, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				((TextView)findViewById(R.id.ringtone)).setText(ssb);
			} else {
			}
			
		}

		public void onReceive(int err) {
			// TODO Auto-generated method stub
			
		}	
}



