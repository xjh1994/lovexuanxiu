package com.greentech.ixuanxiu.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.util.AppManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xjh1994 on 2015/11/15.
 */
public class AvatarActivity extends AppCompatActivity {

    private static final int OPENGALARY = 0;
    private MyUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);

        initData();
        initListener();
    }

    private void initData() {
        myUser = BmobUser.getCurrentUser(this, MyUser.class);
        if (null == myUser) {
            finish();
        }
    }

    private void initListener() {
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AvatarActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, OPENGALARY);
            }
        });
    }


    private String filePath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == OPENGALARY) {
                Uri uri = data.getData();
//                Uri imageUri = Uri.parse("content://media" + uri.getPath());
                logo.setImageURI(uri);

                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, pojo, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                    cursor.moveToFirst();
                    filePath = cursor.getString(columnIndex);
                    try {
                        // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
                        if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                            cursor.close();
                        }
                    } catch (Exception e) {
                        // Log.e("cursor", "error:" + e);
                    }
                    uploadAvatar();
                }
            }
        }
    }

    private void uploadAvatar() {
        new MaterialDialog.Builder(this)
                .title(R.string.uploading)
                .content(R.string.please_wait)
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 100, true)
                .cancelable(false)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                        BTPFileResponse response = BmobProFile.getInstance(AvatarActivity.this).upload(filePath, new UploadListener() {

                            @Override
                            public void onSuccess(String fileName, String url, BmobFile file) {
                                MyUser myUser = new MyUser();
                                myUser.setAvatarFile(file);
                                myUser.update(AvatarActivity.this, AvatarActivity.this.myUser.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(AvatarActivity.this, getString(R.string.toast_upload_avatar_success), Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(AvatarActivity.this, HomeActivity.class));
                                        overridePendingTransition(android.R.anim.slide_in_left,
                                                android.R.anim.slide_out_right);
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(AvatarActivity.this, s, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress) {
                                dialog.incrementProgress(progress);
                                if (dialog.getCurrentProgress() == dialog.getMaxProgress())
                                    dialog.dismiss();
                            }

                            @Override
                            public void onError(int statuscode, String errormsg) {
                                Toast.makeText(AvatarActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Bind(R.id.appname)
    TextView appname;
    @Bind(R.id.logo)
    CircleImageView logo;
    @Bind(R.id.upload)
    ButtonRectangle upload;
    @Bind(R.id.skip)
    ButtonRectangle skip;
    @Bind(R.id.tips)
    TextView tips;
}
