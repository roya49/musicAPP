package com.example.bottomnavigationview;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Song.Song;
import Song.SongAdapter;
import Utils.SongUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.content.Context.MODE_PRIVATE;

public class MusicFragment extends Fragment {

    public static final String SINGLE = "单曲循环";     //单曲
    public static final String ALL = "全部播放";        //全部
    public static final String RANDOM = "随机播放";     //随机
    public static List<Song> songs = new ArrayList<>();
    private static MusicConn musicConn;
    public static MusicService.MusicBinder musicBinder;
    private static int position;
    private static CircleImageView songImage;
    private static Button btnPause;
    private static TextView info;
    private static Button btnMode;
    private static RecyclerView recyclerView;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private static LinearLayout songDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_music_layout, container, false);
        init(view);
        initAdapter();
        initServer();
        initUI();
        //播放、暂停
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song song = songs.get(position);
                musicBinder.start(song.getPath());
                if (btnPause.getText().equals("暂停")) {
                    btnPause.setText("播放");
                } else {
                    btnPause.setText("暂停");
                }
                String mode = preference.getString("mode", "");
                if (!mode.equals("")) {
                    btnMode.setText(mode);
                    musicBinder.setMode(mode);
                }
            }
        });
        //变更模式
        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btnMode.getText().toString().trim()) {
                    case SINGLE:
                        btnMode.setText(ALL);
                        break;
                    case ALL:
                        btnMode.setText(RANDOM);
                        break;
                    case RANDOM:
                        btnMode.setText(SINGLE);
                        break;
                }
                musicBinder.changeMode();
            }
        });
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.list_song);
        songImage = getActivity().findViewById(R.id.song_image);
        btnPause = getActivity().findViewById(R.id.btn_pause);
        btnMode = getActivity().findViewById(R.id.btn_mode);
        songDetail = getActivity().findViewById(R.id.song_layout);
        info = getActivity().findViewById(R.id.song_info);
        info.setSelected(true);
        preference = getActivity().getSharedPreferences("Song", MODE_PRIVATE);
        editor = preference.edit();
    }

    private void initAdapter() {
        //设置adapter
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        //防止多次加载
        if (songs.size() == 0) {
            songs = SongUtil.searchMusic(this.getActivity());
        }
        SongAdapter adapter = new SongAdapter(songs);
        recyclerView.setAdapter(adapter);
        //设置item缓存个数，防止出错
        recyclerView.setItemViewCacheSize(adapter.getItemCount());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //添加分割线
//        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        Drawable drawable=getResources().getDrawable(R.drawable.view_splitline);
//        dividerItemDecoration.setDrawable(drawable);
//        recyclerView.addItemDecoration(dividerItemDecoration);
        //点击播放
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                Song song = songs.get(pos);
                if (position == pos) {
                    if (musicBinder.isPlaying()) {
                        btnPause.setText("播放");
                    } else {
                        btnPause.setText("暂停");
                    }
                } else {
                    btnPause.setText("暂停");
                }
                updateUI(song);
                position = pos;
                musicBinder.start(song.getPath());
                String mode = preference.getString("mode", "");
                if (!mode.equals("")) {
                    musicBinder.setMode(mode);
                }
            }

            @Override
            public void onLongClick(int pos) {
                Song song = songs.get(pos);
                Toast.makeText(getContext(), "" + song.getPath(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initServer() {
        //启动服务
        Intent intent = new Intent(getActivity(), MusicService.class);
        musicConn = new MusicConn();
        getActivity().startService(intent);
        getActivity().bindService(intent, musicConn, Context.BIND_AUTO_CREATE);
    }

    private void initUI() {
        position = preference.getInt("pos", 0);
        Song song = songs.get(position);
        updateUI(song);
        String mode = preference.getString("mode", "");
        if (!mode.equals("")) {
            btnMode.setText(mode);
        }
        songDetail.setTag(song);
    }

    class MusicConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    public static void playAll() {
        position += 1;
        Song song = songs.get(position);
        musicBinder.start(song.getPath());
        musicBinder.setPos(position);
        updateUI(song);
    }

    public static void playRandom() {
        Random random = new Random();
        position = random.nextInt(songs.size());
        Song song = songs.get(position);
        musicBinder.start(song.getPath());
        musicBinder.setPos(position);
        updateUI(song);
    }

    private static void updateUI(final Song song) {
        info.setText(song.getSong() + "-" + song.getSinger());
        String bitmapPath = song.getBitmapPath();
        if (bitmapPath != null) {
            songImage.setImageBitmap(BitmapFactory.decodeFile(bitmapPath));
        } else {
//            //判断是否是默认图片，减少重复设置图片;前一句防止得不到对象
//            if (((BitmapDrawable)(songImage.getDrawable())).getBitmap()!=null&&
//                    ((BitmapDrawable)(songImage.getDrawable())).getBitmap().equals(BitmapFactory.decodeResource(getActivity().getResources(),R.mipmap.init))){
//                return;
//            }
            songImage.setImageResource(R.mipmap.init);
        }
        songDetail.setTag(song);
    }

    @Override
    public void onStop() {
        editor.putString("mode", btnMode.getText().toString().trim());
        editor.putInt("pos", position);
        editor.apply();
        super.onStop();
    }
}
