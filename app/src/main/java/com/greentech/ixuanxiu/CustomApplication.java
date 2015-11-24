package com.greentech.ixuanxiu;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.greentech.ixuanxiu.util.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.socialization.Socialization;
import cn.smssdk.SMSSDK;

/**
 * Created by xjh1994 on 2015/10/25.
 */
public class CustomApplication extends Application {

    /**
     *
     *初始化和记录一些app信息，例如app的版本信息、设备信息等等；
     *初始化特定的业务需求，例如有盟统计类、分享SDK、推送等等记录应用启动次数、是否第一次安装等等
     *记录是否开启处于调试模式。
     *
     */

    public static CustomApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(getResources().getString(R.string.app_name)).setLogLevel(LogLevel.FULL);
        mInstance = this;
        init();

        CrashReport.initCrashReport(this, "900011569", false);

        //Mob短信
        SMSSDK.initSDK(this, Config.mobAppkey, Config.mobAppSecret);

    }

    private void init() {

        initImageLoader(this);
        Fresco.initialize(this);
    }

    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                Config.CACHE_DIR);// 获取到缓存的目录地址
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                // 线程池内加载的数量
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                        // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
    }

    public static CustomApplication getInstance() {
        return mInstance;
    }

    // 单例模式，才能及时返回数据
    SharePreferenceUtil mSpUtil;
    public static final String PREFERENCE_NAME = "_sharedinfo";

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            String currentId = BmobUserManager.getInstance(
                    getApplicationContext()).getCurrentUserObjectId();
            String sharedName = currentId + PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this, sharedName);
        }
        return mSpUtil;
    }

    NotificationManager mNotificationManager;

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    MediaPlayer mMediaPlayer;

    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }

    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();
        setContactList(null);
    }
}
