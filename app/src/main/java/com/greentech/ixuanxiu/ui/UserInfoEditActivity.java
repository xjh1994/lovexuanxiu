package com.greentech.ixuanxiu.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.util.SPUtils;
import com.igexin.sdk.PushManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by xjh1994 on 2015/11/10.
 */
public class UserInfoEditActivity extends BaseActivity {

    private static final int OPENGALARY = 0;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        Uri uri = null;
        if (null != BmobUser.getCurrentUser(this, MyUser.class)) {
            nickName.setText(getCurrentUser().getNick());
            if (null != getCurrentUser().getSex()) {
                sex.setText((getCurrentUser().getSex() == true) ? "汉子" : "妹子");
            } else {
                sex.setText(getString(R.string.not_writed));
            }
            sign.setText(TextUtils.isEmpty(getCurrentUser().getSign()) ? getString(R.string.sign_no_text) : getCurrentUser().getSign());
            if (null != getCurrentUser().getAvatarFile()) {
                uri = Uri.parse(getCurrentUser().getAvatarFile().getFileUrl(this));
            } else {
                uri = Uri.parse(Config.logo_url);
            }
        } else {
            uri = Uri.parse(Config.logo_url);
        }
        image.setImageURI(uri);
    }

    @Override
    public void initListeners() {
        layoutAvatart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, OPENGALARY);
            }
        });
        layoutNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(UserInfoEditActivity.this)
                        .title(R.string.dialog_change_nick)
                        .inputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .inputMaxLength(14)
                        .positiveText(R.string.submit)
                        .input(R.string.hint_nick, 0, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                nickName.setText(input);
                                MyUser myUser = new MyUser();
                                myUser.setNick(input.toString());
                                myUser.update(UserInfoEditActivity.this, getCurrentUser().getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        toast(getString(R.string.toast_change_nick_success));
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        toast(s);
                                    }
                                });

                            }
                        }).show();
            }
        });
        layoutPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserInfoEditActivity.this, ChangePassActivity.class));
            }
        });
        layoutSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(UserInfoEditActivity.this)
                        .title(R.string.change_sex)
                        .items(R.array.sex_choice)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        MyUser myUser = new MyUser();
                                        myUser.setSex(true);
                                        myUser.update(UserInfoEditActivity.this, getCurrentUser().getObjectId(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                toast(getString(R.string.toast_change_sex_success));
                                                sex.setText(getString(R.string.man));
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                toast(s);
                                            }
                                        });
                                        break;
                                    case 1:
                                        MyUser myUser1 = new MyUser();
                                        myUser1.setSex(false);
                                        myUser1.update(UserInfoEditActivity.this, getCurrentUser().getObjectId(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                toast(getString(R.string.toast_change_sex_success));
                                                sex.setText(getString(R.string.woman));
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                toast(s);
                                            }
                                        });
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        layoutSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(UserInfoEditActivity.this)
                        .title(R.string.dialog_write_sign)
                        .inputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .inputMaxLength(40)
                        .positiveText(R.string.submit)
                        .input(R.string.hint_sign, 0, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                sign.setText(input);
                                MyUser myUser = new MyUser();
                                myUser.setSign(input.toString());
                                myUser.update(UserInfoEditActivity.this, getCurrentUser().getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        toast(getString(R.string.toast_change_sign_success));
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        toast(s);
                                    }
                                });

                            }
                        }).show();
            }
        });
    }

    @Override
    public void initData() {

    }

    private String filePath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == OPENGALARY) {
                Uri uri = data.getData();
//                Uri imageUri = Uri.parse("content://media" + uri.getPath());
                image.setVisibility(View.VISIBLE);
                image.setImageURI(uri);

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
                        BTPFileResponse response = BmobProFile.getInstance(UserInfoEditActivity.this).upload(filePath, new UploadListener() {

                            @Override
                            public void onSuccess(String fileName, String url, BmobFile file) {
                                MyUser myUser = new MyUser();
                                myUser.setAvatarFile(file);
                                myUser.update(UserInfoEditActivity.this, getCurrentUser().getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        toast(getString(R.string.toast_upload_avatar_success));
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        toast(s);
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
                                toast(errormsg);
                            }
                        });
                    }
                }).show();
    }

    @Bind(R.id.image)
    SimpleDraweeView image;
    @Bind(R.id.layout_avatart)
    RelativeLayout layoutAvatart;
    @Bind(R.id.nickName)
    TextView nickName;
    @Bind(R.id.layout_nick)
    RelativeLayout layoutNick;
    @Bind(R.id.sex)
    TextView sex;
    @Bind(R.id.layout_sex)
    RelativeLayout layoutSex;
    @Bind(R.id.pass)
    TextView pass;
    @Bind(R.id.layout_pass)
    RelativeLayout layoutPass;
    @Bind(R.id.sign)
    TextView sign;
    @Bind(R.id.layout_sign)
    RelativeLayout layoutSign;
}
