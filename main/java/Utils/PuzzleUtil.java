package Utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.example.bottomnavigationview.R;

import java.util.ArrayList;
import java.util.List;

import Puzzle.ImagePiece;

public class PuzzleUtil {
    /**
     * 返回屏幕的宽高，用数组返回
     * 下标0，width。 下标1，height。
     *
     * @param context
     * @return
     */
    public static int[] getScreenWidth(Context context) {
        context = context.getApplicationContext();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        int[] size = new int[2];
        size[0] = width;
        size[1] = height;
        return size;
    }

    /**
     * 传入一个bitmap 返回 一个picec集合
     *
     * @param bitmap
     * @param count
     * @return
     */
    public static List<ImagePiece> splitImage(Context context, Bitmap bitmap, int count, String gameMode) {
        List<ImagePiece> imagePieces = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int picWidth = width / count;
        int picHeight = height / count;

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * count);
                //为createBitmap 切割图片获取xy
                int x = j * picWidth;
                int y = i * picHeight;
                if (gameMode.equals(Puzzle.PuzzleLayout.GAME_MODE_NORMAL)) {
                    if (i == count - 1 && j == count - 1) {
                        imagePiece.setType(ImagePiece.TYPE_EMPTY);
                        Bitmap emptyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
                        imagePiece.setBitmap(emptyBitmap);
                    } else {
                        imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, picWidth, picHeight));
                    }
                } else {
                    imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, picWidth, picHeight));
                }
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;
    }

    /**
     * 读取图片，按照缩放比保持长宽比例返回bitmap对象
     * <p>
     * @param scale 缩放比例(1到10, 为2时，长和宽均缩放至原来的2分之1，为3时缩放至3分之1，以此类推)
     * @return Bitmap
     */
    public synchronized static Bitmap readBitmap(Context context, int res, int scale) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeResource(context.getResources(), res, options);
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized static Bitmap readBitmap(String path,int scale){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(path,options);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getMinLength(int... params) {
        int min = params[0];
        for (int para : params) {
            if (para < min) {
                min = para;
            }
        }
        return min;
    }

    //dp px
    public static int dp2px(Context context, int dpval) {
        context = context.getApplicationContext();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval, context.getResources().getDisplayMetrics());
    }

    //判断图片的形状
    public static boolean bitmpShape(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width==height){
            return true;   //正方形
        }else {
            return false;   //长方形
        }
    }

    /**
     * 得到图片path
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri){
        String filePath="";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    private static String getDataColumn(Context context, Uri uri, String  selection, String[]  selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
