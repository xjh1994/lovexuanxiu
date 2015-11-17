package com.greentech.ixuanxiu.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.PostComment;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.util.TimeUtils;

import java.text.ParseException;
import java.util.List;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class PostCommentListAdapter extends BaseAdapter {

    private Context context;
    private List<PostComment> commentList;
    private ViewHolder holder;

    public PostCommentListAdapter(Context context, List<PostComment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_comment_post_list, parent, false);
            holder = new ViewHolder();
            holder.image = (SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PostComment comment = commentList.get(position);
        MyUser myUser = comment.getMyUser();
        Uri uri = null;
        if (null != myUser.getAvatarFile()) {
            if (null != context) {
                uri = Uri.parse(myUser.getAvatarFile().getFileUrl(context));
            } else {
                uri = Uri.parse(Config.logo_url);
            }
        } else if (!TextUtils.isEmpty(myUser.getAvatarSmall())) {
            uri = Uri.parse(myUser.getAvatarSmall());
        } else {
            uri = Uri.parse(Config.logo_url);
        }
        holder.image.setImageURI(uri);
        holder.username.setText(myUser.getNick());
        try {
            holder.time.setText(TimeUtils.friendlyFormat(TimeUtils.getDateFromString(comment.getCreatedAt())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.content.setText(TextUtils.isEmpty(comment.getContent()) ? context.getString(R.string.text_say_nothing) : comment.getContent());

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView image;
        TextView username;
        TextView time;
        TextView content;
        LinearLayout item;
    }
}
