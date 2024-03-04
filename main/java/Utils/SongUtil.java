package Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Song.Song;

public class SongUtil {

    public  static List<Song> list;
    public static Song song;

    //查询本地里的歌曲
    public static List<Song> searchMusic(Context context){
        list=new ArrayList<>();
        Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null, MediaStore.Audio.Media.TITLE );
            if(cursor!=null){
            while (cursor.moveToNext()){
                song =new Song();
                song.setSong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                song.setSinger(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                song.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                song.setTime(formatTime(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                song.setBitmapPath(getBitmap(context,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));
                list.add(song);
            }
        }
        cursor.close();
        return list;
    }

    private static String getBitmap(Context context,String  id){
        String uri="content://media/external/audio/albums";
        String[] projection=new String[]{"album_art"};
        String art="";
        Cursor cursor=context.getContentResolver().query(Uri.parse(uri+"/"+id),projection,null,null,null);
        if (cursor.getColumnCount()>0 && cursor.getCount()>0){
            cursor.moveToFirst();
            art=cursor.getString(0);
        }
        cursor.close();
        return art;
    }

    public static String formatTime(int time){
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("mm:ss");
        return format.format(date);
    }
}
