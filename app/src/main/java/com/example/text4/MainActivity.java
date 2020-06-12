package com.example.text4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.musicplayer.Player;

import static android.widget.SeekBar.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnStart,btnPause,btnStop;//按钮
    private SeekBar seekBar;//seekbar
    private Intent intent;
    private Myconn conn;
    private Player music;
    private boolean seekbarChanging=false;//拖动Seekbar时，不允许更新进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylayout);

        btnStart=findViewById(R.id.btnStart);
        btnPause=findViewById(R.id.btnPause);
        btnStop=findViewById(R.id.btnStop);
        seekBar=findViewById(R.id.seekBar);

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动进度条时，即“按下”动作
                seekbarChanging=true;//按下进度条之后，seekBar无法被程序更新
                System.out.println("1");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拖到进度条之后，即“松开”动作
                try {
                    if(music.isCreate()){
                        music.SeekTo(seekBar.getProgress());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                seekbarChanging=false;//松开之后，进度条可以被更新
                System.out.println("2");
            }
        });

        if(conn==null){
            conn=new Myconn();
        }
        intent=new Intent();
        //连接到service
        intent.setClassName("com.example.musicplayer","com.example.musicplayer.MusicPlayer");
        //绑定服务
        bindService(intent,conn,BIND_AUTO_CREATE);
    }



    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                try {
                    music.Start();//调用服务内Mybinder的函数，开始播放
                    seekBar.setMax(music.getDuration());//设置进度条最大值为歌曲最大值
                    new Thread(){//使用一个线程，来更新SeekBar，显示歌曲播放的位置
                        @Override
                        public void run() {
                            try {
                                if(music.isCreate()){
                                    while(true){
                                        if(seekbarChanging==false){
                                            seekBar.setProgress(music.getCurrention());
                                        }
                                    }
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
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
            case R.id.btnStop:
                try {
                    music.Stop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private class Myconn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            music= Player.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            music=null;
        }
    }
}