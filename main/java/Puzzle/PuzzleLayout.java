package Puzzle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bottomnavigationview.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Utils.PuzzleUtil;

public class PuzzleLayout extends FrameLayout implements View.OnClickListener {

    public static final String GAME_MODE_NORMAL = "gameModeNormal";
    public static final String GAME_MODE_EXCHANGE = "gameModeExchange";

    private static final int DEFAULT_MARGIN = 3;

    //游戏模式
    private String mGameMode = GAME_MODE_NORMAL;

    //拼图布局为正方形，宽度为屏幕的宽度
    private int mViewWidth = 0;

    private int mViewHeight = 0;

    private int viewWidth;

    //拼图游戏每一行的图片个数(默认为三个)
    private int mCount = 3;

    //每张图片的宽度
    private int mItemWidth;

    private int mItemHeight;

    //拼图游戏bitmap集合
    private List<ImagePiece> mImagePieces;

    //用于给每个图片设置大小
    private FrameLayout.LayoutParams layoutParams;

    //大图
    private Bitmap mBitmap;

    //动画层
    private RelativeLayout mAnimLayout;

    //小图之间的margin
    private int mMargin;

    //这个view的padding
    private int mPadding;

    //选中的第一张图片
    private ImageView mFirst;

    //选中的第二张图片
    private ImageView mSecond;

    //是否添加了动画层
    private boolean isAddAnimatorLayout = false;

    //是否正在进行动画
    private boolean isAnimation = false;

    private String path = "";

    private int res = R.mipmap.init;

    public PuzzleLayout(Context context) {
        this(context, null);
    }

    public PuzzleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PuzzleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBitmaps();
        init(context);
        initBitmapsWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ImageView) {
                ImageView imageView = (ImageView) getChildAt(i);
                imageView.layout(imageView.getLeft(), imageView.getTop(), imageView.getRight(), imageView.getBottom());
            } else {
                RelativeLayout relativeLayout = (RelativeLayout) getChildAt(i);
                relativeLayout.layout(0, 0, mViewWidth, mViewHeight);
            }
        }
    }

    /**
     * 初始化初始变量
     *
     * @param context
     */
    private void init(Context context) {
        mMargin = PuzzleUtil.dp2px(context, DEFAULT_MARGIN);
        viewWidth = PuzzleUtil.getScreenWidth(context)[0];
        if (PuzzleUtil.bitmpShape(mBitmap)) {
            mViewWidth = viewWidth;
            mViewHeight = viewWidth;
        } else {
            mViewWidth = viewWidth;
            mViewHeight = (int) (viewWidth * ((mBitmap.getHeight() / (double) mBitmap.getWidth())));
        }
        mPadding = PuzzleUtil.getMinLength(getPaddingBottom(), getPaddingLeft(), getPaddingRight(), getPaddingTop());
        mItemWidth = (mViewWidth - mPadding * 2 - mMargin * (mCount - 1)) / mCount;
        mItemHeight = (mViewHeight - mPadding * 2 - mMargin * (mCount - 1)) / mCount;
    }

    /**
     * 将大图切割成多个小图
     */
    private void initBitmaps() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), res);
        }
        mImagePieces = PuzzleUtil.splitImage(getContext(), mBitmap, mCount, mGameMode);
        sortImagePieces();
    }

    private void initBitmaps(String path) {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeFile(path);
        }
        mImagePieces = PuzzleUtil.splitImage(getContext(), mBitmap, mCount, mGameMode);
        sortImagePieces();
    }

    /**
     * 对ImagePieces进行排序
     */
    private void sortImagePieces() {
        try {
            Collections.sort(mImagePieces, new Comparator<ImagePiece>() {
                @Override
                public int compare(ImagePiece lhs, ImagePiece rhs) {
                    return Math.random() > 0.5 ? 1 : -1;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mGameMode.equals(GAME_MODE_NORMAL)) {
                //如果是第二种模式就将空图放在最后
                ImagePiece tempImagePieces = null;
                int tempIndex = 0;
                for (int i = 0; i < mImagePieces.size(); i++) {
                    ImagePiece imagePiece = mImagePieces.get(i);
                    if (imagePiece.getType() == ImagePiece.TYPE_EMPTY) {
                        tempImagePieces = imagePiece;
                        tempIndex = i;
                        break;
                    }
                }
                if (tempImagePieces == null) return;
                mImagePieces.remove(tempIndex);
                mImagePieces.add(mImagePieces.size(), tempImagePieces);
            }
        }
    }

    /**
     * 设置图片的大小和layout的属性
     */
    private void initBitmapsWidth() {
        int line = 0;
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        for (int i = 0; i < mImagePieces.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(mImagePieces.get(i).getBitmap());
            layoutParams = new LayoutParams(mItemWidth, mItemHeight);
            imageView.setLayoutParams(layoutParams);
            if (i != 0 && i % mCount == 0) {
                line++;
            }
            if (i % mCount == 0) {
                left = i % mCount * mItemWidth;
            } else {
                left = i % mCount * mItemWidth + (i % mCount) * mMargin;
            }
            top = mItemHeight * line + line * mMargin;
            right = left + mItemWidth;
            bottom = top + mItemHeight;
            imageView.setRight(right);
            imageView.setLeft(left);
            imageView.setBottom(bottom);
            imageView.setTop(top);
            imageView.setId(i);
            if (mGameMode.equals(GAME_MODE_EXCHANGE)) {
                imageView.setAlpha(0.7f);
                checkIsOk(imageView);
            }
            imageView.setOnClickListener(this);
            mImagePieces.get(i).setImageView(imageView);
            addView(imageView);
        }
    }

    /**
     * 改变游戏模式
     *
     * @param gameMode
     */
    public void changeMode(@NonNull String gameMode) {
        if (gameMode.equals(mGameMode)) {
            return;
        }
        this.mGameMode = gameMode;
        reset();
    }

    public void reset() {
        if (PuzzleUtil.bitmpShape(mBitmap)) {
            mViewWidth = viewWidth;
            mViewHeight = viewWidth;
        } else {
            mViewWidth = viewWidth;
            mViewHeight = (int) (viewWidth * ((mBitmap.getHeight() / (double) mBitmap.getWidth())));
        }
        mItemWidth = (mViewWidth - mPadding * 2 - mMargin * (mCount - 1)) / mCount;
        mItemHeight = (mViewHeight - mPadding * 2 - mMargin * (mCount - 1)) / mCount;
        if (mImagePieces != null) {
            mImagePieces.clear();
        }
        isAddAnimatorLayout = false;
        mBitmap = null;
        removeAllViews();
        if (!path.equals("")) {
            initBitmaps(path);
        } else {
            initBitmaps();
        }
        initBitmapsWidth();
    }

    /**
     * 添加count 最多每行7个
     */
    public boolean addCount() {
        mCount++;
        if (mCount > 7) {
            mCount--;
            return false;
        }
        reset();
        return true;
    }

    /**
     * 改变图片
     */
    public void changeRes(String path) {
        this.path = path;
        reset();
        reset();
    }

    /**
     * 减少count 最少每行三个，否则普通模式无法游戏
     */
    public boolean reduceCount() {
        mCount--;
        if (mCount < 3) {
            mCount++;
            return false;
        }
        reset();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (mGameMode.equals(GAME_MODE_EXCHANGE)) {
            if (v.getAlpha() == 1.0f) {
                //图片放对位置，不允许点击
                return;
            }
        }
        if (isAnimation) {
            //还在运行动画的时候，不允许点击
            return;
        }
        if (!(v instanceof ImageView)) {
            return;
        }
        if (GAME_MODE_NORMAL.equals(mGameMode)) {
            ImageView imageView = (ImageView) v;
            ImagePiece imagePiece = mImagePieces.get(imageView.getId());
            if (imagePiece.getType() == ImagePiece.TYPE_EMPTY) {
                //普通模式，点击到空图不做处理
                return;
            }
            if (mFirst == null) {
                mFirst = (ImageView) v;
            }
            checkEmptyImage(mFirst);
        } else {
            //点的是同一个View
            if (mFirst == v) {
                mFirst.setColorFilter(null);
                mFirst = null;
                return;
            }
            if (mFirst == null) {
                mFirst = (ImageView) v;
                //选中之后添加一层颜色
                mFirst.setColorFilter(Color.parseColor("#55FF0000"));
            } else {
                mSecond = (ImageView) v;
                exChangeView();
            }
        }
    }

    /**
     * 检测该图片是否放对位置
     */
    private void checkIsOk(ImageView imageView) {
        ImagePiece imagePiece = mImagePieces.get(imageView.getId());
        if (imageView.getId() == imagePiece.getIndex()) {
            imageView.setAlpha(1.0f);
        }
    }

    /**
     * 检测是否成功
     */
    private boolean checkSuccess() {
        boolean isSuccess = true;
        for (int i = 0; i < mImagePieces.size(); i++) {
            ImagePiece imagePiece = mImagePieces.get(i);
            if (i != imagePiece.getIndex()) {
                isSuccess = false;
            }
        }
        return isSuccess;
    }

    private void checkEmptyImage(ImageView imageView) {
        int index = imageView.getId();
        int line = mImagePieces.size() / mCount;
        ImagePiece imagePiece = null;
        if (index < mCount) {
            //第一行（需要额外计算，下一行是否有空图）
            imagePiece = checkCurrentLine(index);
            //判断下一行同一列的图片是否为空
            imagePiece = checkOtherline(index + mCount, imagePiece);
        } else if (index < (line - 1) * mCount) {
            //中间的行（需要额外计算，上一行和下一行是否有空图）
            imagePiece = checkCurrentLine(index);
            //判断上一行同一列的图片是否为空
            imagePiece = checkOtherline(index - mCount, imagePiece);
            //判断下一行同一列的图片是否为空
            imagePiece = checkOtherline(index + mCount, imagePiece);
        } else {
            //最后一行（需要额外计算，上一行是否有空图））
            imagePiece = checkCurrentLine(index);
            //检查上一行同一列有没有空图
            imagePiece = checkOtherline(index - mCount, imagePiece);
        }
        if (imagePiece == null) {
            //周围没有空的imageView
            mFirst = null;
            mSecond = null;
        } else {
            //记录下第二张ImageView
            mSecond = imagePiece.getImageView();
            //选中第二个图片，开启动两张图片替换的动画
            exChangeView();
        }
    }

    /**
     * 检查上其他行同一列有没有空图
     *
     * @return
     */
    private ImagePiece checkOtherline(int index, ImagePiece imagePiece) {
        if (imagePiece != null) {
            return imagePiece;
        } else {
            return getCheckEmptyImageView(index);
        }
    }

    /**
     * 检查当前行有没有空的图片
     *
     * @param index
     * @return
     */
    private ImagePiece checkCurrentLine(int index) {
        ImagePiece imagePiece = null;
        //第一行
        if (index % mCount == 0) {
            //第一个
            imagePiece = getCheckEmptyImageView(index + 1);
        } else if (index % mCount == mCount - 1) {
            //最后一个
            imagePiece = getCheckEmptyImageView(index - 1);
        } else {
            imagePiece = getCheckEmptyImageView(index + 1);
            if (imagePiece == null) {
                imagePiece = getCheckEmptyImageView(index - 1);
            }
        }
        return imagePiece;
    }

    private ImagePiece getCheckEmptyImageView(int index) {
        ImagePiece imagePiece = mImagePieces.get(index);
        if (imagePiece.getType() == ImagePiece.TYPE_EMPTY) {
            //找到空的imageView
            return imagePiece;
        }
        return null;
    }

    private ImageView addAnimationImageView(ImageView imageView) {
        ImageView getImage = new ImageView(getContext());
        RelativeLayout.LayoutParams firstParams = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
        firstParams.leftMargin = imageView.getLeft() - mPadding;
        firstParams.topMargin = imageView.getTop() - mPadding;
        Bitmap firstBitmap = mImagePieces.get(imageView.getId()).getBitmap();
        getImage.setImageBitmap(firstBitmap);
        getImage.setLayoutParams(firstParams);
        mAnimLayout.addView(getImage);
        return getImage;
    }

    /**
     * 添加动画层，并且添加平移的动画
     */
    private void exChangeView() {
        //添加动画层
        setUpAnimLayout();
        //添加第一个图片
        ImageView first = addAnimationImageView(mFirst);
        //添加另一个图片
        ImageView second = addAnimationImageView(mSecond);

        ObjectAnimator secondXAnimator = ObjectAnimator.ofFloat(second, "TranslationX", 0f, -(mSecond.getLeft() - mFirst.getLeft()));
        ObjectAnimator secondYAnimator = ObjectAnimator.ofFloat(second, "TranslationY", 0f, -(mSecond.getTop() - mFirst.getTop()));
        ObjectAnimator firstXAnimator = ObjectAnimator.ofFloat(first, "TranslationX", 0f, mSecond.getLeft() - mFirst.getLeft());
        ObjectAnimator firstYAnimator = ObjectAnimator.ofFloat(first, "TranslationY", 0f, mSecond.getTop() - mFirst.getTop());
        AnimatorSet secondAnimator = new AnimatorSet();
        secondAnimator.play(secondXAnimator).with(secondYAnimator).with(firstXAnimator).with(firstYAnimator);
        secondAnimator.setDuration(300);

        final ImagePiece firstPiece = mImagePieces.get(mFirst.getId());
        final ImagePiece secondPiece = mImagePieces.get(mSecond.getId());
        final int firstType = firstPiece.getType();
        final int secondType = secondPiece.getType();
        final Bitmap firstBitmap = mImagePieces.get(mFirst.getId()).getBitmap();
        final Bitmap secondBitmap = mImagePieces.get(mSecond.getId()).getBitmap();
        secondAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int fristIndex = firstPiece.getIndex();
                int secondeIndex = secondPiece.getIndex();
                if (mFirst != null) {
                    mFirst.setColorFilter(null);
                    mFirst.setVisibility(VISIBLE);
                    mFirst.setImageBitmap(secondBitmap);
                    firstPiece.setBitmap(secondBitmap);
                    firstPiece.setIndex(secondeIndex);
                }
                if (mSecond != null) {
                    mSecond.setVisibility(VISIBLE);
                    mSecond.setImageBitmap(firstBitmap);
                    secondPiece.setBitmap(firstBitmap);
                    secondPiece.setIndex(fristIndex);
                }
                if (mGameMode.equals(GAME_MODE_NORMAL)) {
                    firstPiece.setType(secondType);
                    secondPiece.setType(firstType);
                }
                mAnimLayout.removeAllViews();
                mAnimLayout.setVisibility(GONE);
                if (mGameMode.equals(GAME_MODE_EXCHANGE)) {
                    checkIsOk(mFirst);
                    checkIsOk(mSecond);
                }
                mFirst = null;
                mSecond = null;
                isAnimation = false;
                invalidate();
                if (checkSuccess()) {
                    Toast.makeText(getContext(), "成功!", Toast.LENGTH_SHORT).show();
                    if (mSuccessListener != null) {
                        mSuccessListener.success();
                    }
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimation = true;
                mAnimLayout.setVisibility(VISIBLE);
                mFirst.setVisibility(INVISIBLE);
                mSecond.setVisibility(INVISIBLE);
            }
        });
        secondAnimator.start();
    }

    /**
     * 构造动画层 用于点击之后的动画
     * 为什么要做动画层？ 要保证动画在整个view上面执行。
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
        }
        if (!isAddAnimatorLayout) {
            isAddAnimatorLayout = true;
            addView(mAnimLayout);
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getRes() {
        return res;
    }

    public int getCount() {
        return mCount;
    }

    private SuccessListener mSuccessListener;

    public void addSuccessListener(SuccessListener successListener) {
        this.mSuccessListener = successListener;
    }

    public interface SuccessListener {
        public void success();
    }
}
