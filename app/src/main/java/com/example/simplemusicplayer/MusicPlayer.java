package com.example.simplemusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;

public class MusicPlayer extends Service {
    private MediaPlayer music;
    private List<Song> list;
    private int curenti;


    public class MyBinder extends Binder{

        int Play(int i) {
            return MusicPlayer.this.play(i);
        }

        void PlayPause(){
            MusicPlayer.this.PlayPause();
        }

        void SeekTo(int i){
            MusicPlayer.this.seekto(i);
        }

        boolean isPlaying(){
            return MusicPlayer.this.isplaying();
        }

        int getCurrent(){
            return MusicPlayer.this.getcurrent();
        }

        void getList(List<Song> list) {
            MusicPlayer.this.getlist(list);
        }
    }

    private void getlist(List<Song> list) {
        this.list=list;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        music=new MediaPlayer();
        music.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onDestroy() {
        music.release();
        super.onDestroy();
    }

    private int play(int i)  {
        this.curenti=i;
        music.reset();
        try {
            music.setDataSource(list.get(curenti).path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AsyncPrepare();
        return list.get(curenti).duration;
    }

    //开始或暂停
    private void PlayPause(){
        if(music.isPlaying()){
            music.pause();
        }else {
            music.start();
        }
    }

    //异步prepare
    private void AsyncPrepare(){
        music.prepareAsync();
        music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }
    //seekto
    private void seekto(int i){
        music.seekTo(i);
    }
    //是否正在正在播放
    private boolean isplaying(){
        return music.isPlaying();
    }
    //获取播放进度
    private int getcurrent(){
        return music.getCurrentPosition();
    }
}
