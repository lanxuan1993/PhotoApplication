package com.gitlab.myapplication.album;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.gitlab.myapplication.album.index.CustomIndexIndicator;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.lanxuan.photo.utils.FileUtils;
import com.lanxuan.photo.utils.ImageUtils;
import com.vansz.glideimageloader.GlideImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author: created by ZhaoBeibei on 2020-08-31 10:24
 * @describe: 保存图片到相册, 仿微信浏览图片
 */
public class ImageAlbum {
    private static final String TAG = ImageAlbum.class.getName();
    private static ImageAlbum instance = null;
    private Transferee transferee;
    private Context mContext;
    private int currentPosition = 0;

    public static ImageAlbum getInstance(Context context) {
        if (instance == null) {
            instance = new ImageAlbum(context);
        }
        return instance;
    }

    private ImageAlbum(Context context) {
        mContext = context;
        transferee = Transferee.getDefault(context);
    }

    /**
     * 根据图片base64数据保存到相册
     *
     * @param base64Data
     */
    public boolean savaImageBase64(String base64Data, String fileName) {
        Bitmap bitmap = ImageUtils.base64ToBitmap(base64Data);
        if (bitmap != null) {
            return saveBitmapToAlbum(bitmap, fileName, getAppName(mContext));
        }
        return false;
    }

    /**
     * 根据图片链接保存到相册
     *
     * @param imgLink
     */
    public void savaImageLink(final String imgLink, final String fileName, final HandleImageResult handleImageResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imgLink);
                    InputStream inputStream = url.openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    if (bitmap != null) {
                        boolean isSave = saveBitmapToAlbum(bitmap, fileName, getAppName(mContext));
                        handleImageResult.handleResult(isSave);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handleImageResult.handleResult(false);
            }
        }).start();
    }


    /**
     * 根据图片localId保存到相册
     *
     * @param localId
     * @param fileName
     * @return
     */
    public boolean savaImageLocalId(String localId, String fileName) {
        try {
            String img = localId.substring(localId.lastIndexOf("/"));
            String path = FileUtils.getFilePath(mContext, Environment.DIRECTORY_PICTURES) + "/img" + img;

//          path ="/storage/emulated/0/Android/data/com.bhfae.fae.production/files/Pictures/img/img.jpg";
            path = "/storage/emulated/0/Android/data/com.bhfae.fae.dev/files/Pictures/img/7a4a6caadb481733b72f2e2a8019f30c.jpg";

            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    return saveBitmapToAlbum(bitmap, fileName, getAppName(mContext));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 将图片Bitmap保存到相册
     *
     * @param bitmap
     * @param fileName
     * @return
     */
    private boolean saveBitmapToAlbum(Bitmap bitmap, String fileName, String folderName) {
        boolean isSave;
        if (Build.VERSION.SDK_INT >= 29) {
            isSave = saveImageAboveAndroidQ(bitmap, fileName, folderName);
        } else {
            isSave = saveImageBelowAndroidQ(bitmap, fileName, folderName);
        }
        return isSave;
    }

    /**
     * 将图片Bitmap保存到相册(适配Android10,公共媒体文件的操作需要用到ContentResolver和Cursor)
     *
     * @param bitmap
     * @param fileName
     */
    private boolean saveImageAboveAndroidQ(Bitmap bitmap, String fileName, String folderName) {
        try {
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/" + folderName);

            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将图片Bitmap保存到相册(适配Android10以下)
     *
     * @param bitmap
     * @param fileName
     */
    private boolean saveImageBelowAndroidQ(Bitmap bitmap, String fileName, String folderName) {
        String albumPath;
        if (Build.BRAND.equalsIgnoreCase("xiaomi") || Build.BRAND.equalsIgnoreCase("Huawei")) {
            albumPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + "/Camera";
        } else {
            albumPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM;
        }

        File fileDir = new File(albumPath, folderName);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        File file = new File(fileDir, fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
//            insertGallery(null,file);
            refreshGallery(mContext, file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送广播，通知刷新图库的显示
     *
     * @param mContext
     * @param file
     */
    private void refreshGallery(Context mContext, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{file.getAbsolutePath()};
            MediaScannerConnection.scanFile(mContext, paths, null, null);
        } else {
            Intent intent;
            if (file.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
            }
            mContext.sendBroadcast(intent);
        }
    }


    /**
     * 插入图库
     *
     * @param bitmap
     * @param file
     */
    private void insertGallery(Bitmap bitmap, File file) {
        try {
            if (file != null) {
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
            } else if (bitmap != null) {
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, null, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取应用程序名称
     *
     * @param context
     * @return
     */
    private String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 初始化Transferee配置,浏览图片
     *
     * @param urlList
     * @param startPosition
     * @param isNeedSave
     * @param isNeedShare
     */
    public void initTransferee(final ArrayList<String> urlList, int startPosition, boolean isNeedSave,
                               boolean isNeedShare, final HandleImageResult handleImageResult) {
        currentPosition = startPosition;
        CustomIndexIndicator customIndexIndicator = new CustomIndexIndicator();
        customIndexIndicator.setOnChangePageSelected(new CustomIndexIndicator.OnChangePageSelected() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }
        });

        CustomView customView = new CustomView(mContext);
        customView.setIsNeedSave(isNeedSave);
        customView.setIsNeedShare(isNeedShare);

        TransferConfig transferConfig = TransferConfig.build()
                .setSourceUrlList(urlList)
                .setIndexIndicator(new NumberIndexIndicator())
                .setImageLoader(GlideImageLoader.with(mContext))
                .setBackgroundColor(Color.parseColor("#000000"))
                .setDuration(300)
                .setOffscreenPageLimit(2)
                .setIndexIndicator(customIndexIndicator)
                .setCustomView(customView)
                .setNowThumbnailIndex(currentPosition)
                .enableJustLoadHitPage(true)
                .enableDragClose(false)
                .enableDragHide(false)
                .enableDragPause(false)
                .enableHideThumb(false)
                .enableScrollingWithPageChange(false)
                .create();
        transferee.apply(transferConfig).show();

        customView.setOnSaveClickListener(new CustomView.OnSaveClickListener() {
            @Override
            public void onSaveClick(View v) {
                Log.i(TAG, "保存:" + currentPosition + "::" + urlList.get(currentPosition));
                boolean isSave = saveTrasferFile(urlList.get(currentPosition));
                handleImageResult.handleResult(isSave);
            }
        });

        customView.setOnShareClickListener(new CustomView.OnShareClickListener() {
            @Override
            public void onShareClick(View v) {
                Log.i(TAG, "分享:" + currentPosition + "::" + urlList.get(currentPosition));
            }
        });
    }

    /**
     * 保存Trasfer的文件
     * @param url
     * @return
     */
    private boolean saveTrasferFile(String url) {
        File file = transferee.getImageFile(url);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                return saveBitmapToAlbum(bitmap, file.getName(), getAppName(mContext));
            }
        }
        return false;
    }

    /**
     * 销毁 transferee 中的资源
     */
    public void releaseTransferee() {
        transferee.destroy();
    }

    public interface HandleImageResult {
        void handleResult(boolean obj);
    }
}
