package Song;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    private String song;
    private String singer;
    private String path;
    private long size;
    private String  time;
    private String  bitmapPath;

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBitmapPath() {
        return bitmapPath;
    }

    public void setBitmapPath(String bitmapPath) {
        this.bitmapPath = bitmapPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(song);
        dest.writeString(singer);
        dest.writeString(path);
        dest.writeLong(size);
        dest.writeString(time);
        dest.writeString(bitmapPath);
    }

    public static final Parcelable.Creator<Song> CREATOR=new Parcelable.Creator<Song>(){
        @Override
        public Song createFromParcel(Parcel source) {
            Song song=new Song();
            song.song=source.readString();
            song.singer=source.readString();
            song.path=source.readString();
            song.size=source.readLong();
            song.time=source.readString();
            song.bitmapPath=source.readString();
            return song;
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
