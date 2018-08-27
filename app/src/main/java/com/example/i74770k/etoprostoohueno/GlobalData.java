package com.example.i74770k.etoprostoohueno;

import java.util.List;

/**
 * SingletonData
 *
 * Use:
 * GlobalData.getInstance().setLoginState(true); // SET state
 * boolean loginState = GlobalData.getInstance().getLoginState(); // GET state
 *
 * Examples:
 *
 * if (GlobalData.getInstance().getLoginState()) {
 * startMainActivity();
 * } else {
 * startLogInActivity();
 * }
 *
 * userNameTextView.setText(GlobalData.getInstance().getUserData().getTitle());
 */

public class GlobalData {


    private GlobalData() {

    }


    private static class SingletonHelper {
        private static final GlobalData INSTANCE = new GlobalData();
    }

    public static GlobalData getInstance() {
        return SingletonHelper.INSTANCE;
    }


    /**
     * Global Data: Getters/Setters
     */


    private List<Song> mAllLullabiesList;

    public void setAllLullabiesList(List<Song> allLullabiesList) {
        mAllLullabiesList = allLullabiesList;
    }

    public List<Song> getAllLullabiesList() {
        return mAllLullabiesList;
    }

    public Song getLullabiesFromAllLullabiesList(Song mySong) {
        return getLullabiesFromAllLullabiesList(mySong.getID());
    }

    public Song getLullabiesFromAllLullabiesList(long songID) {
        for (Song song : mAllLullabiesList) {
            if(song.getID() == songID)
                return song;
        }
        return null;
    }
}
