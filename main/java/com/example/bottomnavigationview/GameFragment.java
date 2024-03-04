package com.example.bottomnavigationview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import Puzzle.PuzzleGame;
import Puzzle.PuzzleLayout;
import Puzzle.SuccessDialog;
import Utils.PuzzleUtil;

public class GameFragment extends Fragment implements PuzzleGame.GameStateListener {
    private PuzzleLayout puzzleLayout;
    private Bitmap bitmap;
    private String path="";
    private PuzzleGame puzzleGame;
    private ImageView srcImg;
    private Spinner spinner;
    private TextView tvLevel;
    private Button addlevel;
    private Button reducelevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.menu_game_layout, container, false);
        initView(view);
        initListener();
        return view;
    }

    private void initView(View view) {
        puzzleLayout = view.findViewById(R.id.puzzleLayout);
        puzzleGame = new PuzzleGame(view.getContext(), puzzleLayout);
        addlevel=view.findViewById(R.id.btnAddLevel);
        reducelevel=view.findViewById(R.id.btnReduceLevel);
        srcImg = view.findViewById(R.id.ivSrcImg);
        spinner =  view.findViewById(R.id.modeSpinner);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvLevel.setText("难度等级：" + puzzleGame.getLevel());
        srcImg.setImageBitmap(PuzzleUtil.readBitmap(getContext(), puzzleLayout.getRes(), 4));
    }

    private void initListener() {
        puzzleGame.addGameStateListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    puzzleGame.changeMode(PuzzleLayout.GAME_MODE_NORMAL);
                } else {
                    puzzleGame.changeMode(PuzzleLayout.GAME_MODE_EXCHANGE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //申请访问手机内存权限
        srcImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getView().getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }else {
                    selectPic();
                }
            }
        });
        addlevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puzzleGame.addLevel();
            }
        });
        reducelevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puzzleGame.reduceLevel();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    selectPic();        //获得权限打开手机图库
                }else {
                    Toast.makeText(getContext(),"You denied this request!",Toast.LENGTH_SHORT).show();
                    System.exit(0);     //没有权限退出
                }
        }
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
                    path=PuzzleUtil.getPath(getContext(),data.getData());
                    puzzleGame.changeImage(path);
                    bitmap=PuzzleUtil.readBitmap(path,12);
                    srcImg.setImageBitmap(bitmap);
                }
                break;
        }
    }

    @Override
    public void setLevel(int level) {
        tvLevel.setText("难度等级：" + level);
    }

    @Override
    public void gameSuccess(int level) {
        final SuccessDialog successDialog = new SuccessDialog();
        successDialog.show(getActivity().getFragmentManager(), "successDialog");
        successDialog.addButtonClickListener(new SuccessDialog.OnButtonClickListener() {
            @Override
            public void nextLevelClick() {
                puzzleGame.addLevel();
                successDialog.dismiss();
            }
            @Override
            public void cancelClick() {
                successDialog.dismiss();
            }
        });
    }
}
