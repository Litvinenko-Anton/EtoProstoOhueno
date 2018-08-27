package com.example.i74770k.etoprostoohueno

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import com.example.i74770k.etoprostoohueno.music.MusicService
import com.example.i74770k.etoprostoohueno.music.OnControlMusicService
import kotlinx.android.synthetic.main.activity_main_.*

class MainActivity : AppCompatActivity(), MusicService.OnChangePlayingListener {

    private var mPlayIntent: Intent? = null
    private var mServiceConnection: ServiceConnection? = null
    private var mMusicService: MusicService? = null
    private var mMusicControl: OnControlMusicService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_)
        initMusicService()
        childButton.setOnClickListener {
            mMusicControl?.onStartPlayingById(0)
        }
        parentButton.setOnClickListener {
            mMusicControl?.onStartPlayingById(1)
        }
    }

    private fun initMusicService() {
        mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as MusicService.MusicBinder
                mMusicService = binder.service //get service
                mMusicControl = mMusicService

                mMusicService?.let {
                    it.setOnChangePlayingListener(this@MainActivity)//set pass list
                    it.prepareSong(0)
                }
            }
            override fun onServiceDisconnected(name: ComponentName) {

            }
        }

        if (mPlayIntent == null) {
            mPlayIntent = Intent(this, MusicService::class.java)
            bindService(mPlayIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
            startService(mPlayIntent)
        }
    }

    override fun onResume() {
        mMusicControl?.onStartPlaying()
        super.onResume()
    }

    override fun onStop() {
        mMusicControl?.onPausePlaying()
        super.onStop()
    }

    override fun onDestroy() {
        mMusicControl?.onStopPlaying()
        stopService(Intent(this, MusicService::class.java))
        unbindService(mServiceConnection)
        super.onDestroy()
    }

    override fun onPlayingStarted(songID: Long) {

    }

    override fun onPlayingStopped(songID: Long) {

    }

    override fun onPlayingError(message: String?) {

    }
}
