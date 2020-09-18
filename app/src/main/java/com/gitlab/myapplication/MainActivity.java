package com.gitlab.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gitlab.myapplication.album.ImageAlbum;
import com.lanxuan.photo.TakePhotoManager;
import com.lanxuan.photo.TakePhotoResult;
import com.lanxuan.photo.utils.ImageUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    ImageView imageView;
    TakePhotoManager takePhotoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button cameraBt = (Button) findViewById(R.id.bt_camera);
        Button albumBt = (Button) findViewById(R.id.bt_album);
        Button saveBase64Bt = (Button) findViewById(R.id.bt_saveBase64);
        Button saveLinkBt = (Button) findViewById(R.id.bt_saveLink);
        Button saveLocalIdBt = (Button) findViewById(R.id.bt_saveLocalId);
        Button previewImageBt = (Button) findViewById(R.id.bt_previewImage);

        albumBt.setOnClickListener(this);
        cameraBt.setOnClickListener(this);
        saveBase64Bt.setOnClickListener(this);
        saveLinkBt.setOnClickListener(this);
        saveLocalIdBt.setOnClickListener(this);
        previewImageBt.setOnClickListener(this);

        imageView = findViewById(R.id.iv_img);
        takePhotoManager = new TakePhotoManager(this);
    }

    @Override
    public void onClick(View v) {
        final String fileName = System.currentTimeMillis() + ".jpg";

        switch (v.getId()) {
            case R.id.bt_camera:
                takePhotoManager.openCamera()
                        .build(new TakePhotoResult() {
                            @Override
                            public void takeSuccess(String response) {
                                Log.i(TAG, "takeSuccess: " + response);
                                Bitmap bitmap = ImageUtils.pathToBitmap(response);
                                imageView.setImageBitmap(bitmap);
                            }

                            @Override
                            public void takeFailure(String code, String msg) {
                            }
                        });
                break;
            case R.id.bt_album:
                takePhotoManager.openAlbum()
                        .build(new TakePhotoResult() {
                            @Override
                            public void takeSuccess(String response) {
                                Log.i(TAG, "takeSuccess: " + response);
                                Bitmap bitmap = ImageUtils.pathToBitmap(response);
                                imageView.setImageBitmap(bitmap);
                            }

                            @Override
                            public void takeFailure(String code, String msg) {
                            }
                        });
                break;
            case R.id.bt_saveBase64:
                String base64Data = "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";
                if (ImageAlbum.getInstance(this).savaImageBase64(base64Data, fileName)) {
                    Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_saveLink:
                String imgLink = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png";
                ImageAlbum.getInstance(MainActivity.this).savaImageLink(imgLink, fileName, new ImageAlbum.HandleImageResult() {
                    @Override
                    public void handleResult(boolean obj) {
                        if (obj) {
                            Log.i(TAG, "handleResult: 图片保存成功");
                        } else {
                            Log.i(TAG, "handleResult: 图片保存失败");
                        }
                    }
                });

                break;
            case R.id.bt_saveLocalId:
                String localId = "bhfile://img/854de2c799be777b1e79d5e2e1d4f205.jpg";
                if (ImageAlbum.getInstance(this).savaImageLocalId(localId, fileName)) {
                    Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_previewImage:
                previewImage(this);
                break;
        }
    }

    /**
     * 图片预览
     */
    public void previewImage(Context mContext) {
        ArrayList<String> urlList = new ArrayList<>();
        urlList.add("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
        urlList.add("http://pic.4j4j.cn/upload/pic/20130617/55695c3c95.jpg");
        urlList.add("http://juheimg.oss-cn-hangzhou.aliyuncs.com/joke/201609/18/0D1DA63F5943FE6E2070E43F88E15E30.png");
        urlList.add("/storage/emulated/0/Android/data/com.bhfae.fae.dev/files/Pictures/img/854de2c799be777b1e79d5e2e1d4f205.jpg");
        String menus[] = {"share", "download"};
        boolean isNeedSave = false;
        boolean isNeedShare = false;
        for (String str : menus) {
            if (str.equals("download")) {
                isNeedSave = true;
            } else if (str.equals("share")) {
                isNeedShare = true;
            }
        }
        int position = 0;
        ImageAlbum.getInstance(this).initTransferee(urlList, position, isNeedSave, isNeedShare, new ImageAlbum.HandleImageResult() {
            @Override
            public void handleResult(boolean obj) {
                if (obj) {
                    Log.i(TAG, "handleResult: 图片保存成功");
                } else {
                    Log.i(TAG, "handleResult: 图片保存失败");
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageAlbum.getInstance(this).releaseTransferee();
    }
}
