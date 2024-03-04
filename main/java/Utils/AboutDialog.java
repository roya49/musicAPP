package Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bottomnavigationview.R;

public class AboutDialog extends DialogFragment {

    public OnClickListener clickListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.about));
        builder.setMessage(getString(R.string.message)).
                setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (clickListener != null) {
                            clickListener.onClick();
                        }
                    }
                });
        return builder.create();
    }

    public void addOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnClickListener {
        void onClick();
    }
}
