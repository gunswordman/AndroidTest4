package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnStart,btnPause,btnStop;
    private SeekBar seekBar;
    private Myconn conn;
    private MusicPlayer.MyBinder music;
    private Intent intent;
    private int duration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylayout);
        initview();
        if(conn == null){
            conn=new Myconn();
        }
        intent=new Intent(this,MusicPlayer.class);
        bindService(intent,conn,BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        if(conn!=null){
            unbindService(conn);
            conn=null;
        }
        super.onDestroy();
    }

    private void initview(){
        btnStart=findViewById(R.id.btnStart);
        btnPause=findViewById(R.id.btnPause);
        btnStop=findViewById(R.id.btnStop);
        seekBar=findViewById(R.id.seekBar);

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拖到进度条之后
                try {
                    music.SeekTo(seekBar.getProgress());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                try {
                    music.Start();
                    seekBar.setMax(music.getDuration());
                    new Thread(){
                        @Override
                        public void run() {
                            while (true){
                                try {
                                    if(music.isCreate()){
                                        seekBar.setProgress(music.getCurrention());
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnStop:
                try {
                    music.Stop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnPause:
                try {
                    music.Pause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private class Myconn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            music= (MusicPlayer.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}