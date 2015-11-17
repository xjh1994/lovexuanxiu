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
import com.greentech.ixuanxiu.bean.Comment;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.MyUser;

import java.util.List;

import butterknife.Bind;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class CommentThreeAdapter extends BaseAdapter {

    private Context context;
    private List<Comment> commentList;
    private ViewHolder holder;

    public CommentThreeAdapter(Context context, List<Comment> commentList) {
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
                    R.layout.item_comment_list, parent, false);
            holder = new ViewHolder();
            holder.image = (SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.rating = (RatingBar) convertView.findViewById(R.id.rating);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = commentList.get(position);
        MyUser myUser = comment.getMyUser();
        Course course = comment.getCourse();
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
        holder.rating.setRating(comment.getRating());
        holder.time.setText(comment.getCreatedAt().substring(5, 10));
        holder.content.setText(TextUtils.isEmpty(comment.getContent()) ? context.getString(R.string.text_say_nothing) : comment.getContent());

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView image;
        TextView username;
        RatingBar rating;
        TextView time;
        TextView content;
        LinearLayout item;
    }
}
