package com.greentech.ixuanxiu.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Course;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xjh1994 on 2015/11/9.
 */
public class StartLessonActivity extends BaseActivity {

    private static final int OPENGALARY = 0;

    /**
     * 我要开课
     */

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_start_lesson);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        layoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, OPENGALARY);
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
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.send:
                startLesson(item);
                break;
        }
        return true;
    }

    private void startLesson(final MenuItem item) {
        item.setEnabled(false);
        if (isEmpty()) {
            item.setEnabled(true);
            return;
        }
        if (isTooLong()) {
            item.setEnabled(true);
            return;
        }
        if (isTooShort()) {
            item.setEnabled(true);
            return;
        }
        final Course course = new Course();
        course.setName(title.getText().toString().trim());
        course.setMyUser(getCurrentUser());
        course.setTeacher(teacher.getText().toString().trim());
        course.setClassTime(classTime.getText().toString().trim());
        course.setClassPlace(classPlace.getText().toString().trim());
        course.setContent(content.getText().toString().trim());
        course.setIntro(content.getText().toString().trim());
        course.setState(0);
        course.setCategory(getString(R.string.start_lesson));
        if (!TextUtils.isEmpty(filePath)) {
            toast(getString(R.string.uploading));
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
                            BTPFileResponse response = BmobProFile.getInstance(StartLessonActivity.this).upload(filePath, new UploadListener() {

                                @Override
                                public void onSuccess(String fileName, String url, BmobFile file) {
                                    course.setCoverFile(file);
                                    course.save(StartLessonActivity.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            toast(getString(R.string.toast_start_lesson_success));
                                            finish();
                                            Bundle data = new Bundle();
                                            data.putSerializable("course", course);
                                            Intent intent = new Intent(StartLessonActivity.this, CourseDetailActivity.class);
                                            intent.putExtras(data);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            toast(s);
                                            item.setEnabled(true);
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(int progress) {
//                                    takePhoto.setTextColor(getResources().getColor(R.color.primarycolor));
//                                    takePhoto.setText("已上传" + progress + "%");
                                    dialog.incrementProgress(progress);
                                    if (dialog.getCurrentProgress() == dialog.getMaxProgress())
                                        dialog.dismiss();
                                }

                                @Override
                                public void onError(int statuscode, String errormsg) {
                                    item.setEnabled(true);
                                }
                            });
                        }
                    }).show();
        } else {
            toast(getString(R.string.please_upload_cover));
            item.setEnabled(true);
            /*course.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    toast(getString(R.string.toast_start_lesson_success));
                }

                @Override
                public void onFailure(int i, String s) {
                    toast(s);
                    item.setEnabled(true);
                }
            });*/
        }

    }

    private boolean isTooShort() {
        if (content.getText().toString().trim().length() < 20) {
            toast(getString(R.string.toast_short_text_not_allow));
            return true;
        }
        return false;
    }

    private boolean isTooLong() {
        if (title.getText().toString().trim().length() > 30 || teacher.getText().toString().trim().length() > 20
                || classTime.getText().toString().trim().length() > 20 || classPlace.getText().toString().trim().length() > 30
                || content.getText().toString().trim().length() > 9999) {
            toast(getString(R.string.toast_content_too_long));
            return true;
        }
        return false;
    }

    private boolean isEmpty() {
        if (TextUtils.isEmpty(title.getText().toString().trim()) || TextUtils.isEmpty(teacher.getText().toString().trim())
                || TextUtils.isEmpty(classTime.getText().toString().trim()) || TextUtils.isEmpty(classPlace.getText().toString().trim()) ||
                TextUtils.isEmpty(content.getText().toString().trim())) {
            toast(getString(R.string.toast_empty_not_allowed));
            return true;
        }
        return false;
    }

    private void showDeterminateProgressDialog(final int progress) {
        new MaterialDialog.Builder(this)
                .title(R.string.uploading)
                .content(R.string.please_wait)
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 150, true)
                .cancelable(false)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                        dialog.incrementProgress(progress);
                        if (100 == progress)
                            dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start_lesson, menu);
        return true;
    }

    @Bind(R.id.title)
    MaterialEditText title;
    @Bind(R.id.teacher)
    MaterialEditText teacher;
    @Bind(R.id.class_time)
    MaterialEditText classTime;
    @Bind(R.id.class_place)
    MaterialEditText classPlace;
    @Bind(R.id.content)
    MaterialEditText content;
    @Bind(R.id.take_photo)
    TextView takePhoto;
    @Bind(R.id.layout_take_photo)
    LinearLayout layoutTakePhoto;
    @Bind(R.id.image)
    SimpleDraweeView image;
}
