package com.example.bottomnavigationview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import Song.Song;
import Utils.SongUtil;

public class songDetail extends AppCompatActivity {

    private LinearLayout back;
    private ImageView bitmap;
    private TextView songName;
    private TextView signer;
    private TextView currentTime;
    private TextView time;
    private SeekBar timeSeekBar;
    private int MaxTime;
    private SharedPreferences preference;
    private Button state;
    private Boolean isPlaying;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_detail_layout);
        init();
        changeImg();
        MaxTime=MusicFragment.musicBinder.getAll();
        timeSeekBar.setMax(MaxTime);
        initUI();
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicFragment.musicBinder.toTime(seekBar.getProgress());
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (timeSeekBar.getProgress()<=timeSeekBar.getMax()){
                    timeSeekBar.setProgress(MusicFragment.musicBinder.getProgress());
                    currentTime.setText(SongUtil.formatTime(MusicFragment.musicBinder.getProgress()));
                }
            }
        }).start();
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying=MusicFragment.musicBinder.isPlaying();
                if (isPlaying){
                    MusicFragment.musicBinder.pause();
                    state.setText("播放");
                }else {
                    MusicFragment.musicBinder.recovery();
                    state.setText("暂停");
                }
            }
        });
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MusicFragment.musicBinder.next();
//                changeMusic();
//            }
//        });
    }

    private void changeMusic() {
        final Song song=MusicFragment.songs.get(MusicFragment.musicBinder.getPos());
        if (song.getBitmapPath()!=null){
            bitmap.setImageBitmap(BitmapFactory.decodeFile(song.getBitmapPath()));
        }else {
            bitmap.setBackgroundResource(R.mipmap.init);
        }
        songName.setText(song.getSong());
        signer.setText(song.getSinger());
        time.setText(song.getTime());
        if (isPlaying){
            state.setText("暂停");
        }else {
            state.setText("播放");
        }
        MaxTime=MusicFragment.musicBinder.getAll();
        timeSeekBar.setMax(MaxTime);
    }

    private void init(){
        back=findViewById(R.id.detail_back);
        bitmap=findViewById(R.id.detail_album);
        songName=findViewById(R.id.album_text);
        songName.setSelected(true);
        signer=findViewById(R.id.text_signer);
        currentTime=findViewById(R.id.cur_time);
        time=findViewById(R.id.time);
        timeSeekBar=findViewById(R.id.time_seek);
        preference=getSharedPreferences("Song",MODE_PRIVATE);
        state=findViewById(R.id.detail_btnState);
        isPlaying=MusicFragment.musicBinder.isPlaying();
        next=findViewById(R.id.detail_next);
        if (MusicFragment.songs.size()==0){
            MusicFragment.songs=SongUtil.searchMusic(this);
        }
    }

    private void initUI(){
        Song song=(Song)getIntent().getParcelableExtra("song");
        if (song.getBitmapPath()!=null){
            bitmap.setImageBitmap(BitmapFactory.decodeFile(song.getBitmapPath()));
        }else {
            bitmap.setBackgroundResource(R.mipmap.init);
        }
        songName.setText(song.getSong());
        signer.setText(song.getSinger());
        time.setText(song.getTime());
        if (isPlaying){
            state.setText("暂停");
        }else {
            state.setText("播放");
        }
    }

    private void changeImg(){
        String path=preference.getString("imgPath","");
        if (!path.equals("")){
            back.setBackground(Drawable.createFromPath(path));
        }else {
            back.setBackgroundResource(R.mipmap.init);
        }
        back.getBackground().setAlpha(200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
