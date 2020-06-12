package com.example.musicplayer;

interface Player {
    void Start();//播放
    void Pause();//暂停
    void SeekTo(int progress);//播放从progerss开始
    void Stop();//停止
    int getDuration();//获取音乐最大进度
    int getCurrention();//获取音乐当前播放进度
    boolean isCreate();//MediaPlayer是否被建立
}
