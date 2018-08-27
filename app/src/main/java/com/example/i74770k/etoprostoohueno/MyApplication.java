package com.example.i74770k.etoprostoohueno;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static String deviceId = "";
    private static String versionName = "";
    private static int versionCode;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initAppVersion();
        initAllLullabiesList();
    }

    private void initAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getAppVersionName() {
        if (versionName == null)
            return "";
        else
            return new String(versionName);
    }


    public static int getAppVersionCode() {
            return versionCode;
    }

    private void initAllLullabiesList() {
        List<Song> allLullabiesList = new ArrayList<Song>() {{
//            add(new Song(0, "Eto Prosto Ohueno", "eto_prosto_ohueno.mp3", true));//TODO TEST
//            add(new Song(1, "Idi nahiy", "idi_nahiy.mp3", true));
            add(new Song(0, "Eto Prosto Ohueno", "twinkle.mp3", true));
            add(new Song(1, "Idi nahiy", "lullaby.mp3", true));
        }};
        GlobalData.getInstance().setAllLullabiesList(allLullabiesList);
    }

    public static Context getAppContext() {
        return context;
    }

    public static String getAppDeviceId() {
        if (deviceId.isEmpty())
            deviceId = Settings.Secure.getString(getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceId == null)
            return "";
        else
            return deviceId;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static int mapValueLevel(int inValue, int inMin, int inMax, int outMin, int outMax, int step) {
        return ((inValue - inMin) * (outMax - outMin) / (inMax - inMin) + outMin) * step;
    }

    public static final String TAG = "DEBUG ";


    public static void logD(@Nullable String message) {
        logD(TAG, message);
    }

    public static void logD(@NonNull String TAG, @Nullable String massage) {
        if (BuildConfig.DEBUG && TAG != null)
            if (massage == null)
                Log.d(TAG, "massage == null");
            else
                Log.d(TAG, massage);
    }

    public static void toastD(String messageBody) {
        if (messageBody != null && !messageBody.isEmpty() && context != null)
            Toast.makeText(context, messageBody, Toast.LENGTH_LONG).show();
    }

}
