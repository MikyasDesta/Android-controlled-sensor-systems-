package com.example.suyash776.autobot;

import android.media.MediaPlayer;
import android.os.Message;
//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.os.Handler;
import android.widget.TextView;


public class Joystick extends Activity {

   // private static final String TAG = "Joystick";
   private static final String TAG = Joystick.TAG;
    public static final int MESSAGE_READ = 2;
    public static String packet = "";
    public static String acceleration_values = "";
    public EditText Box1 = null;
    EditText temprature, humidity , distance;
    private MediaPlayer mMediaPlayer = null;
    private SurfaceView videoViewer = null;
    private SurfaceHolder videoViewerHolder = null;
    private boolean preparingFlag;
    public String K="mine check";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothChatService.setHandler(jHandler);
        setContentView(R.layout.activity_joystick);
        Box1 = (EditText) findViewById(R.id.Box1);
        temprature = (EditText) findViewById(R.id.editText_temprature);
        humidity = (EditText) findViewById(R.id.editText_humidity);
        distance = (EditText) findViewById(R.id.editText_distance);
        Box1.setFocusable(false);
        Box1.setText("o");
        Log.e("damn", K);
        //videoViewer = (SurfaceView)findViewById(R.id.videoViewer);
        //videoViewer.setKeepScreenOn(true);
       // videoViewerHolder = videoViewer.getHolder();
       // videoViewerHolder.addCallback(holderCallback);
       // videoViewerHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final Button up_button = (Button) findViewById(R.id.up_Button);
       // final ImageView down_button = (ImageView) findViewById(R.id.down_Button);/*
      //  final ImageView left_button = (ImageView) findViewById(R.id.left_Button);
       // final ImageView right_button = (ImageView) findViewById(R.id.right_Button);*/
        final Button stop_button = (Button) findViewById(R.id.stop_Button);/*
        final ImageView upLeft_button = (ImageView) findViewById(R.id.upLeft_Button);
        final ImageView upRight_button = (ImageView) findViewById(R.id.upRight_Button);
        final ImageView downLeft_button = (ImageView) findViewById(R.id.downLeft_Button);
        final ImageView downRight_button = (ImageView) findViewById(R.id.downRight_Button);
        final ImageView clockRotate_button = (ImageView) findViewById(R.id.clockRotate_Button);
        final ImageView aClockRotate_button = (ImageView) findViewById(R.id.aClcokRotate_Button);*/

         up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "F";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });
      /*  down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "B";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });
        left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("lamn", K);
                String acceleration_values = "L";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });
        right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "TF";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });*/
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("omen", K);
                String acceleration_values = "TO";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });/*
        upLeft_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "2";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });
        upRight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "1";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });

        downLeft_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "3";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });

        downRight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "4";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });
        clockRotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "C";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });
        aClockRotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acceleration_values = "A";
                byte[] send = acceleration_values.getBytes();
                BluetoothChatService.write(send);
            }
        });*/

    }
/*
    private SurfaceHolder.Callback holderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startMediaPlayer();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }
    };

    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            preparingFlag = false;
            mMediaPlayer.reset();
            return false;
        }
    };*/
/*
    private void startMediaPlayer(){
        preparingFlag = true;
        try{
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource("http://192.168.0.102  :8160/");
            mMediaPlayer.setOnErrorListener(errorListener);
            mMediaPlayer.setOnPreparedListener(preparedListener);
            mMediaPlayer.setDisplay(videoViewerHolder);
            mMediaPlayer.prepareAsync();
        }catch(Exception e){}
    }

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            preparingFlag = false;
            android.util.Log.e("TrackingFlow", "Prepared Done");
            mMediaPlayer.start();
        }
    };
*/

   public String u="wello";
    public static String Temprature = "0.00";
    public static String humid = "0.00";
            public static String distnce = "0.00";
  //  Handler handler = new Handler();
    /*
    private final Handler jHandler = new Handler() {
       public String prt = "should work";
        public String submicky;

        @Override
        public void handleMessage(Message msg) {
            String data = "mic check";
            Log.e("gpamn", prt);
            switch (msg.what) {
                case MESSAGE_READ:
                   // Log.e("lastone: ", prt);
                  //  byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                  //  String readMessage = new String(writeBuf);
                  //  data += readMessage;


                    String readMessage = (String) msg.obj;
                    data+=readMessage;
                   // String K="mine check";
                    // Data input format is <rpmvalue1>,<rpmvalue2>^<rpmvalue3>@<batteryLevelB
                    Temprature = data.substring(data.indexOf("$")+1, data.indexOf("^"));
                    humid = data.substring(data.indexOf("&")+1, data.indexOf("@"));
                    distnce = data.substring(data.indexOf("~")+1, data.indexOf("!"));
                    //Log.d("Values: ", rpmvalue1 + " " + rpmvalue2 + " " + rpmvalue3 + " " + batteryLevel);
                  //  Log.e("damn", K);
                    //Log.e("Values: ", data);

                    Log.e("lastveryone", submicky);

                    Box1.setText(submicky);
                    temprature.setText("Temprature: "+Temprature+" F");
                    humidity.setText("Humidity: "+humid+" %");
                    distance.setText("Distance from obstacle: "+distnce+" cm");

                    data = "";
                    break;

            }
            Log.e("lastone", data);
        }
    };
    */

    private final Handler jHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String data = "";
            String temperatureS;
            String humidityS;
            String distanceS;
            String text;
            double temp;
            String Vint;
            double Vi;
            String Vp_n;
            double Vp;
            switch (msg.what) {
                case MESSAGE_READ:
                    // byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                   // data="";
                    Box1.setText("");
                    Log.d("COMPLETE_PACKET FINAL", packet);
                    String readMessage = (String) msg.obj;
                    data=readMessage;
                    Log.e("hossain",data);

                    if (data.contains("A")) {
                       // text = data.substring(1, data.indexOf("A"));
                        temperatureS = data.substring(data.indexOf("%")+1, data.indexOf("$"));
                       // temp =Double.valueOf(temperatureS).doubleValue();
                        humidityS = data.substring(data.indexOf("@")+1, data.indexOf("&"));
                       // distanceS = data.substring(data.indexOf("~")+1, data.indexOf("!"));
                        //Vi =Double.valueOf(Vint).doubleValue();
                       // Vp_n = data.substring(data.indexOf("&")+1, data.indexOf("B"));
                       // Vp =Double.valueOf(Vp_n).doubleValue();
                       temprature.setText(temperatureS+ "C");
                        humidity.setText(humidityS+ "%");
                        //distance.setText("Distance from the obstacle: "+distanceS);
                        distance.setText(" 34.89 cm");


                       // Box1.setText(text);

                    }

                    else{
                        Box1.setText(data);
                    }

                   // data = "";
                    break;

            }
        }
    };


    public class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, normalInterval);
                clickListener.onClick(downView);
                Log.e("aziz", u );
                String Touch_values ="a";
                    byte[]send = Touch_values.getBytes();
                    BluetoothChatService.write(send);

            }
        };

        private View downView;

        /***
         * @param initialInterval The interval after first click event
         * @param normalInterval The interval after second and subsequent click
         *       events
         * @param clickListener The OnClickListener, that will be called
         *       periodically
         **/
        public RepeatListener(int initialInterval, int normalInterval,
                              View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "Action Down");
                    Log.e("aziz1", u);
                  //  moving = true;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    downView = view;
                    clickListener.onClick(view);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "Action Up");
                   // moving = false;
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    Log.d(TAG, "Terminating runnable thread");
                    handler.removeCallbacks(handlerRunnable);
                    downView = null;
                    break;
            }
            return true;
        }

    }
}
