package com.greentech.ixuanxiu.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.util.DensityUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.v3.listener.ThumbnailUrlListener;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class CourseAdapter extends UltimateViewAdapter {

    private Context context;
    private List<Course> courseMapList;

    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;

    public CourseAdapter(Context context, List<Course> courseMapList) {
        this.context = context;
        this.courseMapList = courseMapList;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= courseMapList.size() : position < courseMapList.size()) && (customHeaderView != null ? position > 0 : true)) {

            int p = customHeaderView != null ? position - 1 : position;
            final Course c = courseMapList.get(p);

            if (null != c.getCoverFile()) {
                if (null == context) {
                    Uri uri = Uri.parse((c.getCover()));
                    ((ViewHolder) holder).image.setImageURI(uri);
                } else {
                    Uri uri = Uri.parse((c.getCoverFile().getFileUrl(context)));
                    ((ViewHolder) holder).image.setImageURI(uri);
                }
            } else {
                Uri uri = Uri.parse((c.getCover()));
                ((ViewHolder) holder).image.setImageURI(uri);
            }

            ((ViewHolder) holder).name.setText(c.getName());
            ((ViewHolder) holder).teacher.setText(c.getTeacher());
            ((ViewHolder) holder).intro.setText(c.getIntro());
            ((ViewHolder) holder).number.setText(c.getCategory());

            // ((ViewHolder) holder).itemView.setActivated(selectedItems.get(position, false));
        }

    }

    @Override
    public int getAdapterItemCount() {
        return courseMapList.size();
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_list, parent, false);
        ViewHolder vh = new ViewHolder(v, mItemClickListener, mItemLongClickListener);
        return vh;
    }


    public void insert(Course course, int position) {
        insert(courseMapList, course, position);
    }

    public void remove(int position) {
        remove(courseMapList, position);
    }

    public void clear() {
        clear(courseMapList);
    }

    @Override
    public void toggleSelection(int pos) {
        super.toggleSelection(pos);
    }

    @Override
    public void setSelected(int pos) {
        super.setSelected(pos);
    }

    @Override
    public void clearSelection(int pos) {
        super.clearSelection(pos);
    }


    public void swapPositions(int from, int to) {
        swapPositions(courseMapList, from, to);
    }


    @Override
    public long generateHeaderId(int position) {
        // URLogs.d("position--" + position + "   " + getItem(position));
        if (getItem(position).getName().length() > 0)
            return getItem(position).getName().charAt(0);
        else return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.stick_header_item, viewGroup, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.name);


    }

    class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name;
        SimpleDraweeView image;
        TextView teacher;
        TextView number;
        TextView intro;
        LinearLayout item;

        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        public ViewHolder(View itemView, MyItemClickListener listener, MyItemLongClickListener longClickListener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            teacher = (TextView) itemView.findViewById(R.id.teacher);
            number = (TextView) itemView.findViewById(R.id.number);
            intro = (TextView) itemView.findViewById(R.id.intro);
            item = (LinearLayout) itemView.findViewById(R.id.item);

            this.mListener = listener;
            this.mLongClickListener = longClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongClickListener != null) {
                mLongClickListener.onItemLongClick(v, getPosition());
            }
            return true;
        }
    }

    public Course getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < courseMapList.size())
            return courseMapList.get(position);
        else return null;
    }

    /**
     * 设置item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(MyItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }
}
