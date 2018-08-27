package com.example.i74770k.etoprostoohueno.music;

/**
 * Created by CherryPie on 14.08.2017
 */

public interface OnControlMusicService {
    void onStartPlaying();
    void onStartPlayingById(long songID);
    void onPausePlaying();
    void onStopPlaying();
    void onUpdatePlayList();
    void onUpdateSettings();
}
