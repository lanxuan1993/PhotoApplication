package com.lanxuan.photo.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: created by ZhaoBeibei on 2020-06-08 16:39
 * @describe: 文件处理工具类
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getName();

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param path
     * @return 删除成功返回true，否则返回false
     */
    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                delDirectory(path);
            }
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param path
     * @return 目录删除成功返回true，否则返回false
     */
    public static void delDirectory(String path) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符(斜线)
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    delDirectory(file.getAbsolutePath());
                }
            }
            // 删除当前目录
            dirFile.delete();
        }
    }

    /**
     * 获取应用文件缓存路径
     * 用来存储一些长时间保留的数据,应用卸载会被删除
     *
     * @param context
     * @param type
     * @return
     */
    public static String getFilePath(Context context, String type) {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            filePath = context.getExternalFilesDir(type).getPath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }

    /**
     * 获取应用缓存路径
     * 一般存放临时缓存数据,应用卸载会被删除
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        String cachePath = null;

        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                    !Environment.isExternalStorageRemovable()) {
                //外部存储可用
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                //外部存储不可用
                cachePath = context.getCacheDir().getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cachePath;
    }

    /**
     * 拷贝文件
     *
     * @param sourceFile
     * @param dstFile
     */
    public static void copyFile(File sourceFile, File dstFile) {
        try {
            InputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(dstFile);

            int BUFFER_SIZE = 1024 * 2;
            byte[] buffer = new byte[BUFFER_SIZE];
            BufferedInputStream in = new BufferedInputStream(inputStream, BUFFER_SIZE);
            BufferedOutputStream out = new BufferedOutputStream(outputStream, BUFFER_SIZE);
            int n;

            try {
                while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    out.write(buffer, 0, n);
                }
                out.flush();
            } finally {
                try {
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 创建文件目录
     * @param path (完整文件目录)
     * @return
     */
    public static File creatDirectory(String path){
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }
}
