package com.example.i74770k.etoprostoohueno;

import android.support.annotation.Nullable;

import java.util.List;

public class Song {

    private long id;
    private String title;
    private String fileName;
    private boolean lullabiesType;
    private boolean inPlaylist = false;

    public Song(String title) {
        new Song(System.currentTimeMillis(), title);
    }

    public Song(long id, String title) {
        new Song(id, title, null, false);
    }

    public Song(long id, String title, String fileName, boolean lullabiesType) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.lullabiesType = lullabiesType;
    }


    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isLullabiesType() {
        return lullabiesType;
    }

    public void setLullabiesType(boolean isLullabiesType) {
        this.lullabiesType = isLullabiesType;
    }

    public boolean isInPlaylist() {
        return inPlaylist;
    }

    public void setToPlaylist(boolean inPlaylist) {
        this.inPlaylist = inPlaylist;
    }

    public void changeStateInPlaylist() {
        inPlaylist = !inPlaylist;
    }

    public static boolean containsSong(@Nullable Song song, @Nullable List<Song> listSongs) {
        return song != null && listSongs != null && containsSong(song.getID(), listSongs);
    }

    public static boolean containsSong(@Nullable Long songID, @Nullable List<Song> listSongs) {
        if (songID != null && listSongs != null)
            for (Song listSong : listSongs) {
                if (songID == listSong.getID())
                    return true;
            }
        return false;
    }

    public static int indexOfSong(@Nullable Song song, @Nullable List<Song> listSongs) {
        if (song != null)
            return indexOfSong(song.getID(), listSongs);
        else
            return -1;
    }

    public static int indexOfSong(long songID, @Nullable List<Song> listSongs) {
        if (songID >= 0 && listSongs != null)
            for (int i = 0; i < listSongs.size(); i++) {
                if (songID == listSongs.get(i).getID())
                    return i;
            }
        return -1; // TODO maybe return 0?
    }

    public static boolean checkedSongsFromMyList(@Nullable List<Song> mySongsList, @Nullable List<Song> allSongsList) {
        return checkedSongsFromMyList(mySongsList, allSongsList, false);
    }

    public static boolean checkedSongsFromMyList(@Nullable List<Song> mySongsList, @Nullable List<Song> allSongsList, boolean disableOldListState) {
        if (mySongsList != null && allSongsList != null) {
            if (disableOldListState)
                for (Song song : allSongsList) {
                    song.setToPlaylist(false);
                }

            for (Song mySong : mySongsList) {
                for (Song song : allSongsList) {
                    if (mySong.getID() == song.getID())
                        song.setToPlaylist(true);
                }
            }
            return true;
        }
        return false;
    }

    public static Song getSongById(long songID, List<Song> allLullabiesList) {
        return allLullabiesList.get(indexOfSong(songID, allLullabiesList));
    }
}
