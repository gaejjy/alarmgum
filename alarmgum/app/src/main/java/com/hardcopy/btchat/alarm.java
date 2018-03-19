package com.hardcopy.btchat;

/**
 * Created by MoonJeein on 2015-11-22.
 */
//http://androidhuman.tistory.com/246 이 사이트에는 액티비티 생명주기에 대해서 잘 설명해놓았다. 참고해서 읽어보면 좋을듯.

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


//여기는 자신이설정한 알람의 리스트를 보여주는 곳임  만약 추가버튼을 누른다면 alramset액티비티로 넘어가게 됨
public class alarm extends Activity {
    ImageButton imagebutton;


    private MyCursorAdapter adapter; //어댑터는 데이터를 사용자 인터페이스 뷰와 바인드하는 브리징 클래스
    private ListView list;
    private DBAdapter db; //db어댑터 클래스를 가져온다.
    private Cursor currentCursor;//안드로이드에서는 DB에서 가져온 데이터를 쉽게 처리하기 위해서 Cursor 라는 인터페이스를 제공
                                 //Cursor는 기본적으로 DB에서 값을 가져와서 마치 실제 Table의 한 행(Row), 한 행(Row) 을 참조하는 것 처럼 사용 할 수 있게 해 줍
                                 //개발자는 마치 그 행(Row) 을 가지고 행(Row)에 있는 데이터를 가져다가 쓰는 것 처럼 사용하면 되는 편의성을 제공받게 됩니다.

    //상수
    private static int colID; //알라마 이름
    private static int colONOFF;//알람 할지 알할지
    /*
    private static int colHOUR;
    private static int colMINUTE;
    private static int colDAY;
    */
    private static int colPRONAME; //버튼고유의 이름
    private static int colDEVNAME; //스마트폰 이름
    private static int colRING; //알람음을 위한 배경음 (여기서는 주소)
    private static int colVIB;//진동설정 on/off
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);//레이아웃 가져옴

        //////////
        //
        db = new DBAdapter(this);
        db.open();//데이터베이스를 연다.
        //
        currentCursor = db.fetchAllAlarm();
        //
        list = (ListView)findViewById(R.id.list);//리스트뷰를 등록하는 과정이고 리스트뷰의 아이템 터치기능등을 정의한다.
        list.setOnItemClickListener(itemClickListener);
        list.setOnTouchListener(TouchListener);

        String[] from = new String[] {DBAdapter.PRODUCT_NAME, DBAdapter.DEVICE_NAME}; //디비에 등록된 알람들의 요일


        int[] to = new int[] {R.id.alarm_product_name, R.id.alarm_device_name};//alram_row.xml 안에 다음과 같은 요소들이 들어있다. 에 들어있다.

        //
        String Productname;
        String Devicename;
        adapter = new MyCursorAdapter(list.getContext(), R.layout.alarm_row, currentCursor, from, to); //밑에 MyCursorAdapter 에 대해서 정의해노음.
        list.setAdapter(adapter);
        // column index
        colID = currentCursor.getColumnIndex("_id");
        colONOFF = currentCursor.getColumnIndex(DBAdapter.ALARM_ON);
        /*
        colDAY = currentCursor.getColumnIndex(DBAdapter.ALARM_APDAY);
        colHOUR = currentCursor.getColumnIndex(DBAdapter.ALARM_HOUR);
        colMINUTE = currentCursor.getColumnIndex(DBAdapter.ALARM_MINUTE);
        */
        colPRONAME = currentCursor.getColumnIndex(DBAdapter.PRODUCT_NAME);
        colDEVNAME = currentCursor.getColumnIndex(DBAdapter.DEVICE_NAME);
        colRING = currentCursor.getColumnIndex(DBAdapter.ALARM_RINGTONE);
        colVIB = currentCursor.getColumnIndex(DBAdapter.ALARM_VIBRATE);
        //
        //////////

        imagebutton = (ImageButton)findViewById(R.id.addAlarm);//이 이미지 버튼은 알람 새로 등록하고 싶을때의 이미지 버튼임.
        imagebutton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(alarm.this, alarmSet.class);
                startActivity(intent);

            }
        });

    }
    //
    @Override
    protected void onResume() {
        super.onResume();
        /* onResume()은 액티비티가 일시정지(Paused) 상태에서 복귀될 때 호출됩니다.
         즉, 이는 액티비티가 사용자와 상호작용을 하지 못하다가 다시 상호작용을 할 수 있게 되는 것을 의미합니다.
          */
        currentCursor = db.fetchAllAlarm();

        adapter.notifyDataSetChanged();
        /*Android에서 ListView의 데이터가 변경되면,

         Adapter의 notifyDataSetChanged() 메소드를 호출해서

        새로운 데이터를 ListView에 나타나도록 한다.
        */
    }

    @Override
    protected void onPause() {
        super.onPause();//액티비티 사용자와 상호작용 중단할때의 메서드다 .
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private static int QuickMenuEvent = 0;
    private static float CheckedColumn_x = 0;

    View.OnTouchListener TouchListener = new View.OnTouchListener() { //좌표값으로 리스트뷰의 아이템의 요소들 예: 삭제버튼 등을  누르기 위해서 사용하는 듯.
        @Override
        public boolean onTouch (View view, MotionEvent event) {//터치할때 어디눌렀는지 알아보려는 메서드를 재정의했다 리스트뷰 아이템에 있는 이미지의 클릭 여부를 따질때 씀
            // ���⼭ view �� ListItem �� �ƴ�  ����Ʈ ��ü��
            CheckedColumn_x = event.getX();
            //CheckedColumn_y = event.getY();
            QuickMenuEvent = event.getAction();

            return false;
        }
    };
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {//리스트뷰를 눌렀을때의 동작을 설명할때 쓰는 동작을 등록함.
        @Override
        public void onItemClick(AdapterView<?> list, View view, int position, long id) {
            currentCursor.moveToPosition(position);//커서가 클릭한 개체로 이동하게 된다.!
            ImageView icon_view = (ImageView)view.findViewById(R.id.toggleButton1);
            ImageView delete_view = (ImageView)view.findViewById(R.id.alarm_delete);


            //
            if (QuickMenuEvent == MotionEvent.ACTION_UP) {//QuickMenuEvent 위에 보면 이 이름으로 상수 등록되어있다.
                if (icon_view.getLeft() < CheckedColumn_x && CheckedColumn_x < icon_view.getRight()) {//alram on off 상태 체크한다 보아하니 이 이미지의 왼쪽과 오른쪽 사이에 들어가면 알람 온오프가 되는 것같다.
                    long db_id = currentCursor.getLong(colID);
                    int on = currentCursor.getInt(colONOFF);
                    //
                    if (on == 0) on = 1;
                    else on = 0;
                    //
                    db.modifyAlarmOn(db_id, on);//db에서 알람 onoff여부를 체크한다.
                    currentCursor = db.fetchAllAlarm();
                    adapter.notifyDataSetChanged();//이렇게 변경됬다고 등록해주어야지 어댑터가 알아차리고 갱신해주는 듯하다.
                    //
                    //	calendar = Calendar.getInstance();
                    if(on == 1){
                        icon_view.setImageResource(R.drawable.clock_on);
                        Toast.makeText(getBaseContext(), "알람이 설정되었습니다. "
                                ,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        icon_view.setImageResource(R.drawable.clock_off);
                        Toast.makeText(alarm.this, "알람이 꺼졌습니다.", Toast.LENGTH_SHORT).show();
                    }

                } else if (delete_view.getLeft() < CheckedColumn_x && CheckedColumn_x < delete_view.getRight()) {
                    new AlertDialog.Builder(alarm.this)//다이알로그 창이 뜨게 됩니다.
                            .setMessage("삭제하시겠습니까?")//삭제할거냐 하고 물어본다.
                            .setCancelable(false)
                            .setPositiveButton("삭제",//그렇다고
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            long db_id = currentCursor.getLong(colID);
                                            db.delAlarm("" + db_id);
                                            currentCursor = db.fetchAllAlarm();
                                            adapter.notifyDataSetChanged();
                                           // Utility.cancelAlarm(alarm.this); //삭제하면 Utility activity에서 무엇인가 동작을 한다. 그곳으로 가봐야 한다.
                                          //  Utility.startFirstAlarm(alarm.this);
                                        }
                                    })
                            .setNegativeButton("취소", null)
                            .show();
                } else{//걍 이 리스트뷰아이템을 클릭하면 해당알람의 수정 화면으로 넘어가게 된다.즉 alarmSet 으로 이동하게 된다.
                       // 그러면 클릭한 놈의 알람정보들을 가지고 세팅화면에 표시해줄것이다 . 그전에 그 액티비티에서 무엇을 할지 알아야 할 것이다.
                    long db_id = currentCursor.getLong(colID);

                    // 그 전에 수
                    Intent intent = new Intent(alarm.this, alarmSet.class);
                    intent.putExtra("id", db_id);//현재 클릭한 리스트뷰의 정보들을 가져가서 이동하게 된다.
                    /*
                    intent.putExtra("day", currentCursor.getInt(colDAY));
                    intent.putExtra("hour", currentCursor.getInt(colHOUR));
                    intent.putExtra("min", currentCursor.getInt(colMINUTE));
                    */
                    intent.putExtra("proname",currentCursor.getString(colPRONAME));
                    intent.putExtra("devname",currentCursor.getString(colDEVNAME));
                    intent.putExtra("ring", currentCursor.getString(colRING));
                    intent.putExtra("vib", currentCursor.getInt(colVIB));

                    startActivity(intent);//alramset으로 이동!!
                }

            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////
//list adapter
////////////////////////////////////////////////////////////////////////////////////
    private class MyCursorAdapter extends SimpleCursorAdapter {//���� ���? : �����ͺ��̽��� �ִ� ������ �����ؼ� ����Ʈ�� ���ε� �� �� SimpleCursorAdapter �� ����Ѵ�.
       /*데이터베이스에 저장 된 데이터를 커서어댑터를 통해 리스트뷰로 출력하는 프로그램을 만들어보도록 하겠습니다.
        데이터베이스에서 조회 한 데이터가 굉장히 많을 경우 쿼리로 전체데이터를 다 읽어 출력하려고 하면 굉장히 느릴 수 있습니다.
         따라서 조회결과를 가지고 있는 커서를 어댑터에 바인딩해 놓고 어댑터뷰로 출력합니다.
        그러면 한꺼번에 다 읽을필요 없이 어댑터는 꼭 필요한 데이터의 레코드만 읽어서 뷰에 출력하기 때문에 효율적입니다.
        [출처] [안드로이드 개발 강좌] 커서어댑터(CursorAdapter) - 리스트뷰(ListView) |작성자 키즈베어*/

        Context my_context;
        /*
        Context 는 어플리케이션과 관련된 정보에 접근하고자 하거나 어플리케이션과 연관된 시스템 레벨의 함수를 호출하고자 할 때 사용됩니다.
        그런데 안드로이드 시스템에서 어플리케이션 정보를 관리하고 있는 것은 시스템이 아닌, ActivityManagerService 라는 일종의 또 다른 어플리케이션입니다.
        따라서 다른 일반적은 플랫폼과는 달리, 안드로이드에서는 어플리케이션과 관련된 정보에 접근하고자 할때는 ActivityManagerService 를 통해야만 합니다.
        당연히 정보를 얻고자 하는 어플리케이션이 어떤 어플리케이션인지에 관한 키 값도 필요해집니다.
          즉, 안드로이드 플랫폼상에서의 관점으로 샆펴보면, Context 는 다음과 같은두 가지 역할을 수행하기 때문에 꼭 필요한 존재입니다.
                자신이 어떤 어플리케이션을 나타내고 있는지 알려주는 ID 역할
       ActivityManagerService 에 접근할 수 있도록 하는 통로 역할
         정리하자면 이렇습니다.


         */
        private int mRowLayout;

        MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) { // MyCursorAdapter생성자. 현재 컨텍스트와 디비에 있는 등록되어있는 알람들의 시간 초를 가져온다.
            super(context, layout, c, from, to);
            my_context = context;
            mRowLayout = layout;
        }

        @Override
        public int getCount() {
            return currentCursor.getCount();//커서가 참조 할 수 있는 해당 테이블의 행(Row)의 갯수를 얻어 옵니다.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            currentCursor.moveToPosition(position); ///////////////
            ViewHolder viewHolder;
            /*
            안드로이드에서 뷰 객체를 생성하는 과정은 크게 2가지가 있습니다.
            직접 코드상에서 아래와 같이 생성하는 방법이 있고
            Button b = new Button(this)   // this 는 Context 를 의미
            그리고 xml 파일을 통해서 객체를 생성하는 방법이 있습니다.

            인플레이트라는 것은 xml 파일을 통해서 객체화를 시키는 것을 말합니다.
             */

            if (convertView == null) {
                LayoutInflater inflater=(LayoutInflater)my_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(mRowLayout, parent, false);//XML file에 기술된 layout를 View object로 부풀리는(굳이 뜻을 갖다 붙이자니 억지스럽지만) 역할을 한다.
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView)convertView.findViewById(R.id.toggleButton1);
                viewHolder.productname = (TextView)convertView.findViewById(R.id.alarm_product_name);
                viewHolder.devicename = (TextView)convertView.findViewById(R.id.alarm_device_name);
                //
                convertView.setTag(viewHolder);
                //뷰클래스에서는 뷰에 태그를 붙일 수 있다!!!
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            //
            viewHolder.productname.setText(currentCursor.getString(colPRONAME));//뷰홀더의 요소중 텍스트형이 있으면 디비에서 가져온 값을 이용해서 설정

            /*
            int day = currentCursor.getInt(colDAY);
            String strDay="";
            if((day & 0x01) == 0x01){ strDay = "��"; }
            if((day & 0x02) == 0x02){ strDay += "��"; }
            if((day & 0x04) == 0x04){ strDay += "ȭ"; }
            if((day & 0x08) == 0x08){ strDay += "��"; }
            if((day & 0x10) == 0x10){ strDay += "��"; }
            if((day & 0x20) == 0x20){ strDay += "��"; }
            if((day & 0x40) == 0x40){ strDay += "��"; }
            */
            //
            viewHolder.devicename.setText(currentCursor.getString(colDEVNAME));
            //
            int on = currentCursor.getInt(colONOFF);//디비커서어댑터에서 알람온오프 여부를 가져와서 세팅!

            if(on == 1)	viewHolder.icon.setImageResource(R.drawable.clock_on);
            else viewHolder.icon.setImageResource(R.drawable.clock_off);
            //
            return convertView;
        }

        private class ViewHolder { //해당 리스트뷰에 들어갈 요소들을 정의해놓았다.
            ImageView icon;
            TextView productname;
            TextView devicename;
        };


    }

    /*
    public String getTimeString(int h, int m) {//달력에서 날짜 시간 받아온다 .우리들한테는 필요없는 기능이다.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        SimpleDateFormat dayformat = new SimpleDateFormat("HH:mm");
        dayformat.setCalendar(cal);
        Date date = cal.getTime();
        return dayformat.format(date);
    }
    */

}
