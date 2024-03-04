package Song;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bottomnavigationview.R;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private List<Song> songs;
    class ViewHolder extends RecyclerView.ViewHolder{
        View songView;
        TextView song;
        TextView singer;
        TextView sign;

        public ViewHolder(View view){
            super(view);
            songView=view;
            song=(TextView)view.findViewById(R.id.text_song);
            singer=(TextView)view.findViewById(R.id.text_singer);
            sign=(TextView)view.findViewById(R.id.sign);
        }
    }

    public SongAdapter(List<Song> songList){songs=songList;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Song song=songs.get(position);
        holder.song.setText(song.getSong());
        holder.singer.setText(song.getSinger());
        if (onItemClickListener!=null){
            holder.songView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(position);
                    //holder.sign.setVisibility(View.VISIBLE);
                }
            });
            holder.songView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        //外部接口，点击事件
        void onClick(int pos);
        //外部接口，长点击事件
        void onLongClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
