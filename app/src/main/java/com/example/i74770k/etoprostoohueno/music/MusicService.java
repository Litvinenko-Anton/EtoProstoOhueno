package com.example.i74770k.etoprostoohueno.music;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.example.i74770k.etoprostoohueno.Song;

import java.util.ArrayList;
import java.util.List;

import static com.example.i74770k.etoprostoohueno.MyApplication.logD;
import static com.example.i74770k.etoprostoohueno.MyApplication.toastD;


/**
 * Created by CherryPie on 14.08.2017
 * https://github.com/SueSmith/android-music-player/tree/master/src/com/example/musicplayer
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        OnControlMusicService {

    private long startPlayerTime;
    private MediaPlayer player;
    private List<Song> songsList;
    private Song selectedSong;
    private OnChangePlayingListener callback;
    private final IBinder musicBind = new MusicBinder();//binder
    private boolean playingSong = false;


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer(); //create player
//        songsList = GlobalData.getInstance().getAllLullabiesList();

        initMusicPlayer();
        initLullabiesList(); //initialize
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void initLullabiesList() {
        songsList = new ArrayList<Song>() {{
            add(new Song(0, "Ahuenno", "ahuenno.mp3", true));
            add(new Song(1, "Nahui", "nahui.mp3", true));
        }};
//            songsList = GlobalData.getInstance().getAllLullabiesList();
    }

    public void setOnChangePlayingListener(OnChangePlayingListener callback) {
        this.callback = callback;
    }

    public boolean removeOnChangePlayingListener(OnChangePlayingListener callback) {
        if (this.callback != null && this.callback == callback) {
            this.callback = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onStartPlaying() {
        startPlayerTime = System.currentTimeMillis();
        startPlayer();
    }

    @Override
    public void onStartPlayingById(long songID) {
        playingSong = true;
        prepareSongById(songID);
    }

    @Override
    public void onPausePlaying() {
        pausePlayer();
    }

    @Override
    public void onStopPlaying() {
        stopPlayer();
    }

    @Override
    public void onUpdatePlayList() {
        if (!Song.containsSong(getCurrentSong(), songsList)) //TODO ???
            playNext();
    }

    @Override
    public void onUpdateSettings() {

    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        stopPlayer();
        return false;
    }


    private void startPlayer() {
        playingSong = true;
        if (!player.isPlaying()) {
            logD("MusicService startPlayer");
            prepareSongById(selectedSong.getID());
            player.start();
            if (callback != null)
                callback.onPlayingStarted(selectedSong.getID());
        }
    }

    private void pausePlayer() {
        playingSong = false;
        if (player.isPlaying()) {
            logD("MusicService pausePlayer");
            if (player.isPlaying())
                player.pause();
            if (callback != null)
                callback.onPlayingStopped(selectedSong.getID());
        }
    }

    private void stopPlayer() {
        playingSong = false;
        if (player != null) {
            logD("MusicService stopPlayer");
//            if (player.isPlaying())
//                player.stop();
            player.release();
            if (callback != null)
                callback.onPlayingStopped(selectedSong.getID());
        }
    }

    public void prepareSongById(long songId) {
        player.reset();
        prepareSong(getCurrentSongIndexById(songId));
    }

    public void prepareSong(int songIndex) {
        logD("MusicService prepareSong, songIndex:" + songIndex);
        //songsList = GlobalData.getInstance().getUserData().getMyLullabiesList();
        if (!songsList.isEmpty()) {
            selectedSong = songsList.get(checkSongIndex(songIndex)); //get song

            String folder = (selectedSong.isLullabiesType()) ? "lullabies/" : "whitenoise/";
            AssetFileDescriptor descriptor;

            try {
                descriptor = getAssets().openFd("sounds/" + folder + selectedSong.getFileName());
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
            } catch (Exception e) {
                logD("MusicService - Error setting data source");
                e.printStackTrace();
            }

            try {
                player.prepare();
            } catch (Exception e) {
                toastD("player.prepare() Exception");
                logD("player.prepare() IOException, start player.prepareAsync()");
                logD("player.prepare(): " + e.getMessage());
                if (player != null)
                    player.prepareAsync();   //start in callback -> OnPreparedListener
                e.printStackTrace();
            }
        }
    }

    private int checkSongIndex(int songIndex) {
        if (songIndex >= 0 && songIndex < songsList.size()) {
            return songIndex;
        } else {
            return 0;
        }
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        logD("MusicService onPrepared");
        if (playingSong)  //start playback
            startPlayer();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        logD("MusicService onCompletion " + player.getCurrentPosition());
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        logD("MusicService Playback Error");
        mp.reset();
        if (callback != null)
            callback.onPlayingError("Playback Error");
        return false;
    }

    public int getCurrentSongIndex() {
        if (songsList != null && songsList.contains(selectedSong))
            return songsList.indexOf(selectedSong);
        else
            return 0;
    }

    public Song getCurrentSong() {
        if (selectedSong == null)
            if (songsList != null && !songsList.isEmpty())
                songsList.get(0);
            else
                return null;
        return selectedSong;
    }

    private boolean isPlayingSong() {
        return playingSong;
    }

    private void playNext() {
        logD("MusicService playNext");
        player.reset();
        int nextSongIndex = getNextSongIndex(selectedSong);
        playNextOnce(nextSongIndex);
    }

    private void playNextOnce(int nextSongIndex) {
        logD("MusicService playNextOnce");
        if (nextSongIndex < songsList.size())
            prepareSong(nextSongIndex);
    }

    private void playNextEveryTime(int nextSongIndex) {
        logD("MusicService playNextEveryTime");
        prepareSong(checkSongIndex(nextSongIndex));
    }

    private void playNextOnTimer(int nextSongIndex, long playingTime) {
        logD("MusicService playNextOnTimer");
        if ((System.currentTimeMillis() - startPlayerTime) < playingTime) // Check user playingTime
            prepareSong(checkSongIndex(nextSongIndex));
    }


    private int getNextSongIndex(Song selectedSong) {
        if (songsList != null && Song.containsSong(selectedSong, songsList))
            return (Song.indexOfSong(selectedSong, songsList) + 1);
        else
            return 0;
    }

    private int getCurrentSongIndexById(long songId) {
        return Song.indexOfSong(songId, songsList);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (callback != null)
            callback.onPlayingStopped(selectedSong.getID());
        super.onDestroy();
    }

    public List<Song> getSongsList() {
        return songsList;
    }

    /**
     * OnChangePlayingListener
     */

    public interface OnChangePlayingListener {
        void onPlayingStarted(long songID);

        void onPlayingStopped(long songID);

        void onPlayingError(String message);
    }

}
