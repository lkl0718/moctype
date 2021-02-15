package com.md.moktype.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CommonUtils {

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    /**
     * 네트워크 연결 확인
     * @param context
     * @return
     */
    public static int getConnectivityStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE){
                // 3G or LTE
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI){
                // WIFI
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * URL Decode
     */
    public static String getURLDecode(String content){
        try {
            return URLDecoder.decode(content, "utf-8");
        } catch (NullPointerException | UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 앱 캐시 삭제
     */
    public static void clearApplicationCache(Context context){
        File dir = context.getCacheDir();
        if(dir==null) return;

        File[] children = dir.listFiles();
        try {
            for(int i=0;i<children.length;i++) {
                if(children[i].isDirectory()) {
                    clearApplicationCache(context, children[i]);
                } else {
                    children[i].delete();
                }
            }
        } catch(Exception e){}
    }

    /**
     * 앱 캐시 삭제
     */
    public static void clearApplicationCache(Context context, java.io.File dir){
        if(dir==null) dir = context.getCacheDir();
        if(dir==null) return;

        java.io.File[] children = dir.listFiles();
        try{
            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory()) {
                    clearApplicationCache(context, children[i]);
                } else {
                    children[i].delete();
                }
        }
        catch(Exception e){}
    }

    /**
     * 촬영한 사진 Resize
     * @param src
     * @return
     */
    public static Bitmap getResizedImage(Bitmap src) {
        int nWidth = src.getWidth();
        int nHeight = src.getHeight();

        if(nWidth == 0 || nHeight == 0)
            return null;

        float fScale;
        if (nWidth <= 1920 && nHeight <= 1920) {
            return src;
        }

        if (nWidth >= nHeight)
            fScale = (float) (1920.0 / (float) nWidth);
        else
            fScale = (float) (1920.0 / (float) nHeight);

        int nDstWidth = (int) (nWidth * fScale);
        int nDesHeight = (int) (nHeight * fScale);

        Bitmap resized = Bitmap.createScaledBitmap(src, nDstWidth, nDesHeight, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // 회전한 각도 입력
        resized = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);

        return resized;
    }

    /**
     * 앨범에서 선택한 사진 Resize
     * @param strFilePath
     * @return
     */
    public static Bitmap getResizedImage(String strFilePath) {
        Bitmap src = BitmapFactory.decodeFile(strFilePath);

        if(src == null)
            return null;

        int exifOrientation;
        int exifDegree;

        ExifInterface exif = null;

        try {
            exif = new ExifInterface(strFilePath);

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);

                src = rotate(src, exifDegree);
            } else {
                exifDegree = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int nWidth = src.getWidth();
        int nHeight = src.getHeight();

        float fScale;
        if (nWidth <= 1920 && nHeight <= 1920) {
            return src;
        }

        if (nWidth >= nHeight)
            fScale = (float) (1920.0 / (float) nWidth);
        else
            fScale = (float) (1920.0 / (float) nHeight);

        int nDstWidth = (int) (nWidth * fScale);
        int nDesHeight = (int) (nHeight * fScale);

        Bitmap resized = Bitmap.createScaledBitmap(src, nDstWidth, nDesHeight, true);

//        int fileNameIdx = strFilePath.lastIndexOf("/");
//        String realFileName = strFilePath.substring(fileNameIdx + 1);
//        saveBitmaptoJpeg(resized, realFileName);

        return resized;
    }

    private static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private static Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI(final Context context, final Uri uri) {
        String strAuth = uri.getAuthority();
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri) || isDownloadsDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];
            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/"
                        + split[1];
            } else {
                String SDcardpath = getRemovableSDCardPath(context).split("/Android")[0];
                return SDcardpath +"/"+ split[1];
            }
        }

        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {
            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
        }

        // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] { split[1] };

            return getDataColumn(context, contentUri, selection,
                    selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getRemovableSDCardPath(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return storages[1].toString();
        else
            return "";
    }
}
