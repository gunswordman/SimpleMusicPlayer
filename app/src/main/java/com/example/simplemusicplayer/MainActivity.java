package com.example.simplemusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE_PROGRESS = 0;
    private ImageButton btnplay,btnnext,btnprevious;
    private TextView tV1,tV2,songnametv,singertv;
    private SeekBar seekBar;
    private ListView mylist;
    private MyAdapter myAdapter;

    private List<Song> list;

    private MusicPlayer.MyBinder myBinder;

    private Myconn conn;
    private Intent intent;

    private int duration;
    private int currention;
    private int index;

    private boolean isFirst=true;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_PROGRESS:
                    updateView();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();

        list=new ArrayList<>();
        list=Utils.getmusic(this);

        myAdapter=new MyAdapter(list,this);
        mylist.setAdapter(myAdapter);

        intent=new Intent(this,MusicPlayer.class);
        if(conn==null){
            conn=new Myconn();
        }
        bindService(intent,conn,BIND_AUTO_CREATE);
    }

    private void InitView() {
        btnplay=findViewById(R.id.play);
        btnnext=findViewById(R.id.next);
        btnprevious=findViewById(R.id.previous);
        seekBar=findViewById(R.id.seekbar);
        mylist=findViewById(R.id.mylist);
        tV1=findViewById(R.id.time1);
        tV2=findViewById(R.id.time2);
        songnametv=findViewById(R.id.songname);
        singertv=findViewById(R.id.singer);

        btnplay.setOnClickListener(this);
        btnnext.setOnClickListener(this);
        btnprevious.setOnClickListener(this);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index=position;
                isSelected();
                duration=myBinder.Play(position);
                isFirst=false;
                btnplay.setImageResource(android.R.drawable.ic_media_pause);
                updateText();
                updateView();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setMax(duration);
                myBinder.SeekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                if(isFirst){
                    duration=myBinder.Play(index);
                    seekBar.setMax(duration);
                    handler.sendEmptyMessage(UPDATE_PROGRESS);
                    btnplay.setImageResource(android.R.drawable.ic_media_pause);
                    singertv.setText(list.get(index).singer);
                    songnametv.setText(list.get(index).song);
                    isFirst=false;
                }else {
                    myBinder.PlayPause();
                    updateText();
                    updateIcon();
                }
                break;
            case R.id.previous:
                index-=1;
                if(index<0){
                    index+=list.size();
                }
                duration=myBinder.Play(index);
                updateText();
                isSelected();
                isFirst=false;
                btnplay.setImageResource(android.R.drawable.ic_media_pause);
                break;
            case R.id.next:
                index+=1;
                if(index>=list.size()){
                    index-=list.size();
                }
                duration=myBinder.Play(index);
                updateText();
                isSelected();
                isFirst=false;
                btnplay.setImageResource(android.R.drawable.ic_media_pause);
                break;
        }

    }


    private class Myconn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder= (MusicPlayer.MyBinder) service;
            myBinder.getList(list);
            updateText();
            updateIcon();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void updateIcon() {
        if(myBinder.isPlaying()){
            btnplay.setImageResource(android.R.drawable.ic_media_pause);
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        }else {
            btnplay.setImageResource(android.R.drawable.ic_media_play);
            handler.removeMessages(UPDATE_PROGRESS);
        }
    }

    private void updateText(){
        singertv.setText(list.get(index).singer);
        songnametv.setText(list.get(index).song);
    }

    private void updateView(){
        seekBar.setMax(duration);
        currention=myBinder.getCurrent();
        seekBar.setProgress(currention);
        String time1=Utils.formatTime(currention);
        String time2=Utils.formatTime(duration);
        tV1.setText(time1);
        tV2.setText(time2);
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS,500);
    }

    private void isSelected(){
        myAdapter.setSelectItem(index);
        myAdapter.notifyDataSetChanged();
    }

}