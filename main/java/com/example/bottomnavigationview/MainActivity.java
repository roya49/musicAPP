package com.example.bottomnavigationview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import Song.Song;

public class MainActivity extends AppCompatActivity {
    private static LinearLayout songDetail;
    List<Fragment> fragments;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout layout;
    private Fragment fragment;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.back_layout);
        preference = getSharedPreferences("Song", MODE_PRIVATE);
        editor = preference.edit();
        songDetail = findViewById(R.id.song_layout);
        createFragments();
        createBottom();
        changeImg();
        //详细页面
        songDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicFragment.musicBinder.next();
            }
        });
        songDetail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Song song = (Song) songDetail.getTag();
                Intent intent = new Intent(MainActivity.this, songDetail.class);
                intent.putExtra("song", song);
                startActivity(intent);
                return false;
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void changeImg() {
        String path = preference.getString("imgPath", "");
        if (!path.equals("")) {
            layout.setBackground(Drawable.createFromPath(path));
        } else {
            layout.setBackgroundResource(R.mipmap.back);
        }
        layout.getBackground().setAlpha(180);
    }

    private void createBottom() {
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_music:   //音乐页
                        changeImg();
                        songDetail.setVisibility(View.VISIBLE);
                        setFragmentPosition(0);
                        return true;
                    case R.id.menu_game:    //游戏页
                        layout.setBackgroundColor(getResources().getColor(R.color.white));
                        songDetail.setVisibility(View.GONE);
                        setFragmentPosition(1);
                        return true;
                    case R.id.menu_me:      //主页
                        changeImg();
                        songDetail.setVisibility(View.VISIBLE);
                        setFragmentPosition(2);
                        return true;
                }
                return false;
            }
        });
    }

    private void createFragments() {
        fragments = new ArrayList<>();
        fragments.add(new MusicFragment());
        fragments.add(new GameFragment());
        fragments.add(new MeFragment());
        setFragmentPosition(0);
    }

    //优化切换
    private void setFragmentPosition(int po) {
        Fragment currentFragment = fragments.get(po);
        if (!currentFragment.isAdded()) {
            if (fragment != null)
                getSupportFragmentManager().beginTransaction().hide(fragment).add(R.id.fragment_content, currentFragment).commit();
            else
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_content, currentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(fragment).show(currentFragment).commit();
        }
        fragment = currentFragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "You denied this request!", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //super.onSaveInstanceState(outState);
    }
}
