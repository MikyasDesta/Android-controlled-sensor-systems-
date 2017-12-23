package com.example.suyash776.autobot;

//package com.doepiccoding.raspberrypicamerastream;

        import android.app.Activity;
        import android.media.MediaPlayer;
        import android.media.MediaPlayer.OnErrorListener;
        import android.media.MediaPlayer.OnPreparedListener;
        import android.os.Bundle;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.Window;

public class VideoStream extends Activity {

    private MediaPlayer mMediaPlayer = null;
    private SurfaceView videoViewer = null;
    private SurfaceHolder videoViewerHolder = null;
    private boolean preparingFlag;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_stream);

        videoViewer = (SurfaceView)findViewById(R.id.videoViewer);
        //videoViewer.setKeepScreenOn(true);
        videoViewerHolder = videoViewer.getHolder();
        videoViewerHolder.addCallback(holderCallback);
        videoViewerHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

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

    private OnErrorListener errorListener = new OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            preparingFlag = false;
            mMediaPlayer.reset();
            return false;
        }
    };

    private void startMediaPlayer(){
        preparingFlag = true;
        try{
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource("http://192.168.0.102:8160/");
            mMediaPlayer.setOnErrorListener(errorListener);
            mMediaPlayer.setOnPreparedListener(preparedListener);
            mMediaPlayer.setDisplay(videoViewerHolder);
            mMediaPlayer.prepareAsync();
        }catch(Exception e){}
    }

    private OnPreparedListener preparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            preparingFlag = false;
            android.util.Log.e("TrackingFlow", "Prepared Done");
            mMediaPlayer.start();
        }
    };

    protected void onResume() {
        super.onResume();
        if(mMediaPlayer == null && !preparingFlag){
            startMediaPlayer();
        }
    };

    protected void onPause() {
        super.onPause();
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    };
}
