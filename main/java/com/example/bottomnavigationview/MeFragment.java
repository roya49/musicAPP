package com.example.bottomnavigationview;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import Utils.AboutDialog;
import Utils.HelpDialog;
import Utils.PuzzleUtil;
import static android.content.Context.MODE_PRIVATE;

public class MeFragment extends Fragment {

    private LinearLayout setting;
    private String path;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private LinearLayout layout;
    private LinearLayout about;
    private LinearLayout help;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.menu_me_layout, container, false);
        setting=view.findViewById(R.id.set);
        layout= getActivity().findViewById(R.id.back_layout);
        preference=getActivity().getSharedPreferences("Song",MODE_PRIVATE);
        editor=preference.edit();
        about=view.findViewById(R.id.about);
        help=view.findViewById(R.id.help);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AboutDialog aboutDialog=new AboutDialog();
                aboutDialog.show(getFragmentManager(),"about");
                aboutDialog.addOnClickListener(new AboutDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        aboutDialog.dismiss();
                    }
                });
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPic();
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HelpDialog helpDialog=new HelpDialog();
                helpDialog.show(getFragmentManager(),"help");
                helpDialog.addOnClickListener(new AboutDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        helpDialog.dismiss();
                    }
                });
            }
        });
        return view;
    }

    /**
     * 选择手机图片
     */
    private void selectPic(){
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    /**
     * 得到手机图片，并设置
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (data!=null){
                    path= PuzzleUtil.getPath(getContext(),data.getData());
                    editor.putString("imgPath",path);
                    editor.apply();
                    changeImg();
                }
                break;
        }
    }

    private void changeImg(){
        String path=preference.getString("imgPath","");
        if (!path.equals("")){
            layout.setBackground(Drawable.createFromPath(path));
        }else {
            layout.setBackgroundResource(R.mipmap.init);
        }
        layout.getBackground().setAlpha(200);
    }
}
