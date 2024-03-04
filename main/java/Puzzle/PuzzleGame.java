package Puzzle;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.bottomnavigationview.R;

public class PuzzleGame implements PuzzleLayout.SuccessListener {
    private PuzzleLayout puzzleLayou;
    private GameStateListener stateListener;
    private Context context;

    public void addGameStateListener(GameStateListener stateListener) {
        this.stateListener = stateListener;
    }

    public PuzzleGame(@NonNull Context context, @NonNull PuzzleLayout puzzleLayout) {
        this.context = context.getApplicationContext();
        this.puzzleLayou = puzzleLayout;
        puzzleLayou.addSuccessListener(this);
    }

    private boolean checkNull() {
        return puzzleLayou == null;
    }

    public void addLevel() {
        if (checkNull()) {
            return;
        }
        if (!puzzleLayou.addCount()) {
            Toast.makeText(context, context.getString(R.string.already_the_most_level), Toast.LENGTH_SHORT).show();
        }
        if (stateListener != null) {
            stateListener.setLevel(getLevel());
        }
    }

    public void reduceLevel() {
        if (checkNull()) {
            return;
        }
        if (!puzzleLayou.reduceCount()) {
            Toast.makeText(context, context.getString(R.string.already_the_less_level), Toast.LENGTH_SHORT).show();
        }
        if (stateListener != null) {
            stateListener.setLevel(getLevel());
        }
    }

    public void changeMode(String gameMode) {
        puzzleLayou.changeMode(gameMode);
    }

    public void changeImage(String path) {
        puzzleLayou.changeRes(path);
    }

    public int getLevel() {
        if (checkNull()) {
            return 0;
        }
        int count = puzzleLayou.getCount();
        return count - 3 + 1;
    }

    @Override
    public void success() {
        if (stateListener != null) {
            stateListener.gameSuccess(getLevel());
        }
    }

    public interface GameStateListener {
        public void setLevel(int level);
        public void gameSuccess(int level);
    }
}
