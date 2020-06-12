// Player.aidl
package com.example.musicplayer;

// Declare any non-default types here with import statements

interface Player {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void Start();
    void Pause();
    void SeekTo(int progress);
    void Stop();
    int getDuration();
    int getCurrention();
    boolean isCreate();
}
