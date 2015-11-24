package com.greentech.ixuanxiu.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.CustomApplication;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.bean.PostComment;
import com.greentech.ixuanxiu.bean.Push;
import com.greentech.ixuanxiu.ui.HomeActivity;
import com.greentech.ixuanxiu.ui.PostDetailActivity;
import com.greentech.ixuanxiu.util.CollectionUtils;
import com.greentech.ixuanxiu.util.CommonUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xjh1994 on 2015/10/25.
 */
public class MyMessageReceiver extends BroadcastReceiver {

    // 事件监听
    public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;//
    BmobUserManager userManager;
    BmobChatUser currentUser;

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("msg");
//        Logger.json("收到的message = ", json);

        userManager = BmobUserManager.getInstance(context);
        currentUser = userManager.getCurrentUser();
        boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
        if (isNetConnected) {
            parseMessage(context, json);
        } else {
            for (int i = 0; i < ehList.size(); i++)
                ((EventListener) ehList.get(i)).onNetChange(isNetConnected);
        }
    }

    /**
     * 解析Json字符串
     *
     * @param @param context
     * @param @param json
     * @return void
     * @throws
     * @Title: parseMessage
     * @Description: TODO
     */
    private void parseMessage(final Context context, String json) {
        JSONObject jo = null;
        try {
            jo = new JSONObject(json);
            String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
            if (tag.equals(BmobConfig.TAG_OFFLINE)) {//下线通知
                if (currentUser != null) {
                    if (ehList.size() > 0) {// 有监听的时候，传递下去
                        for (EventListener handler : ehList)
                            handler.onOffline();
                    } else {
                        //清空数据
                        CustomApplication.getInstance().logout();
                    }
                }
            } else {
                String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
                //增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
                final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
                String msgTime = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_MSGTIME);
                if (fromId != null && !BmobDB.create(context, toId).isBlackUser(fromId)) {//该消息发送方不为黑名单用户
                    if (TextUtils.isEmpty(tag)) {//不携带tag标签--此可接收陌生人的消息
                        BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {

                            @Override
                            public void onSuccess(BmobMsg msg) {
                                // TODO Auto-generated method stub
                                if (ehList.size() > 0) {// 有监听的时候，传递下去
                                    for (int i = 0; i < ehList.size(); i++) {
                                        ((EventListener) ehList.get(i)).onMessage(msg);
                                    }
                                } else {
                                    boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowPushNotify();
                                    if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {//当前登陆用户存在并且也等于接收方id
                                        mNewNum++;
                                        showMsgNotify(context, msg);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int code, String arg1) {
                                // TODO Auto-generated method stub
                                BmobLog.i("获取接收的消息失败：" + arg1);
                            }
                        });

                    } else {//带tag标签
                        if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
                            //保存好友请求道本地，并更新后台的未读字段
                            BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
                            if (currentUser != null) {//有登陆用户
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (ehList.size() > 0) {// 有监听的时候，传递下去
                                        for (EventListener handler : ehList)
                                            handler.onAddUser(message);
                                    } else {
                                        //TODO NewFriendActivity
                                        showOtherNotify(context, message.getFromname(), toId, message.getFromname() + "请求添加好友", HomeActivity.class);
                                    }
                                }
                            }
                        } else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {
                            String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
                            //收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
                            BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {

                                @Override
                                public void onError(int arg0, final String arg1) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onSuccess(List<BmobChatUser> arg0) {
                                    // TODO Auto-generated method stub
                                    //保存到内存中
                                    CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
                                }
                            });
                            //显示通知
                            showOtherNotify(context, username, toId, username + "同意添加您为好友", HomeActivity.class);
                            //创建一个临时验证会话--用于在会话界面形成初始会话
                            BmobMsg.createAndSaveRecentAfterAgree(context, json);

                        } else if (tag.equals(BmobConfig.TAG_READED)) {//已读回执
                            String conversionId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_CONVERSIONID);
                            if (currentUser != null) {
                                //更改某条消息的状态
                                BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
                                        for (EventListener handler : ehList)
                                            handler.onReaded(conversionId, msgTime);
                                    }
                                }
                            }
                        }
                    }
                } else {//在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
                    BmobChatManager.getInstance(context).updateMsgReaded(true, fromId, msgTime);
                    BmobLog.i("该消息发送方为黑名单用户");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            //这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
//            BmobLog.i("后台推送给客户端的消息/开发者自定义发送的消息：" + e.getMessage());

            try {
                String type = (null != BmobJsonUtil.getString(jo, "type")) ? BmobJsonUtil.getString(jo, "type") : Config.Push_Type_Common;
                final String alert = (null != BmobJsonUtil.getString(jo, "alert")) ? BmobJsonUtil.getString(jo, "alert") : context.getString(R.string.app_name);

                if (type.equals(Config.Push_Type_Common)) {     //普通推送
                    showNotify(context, alert, HomeActivity.class);
                    return;
                } else if (type.equals(Config.Push_Type_Discuss)) {    //讨论区回复
                    final String username = BmobJsonUtil.getString(jo, "username");
                    final String toId = BmobJsonUtil.getString(jo, "toId");
                    String postId = BmobJsonUtil.getString(jo, "postId");
                    BmobQuery<Post> query = new BmobQuery<>();
                    query.include("myUser, course");
                    query.getObject(context, postId, new GetListener<Post>() {
                        @Override
                        public void onSuccess(Post post) {
                            Bundle data = new Bundle();
                            data.putSerializable("post", post);
                            Intent intent = new Intent(context, PostDetailActivity.class);
                            intent.putExtras(data);
                            showOtherNotifyWithExtras(context, username, toId, alert, intent);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                } else if (type.equals(Config.Push_Type_User)) {    //TODO

                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    //普通通知
    public void showNotify(Context context, String ticker, Class<?> cls) {
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowPushNotify();
        boolean isAllowVoice = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
        if (isAllow && currentUser != null) {
            //同时提醒通知
            BmobNotifyManager.getInstance(context).showNotify(isAllowVoice, isAllowVibrate, R.mipmap.ic_launcher, ticker, "递递", ticker.toString(), cls);
            /*try {
                DbUtils db = DbUtils.create(context);
                Push push = new Push();
                push.setMessage(ticker.toString());
                SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
                String time = format.format(new Date());
                push.setTime(time);
                db.save(push); // 使用saveBindingId保存实体时会为实体的id赋值
            } catch (DbException e1) {
                e1.printStackTrace();
            }*/
        }
    }

    /**
     * 显示其他Tag的通知
     * showOtherNotify
     */
    public void showOtherNotify(Context context, String username, String toId, String ticker, Class<?> cls) {

        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowPushNotify();
        boolean isAllowVoice = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
        if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {
            //同时提醒通知
            BmobNotifyManager.getInstance(context).showNotify(isAllowVoice, isAllowVibrate, R.mipmap.ic_launcher, ticker, username, ticker.toString(), cls);
        }
    }

    public void showOtherNotifyWithExtras(Context context, String username, String toId, String ticker, Intent intent) {

        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowPushNotify();
        boolean isAllowVoice = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
        if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {
            //同时提醒通知
            BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice, isAllowVibrate, R.mipmap.ic_launcher, ticker, username, ticker.toString(), intent);
        }
    }

    /**
     * 显示与聊天消息的通知
     *
     * @return void
     * @throws
     * @Title: showNotify
     */
    public void showMsgNotify(Context context, BmobMsg msg) {
        // 更新通知栏
        int icon = R.mipmap.ic_launcher;
        String trueMsg = "";
        if (msg.getMsgType() == BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")) {
            trueMsg = "[表情]";
        } else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
            trueMsg = "[图片]";
        } else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
            trueMsg = "[语音]";
        } else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
            trueMsg = "[位置]";
        } else {
            trueMsg = msg.getContent();
        }
        CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
        String contentTitle = msg.getBelongUsername() + " (" + mNewNum + "条新消息)";

        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean isAllowVoice = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();

        BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice, isAllowVibrate, icon, tickerText.toString(), contentTitle, tickerText.toString(), intent);
    }
}
