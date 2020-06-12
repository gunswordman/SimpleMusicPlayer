package com.example.simplemusicplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<Song> list;
    private Context context;

    private int selectItem = -1;

    public MyAdapter(List<Song> list,Context context){
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Myholder myholder;
        if(convertView==null){
            //使用自定义的布局文件为Layout
            convertView= LayoutInflater.from(context).inflate(R.layout.text,null);
            //减少findView次数
            myholder=new Myholder();
            //初始化布局中的元素
            myholder.t_position = convertView.findViewById(R.id.t_postion);
            myholder.t_song = convertView.findViewById(R.id.t_song);
            myholder.t_singer = convertView.findViewById(R.id.t_singer);
            myholder.t_duration = convertView.findViewById(R.id.t_duration);

            convertView.setTag(myholder);
        }else {
            myholder=(Myholder)convertView.getTag();
        }

        myholder.t_song.setText(list.get(position).song.toString());
        myholder.t_singer.setText(list.get(position).singer.toString());
        String time = Utils.formatTime(list.get(position).duration);

        myholder.t_duration.setText(time);
        myholder.t_position.setText(position + 1 + "");

        // 4.判断position是否和selectItem相等
        if (position == selectItem) {
            convertView.setBackgroundColor(Color.GRAY);
        }
        else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
    class Myholder {
        TextView t_position, t_song, t_singer, t_duration;
    }


    // 3.listView的position传到变量selectItem中
    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

}
