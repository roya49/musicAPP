package com.example.bottomnavigationview;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import java.io.IOException;

public class MusicService extends Service {

    public static final String  SINGLE="单曲循环";     //单曲
    public static final String  ALL="全部播放";        //全部
    public static final String  RANDOM="随机播放";     //随机
    private static MediaPlayer mediaPlayer;
    private static MusicBinder musicBinder;
    private static boolean isPause=false;
    private static String isPath="";
    private static String  mode="";
    private static int position;

    public MusicService() {
    }

    private static void play(String path){
        try {
            isPath=path;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (mode){
                        case SINGLE:
                            mediaPlayer.start();
                            break;
                        case ALL:
                            MusicFragment.playAll();
                            break;
                        case RANDOM:
                            MusicFragment.playRandom();
                            break;
                    }
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static class MusicBinder extends Binder {
        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }
        public void start(String path){
            if (isPath.equals(path)){
                if (isPlaying()&&!isPause){
                    pause();
                }else {
                    recovery();
                }
            }else {
                play(path);
            }
        }
        public void pause(){
            isPause=true;
            mediaPlayer.pause();
        }
        public void recovery(){
            isPause=false;
            mediaPlayer.start();
        }
        public void changeMode(){
            switch (mode){
                case SINGLE:
                    mode=ALL;
                    break;
                case ALL:
                    mode=RANDOM;
                    break;
                case RANDOM:
                    mode=SINGLE;
                    break;
            }
        }
        public void setMode(String Mode){
            switch (Mode){
                case SINGLE:
                    mode=SINGLE;
                    break;
                case ALL:
                    mode=ALL;
                    break;
                case RANDOM:
                    mode=RANDOM;
                    break;
            }
        }
        public int getProgress(){
            return mediaPlayer.getCurrentPosition();
        }
        public void toTime(int time){
            mediaPlayer.seekTo(time);
        }
        public int getAll(){
            return mediaPlayer.getDuration();
        }
        public void next(){
            switch (mode){
                case SINGLE:
                    mediaPlayer.start();
                    break;
                case ALL:
                    MusicFragment.playAll();
                    break;
                case RANDOM:
                    MusicFragment.playRandom();
                    break;
            }
        }
        public void setPos(int pos){
            position=pos;
        }
        public int getPos(){
            return position;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer= new MediaPlayer();
        musicBinder=new MusicBinder();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mode=SINGLE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }
}
