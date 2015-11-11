package com.greentech.ixuanxiu.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.ui.CourseDetailActivity;

import java.util.List;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class RecommendCourseListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseList;
    private ViewHolder holder;

    public RecommendCourseListAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_course_list, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.image = (SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.teacher = (TextView) convertView.findViewById(R.id.teacher);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.intro = (TextView) convertView.findViewById(R.id.intro);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Course c = courseList.get(position);

        if (null != c.getCoverFile()) {
            if (null == context) {
                Uri uri = Uri.parse((c.getCover()));
                holder.image.setImageURI(uri);
            } else {
                Uri uri = Uri.parse((c.getCoverFile().getFileUrl(context)));
                holder.image.setImageURI(uri);
            }
        } else {
            Uri uri = Uri.parse((c.getCover()));
            holder.image.setImageURI(uri);
        }

        holder.name.setText(c.getName());
        holder.teacher.setText(c.getTeacher());
        holder.intro.setText(c.getIntro());
        holder.number.setText(c.getCategory());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable("course", c);
                Intent intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView name;
        SimpleDraweeView image;
        TextView teacher;
        TextView number;
        TextView intro;
        LinearLayout item;
    }
}
