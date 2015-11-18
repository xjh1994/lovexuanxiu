package com.greentech.ixuanxiu.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.ui.AboutActivity;
import com.greentech.ixuanxiu.ui.LoginActivity;
import com.greentech.ixuanxiu.ui.MineActivity;
import com.greentech.ixuanxiu.ui.UserInfoEditActivity;
import com.greentech.ixuanxiu.util.AppManager;
import com.greentech.ixuanxiu.util.SPUtils;
import com.igexin.sdk.PushManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by xjh1994 on 2015/11/18.
 */
public class FragmentSetting extends FragmentBase {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        ButterKnife.bind(this, view);
        initView();
        initListener();
        return view;
    }


    private void initView() {
        Uri uri = null;
        if (null != BmobUser.getCurrentUser(getActivity(), MyUser.class)) {
            if (null != getCurrentUser()) {
                username.setText(getCurrentUser().getNick());
                phone.setText(getCurrentUser().getPhone());
                if (null != getCurrentUser().getAvatarFile()) {
                    uri = Uri.parse(getCurrentUser().getAvatarFile().getFileUrl(getActivity()));
                } else {
                    uri = Uri.parse(Config.logo_url);
                }
            } else return;
        } else {
            uri = Uri.parse(Config.logo_url);
            username.setText(getString(R.string.app_name));
            phone.setText(R.string.click_to_login);
        }
        image.setImageURI(uri);
    }


    private void initListener() {
        layoutUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != BmobUser.getCurrentUser(getActivity(), MyUser.class)) {
                    startActivity(new Intent(getActivity(), UserInfoEditActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        layoutMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getCurrentUser()) return;
                startActivity(new Intent(getActivity(), MineActivity.class));
            }
        });
        layoutPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.message_push)
                        .items(R.array.push_choice)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        boolean isPushTurnedOn = PushManager.getInstance().isPushTurnedOn(getActivity());
                                        if (!isPushTurnedOn)
                                            PushManager.getInstance().turnOnPush(getActivity());
                                        SPUtils.put(getActivity(), Config.KEY_PUSH, true);
                                        break;
                                    case 1:
                                        PushManager.getInstance().turnOffPush(getActivity());
                                        SPUtils.put(getActivity(), Config.KEY_PUSH, false);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        if (updateStatus == UpdateStatus.Yes) {//版本有更新

                        } else if (updateStatus == UpdateStatus.No) {
                            toast("已是最新版本");
                        } else if (updateStatus == UpdateStatus.EmptyField) {//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
//                            toast("请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。");
                        } else if (updateStatus == UpdateStatus.IGNORED) {
                            toast("该版本已被忽略更新");
                        } else if (updateStatus == UpdateStatus.ErrorSizeFormat) {
//                            toast("请检查target_size填写的格式，请使用file.length()方法获取apk大小。");
                        } else if (updateStatus == UpdateStatus.TimeOut) {
                            toast("查询出错或查询超时");
                        }
                    }
                });
                //发起自动更新
                BmobUpdateAgent.update(getActivity());
            }
        });
        layoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });
        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null == getCurrentUser())   return;
//                startActivity(new Intent(getActivity(), ShareCodeActivity.class));
                showShare();
            }
        });
        layoutExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != BmobUser.getCurrentUser(getActivity(), MyUser.class)) {
                    new MaterialDialog.Builder(getActivity())
                            .items(R.array.exit_choice)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                    switch (which) {
                                        case 0:
                                            BmobUser.logOut(getActivity());
                                            AppManager.getAppManager().finishAllActivity();
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            break;
                                        case 1:
                                            AppManager.getAppManager().AppExit(getActivity());
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show();
                } else {
                    AppManager.getAppManager().AppExit(getActivity());
                }
            }
        });
    }

    private void showShare() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        storeInSD(bitmap);

        String url = "http://www.lovexuanxiu.com";
        String title = "爱选修—最专业的大学生选修课交流社区";
        String comment = "爱选修是一个大学生选修课交流互动平台。在这里你可以找到不点名的课程、提前查看选课信息、给课程评价打分、找人一起上选修课、关注热门课程、蹭好课、和别人共享教材等等。还等什么，快来和大家一起爱上选修课吧！";
        String imagePath = "/sdcard/ixuanxiu.jpg";

        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(comment);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(imagePath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(getActivity());
    }

    protected void storeInSD(Bitmap bitmap) {
        File file = new File("/sdcard/");
        if (!file.exists()) {
            file.mkdir();
        }
        File imageFile = new File(file, "ixuanxiu.jpg");
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bind(R.id.image)
    SimpleDraweeView image;
    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.phone)
    TextView phone;
    @Bind(R.id.layout_userinfo)
    RelativeLayout layoutUserinfo;
    @Bind(R.id.layout_mine)
    RelativeLayout layoutMine;
    @Bind(R.id.push)
    TextView push;
    @Bind(R.id.layout_push)
    RelativeLayout layoutPush;
    @Bind(R.id.update)
    TextView update;
    @Bind(R.id.layout_update)
    RelativeLayout layoutUpdate;
    @Bind(R.id.about)
    TextView about;
    @Bind(R.id.layout_about)
    RelativeLayout layoutAbout;
    @Bind(R.id.layout_share)
    RelativeLayout layoutShare;
    @Bind(R.id.exit)
    TextView exit;
    @Bind(R.id.layout_exit)
    RelativeLayout layoutExit;
}