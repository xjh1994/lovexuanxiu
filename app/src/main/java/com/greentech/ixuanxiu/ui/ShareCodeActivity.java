package com.greentech.ixuanxiu.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.bean.ShareCode;
import com.greentech.ixuanxiu.util.CodeUtils;
import com.greentech.ixuanxiu.util.KeyBoardUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by xjh1994 on 2015/11/15.
 */
public class ShareCodeActivity extends BaseActivity {

    private String code = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_share_code);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(shareCode.getText().toString().trim())) {
                    KeyBoardUtils.copy(shareCode.getText().toString().trim(), ShareCodeActivity.this);
                    toast(getString(R.string.copy_to_keyboard));
                }
            }
        });
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(shareCode.getText().toString().trim())) {
                    showShare(shareCode.getText().toString().trim());
                }
            }
        });
    }

    @Override
    public void initData() {
        final String objectId = getCurrentUser().getObjectId();
        BmobQuery<ShareCode> query = new BmobQuery<>();
        BmobQuery<MyUser> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", objectId);
        query.addWhereMatchesQuery("myUser", "_User", innerQuery);
        query.findObjects(this, new FindListener<ShareCode>() {
            @Override
            public void onSuccess(List<ShareCode> list) {
                copy.setVisibility(View.VISIBLE);
                recommend.setVisibility(View.VISIBLE);

                if (list.size() > 0) {
                    code = list.get(0).getNumber();
                    recommendCount.setText(list.get(0).getUsedTimes() + "人");
                } else {
                    code = CodeUtils.toSerialNumber(objectId);
                    ShareCode shareCode = new ShareCode();
                    shareCode.setMyUser(getCurrentUser());
                    shareCode.setNumber(code);
                    shareCode.setUsedTimes(0);
                    shareCode.save(ShareCodeActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            toast(getString(R.string.get_share_code_success));
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
                shareCode.setText(code);
                recommendCount.setText("0人");
            }

            @Override
            public void onError(int i, String s) {
                toast(s);
            }
        });
    }

    private void showShare(String code) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        storeInSD(bitmap);

        String url = "http://www.lovexuanxiu.com";
        String title = "爱选修邀请码 " + code;
        String comment = "爱选修是一个大学生选修课交流互动平台。在这里你可以找到不点名的课程、提前查看选课信息、给课程评价打分、找人一起上选修课、关注热门课程、蹭好课、和别人共享教材等等。还等什么，快来和大家一起爱上选修课吧！";
        String imagePath = "/sdcard/ixuanxiu.jpg";

        ShareSDK.initSDK(this);
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
        oks.show(this);
    }

    @Bind(R.id.share_code)
    TextView shareCode;
    @Bind(R.id.recommend_count)
    TextView recommendCount;
    @Bind(R.id.copy)
    ButtonRectangle copy;
    @Bind(R.id.recommend)
    ButtonRectangle recommend;
}
