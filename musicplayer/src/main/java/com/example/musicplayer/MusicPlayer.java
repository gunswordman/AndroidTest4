package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

import java.io.IOException;



public class MusicPlayer extends Service {
    private MediaPlayer music;

    private boolean isCreate=false;



    public class MyBinder extends Player.Stub {

        @Override
        public void Start() throws RemoteException {
            musicStart();
        }

        @Override
        public void Pause() throws RemoteException {
            musicPause();
        }

        @Override
        public  void SeekTo(int progress) throws RemoteException{
            dragSeekBar(progress);
        }

        @Override
        public void Stop() throws RemoteException{
            try {
                musicStop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getDuration() throws RemoteException {
            return musicDuartion();
        }

        @Override
        public int getCurrention() throws RemoteException {
            return musicCurrention();
        }

        @Override
        public boolean isCreate() throws RemoteException {
            return isCreate;
        }


    }
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(isCreate==false){
            music=MediaPlayer.create(this,R.raw.time);
            isCreate=true;
        }
        musicComplete();
    }

    @Override
    public void onDestroy() {
        music.stop();
        music.release();
        super.onDestroy();
    }

    //播放音乐
    private void musicStart(){
        if(!music.isPlaying()){
            music.start();
        }
    }
    //暂停音乐
    private void musicPause(){
        if(music.isPlaying()){
            music.pause();
        }
    }
    //停止音乐
    private void musicStop() throws IOException {
        if(music.isPlaying()){
            music.stop();
            music.prepare();
        }
    }
    //播放进度调变化后的音乐
    private void dragSeekBar(int progerss){
        music.seekTo(progerss);
    }

    //获取duration
    private int musicDuartion(){
        return music.getDuration();
    }

    //获取实时状态
    private int musicCurrention(){
        int currention=music.getCurrentPosition();
        return currention;
    }

    //监听音乐是否播放完毕
    private void musicComplete(){
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                music.seekTo(0);
                music.start();
            }
        });
    }
}
