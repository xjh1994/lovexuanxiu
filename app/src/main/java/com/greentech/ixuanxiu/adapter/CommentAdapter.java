package com.greentech.ixuanxiu.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Comment;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class CommentAdapter extends UltimateViewAdapter {

    private Context context;
    private List<Comment> commentList;

    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= commentList.size() : position < commentList.size()) && (customHeaderView != null ? position > 0 : true)) {
            int p = customHeaderView != null ? position - 1 : position;
            Comment c = commentList.get(p);
            Course course = c.getCourse();
            MyUser myUser = c.getMyUser();
            String content = c.getContent();
            Float rating = c.getRating();

            Uri uri = Uri.parse(course.getCover());
            ((ViewHolder) holder).image.setImageURI(uri);
            ((ViewHolder) holder).username.setText(myUser.getNick());
            ((ViewHolder) holder).content.setText(content);
            ((ViewHolder) holder).rating.setRating(rating);
            ((ViewHolder) holder).time.setText(c.getCreatedAt().substring(5, 10));

            // ((ViewHolder) holder).itemView.setActivated(selectedItems.get(position, false));
        }

    }

    @Override
    public int getAdapterItemCount() {
        return commentList.size();
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment_list, parent, false);
        ViewHolder vh = new ViewHolder(v, mItemClickListener, mItemLongClickListener);
        return vh;
    }


    public void insert(Comment course, int position) {
        insert(commentList, course, position);
    }

    public void remove(int position) {
        remove(commentList, position);
    }

    public void clear() {
        clear(commentList);
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
        swapPositions(commentList, from, to);
    }


    @Override
    public long generateHeaderId(int position) {
        // URLogs.d("position--" + position + "   " + getItem(position));
        if (getItem(position).getContent().length() > 0)
            return getItem(position).getContent().charAt(0);
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
        
        @Bind(R.id.image)
        SimpleDraweeView image;
        @Bind(R.id.username)
        TextView username;
        @Bind(R.id.rating)
        RatingBar rating;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.item)
        LinearLayout item;

        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        public ViewHolder(View itemView, MyItemClickListener listener, MyItemLongClickListener longClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mListener = listener;
            this.mLongClickListener = longClickListener;

            item.setOnClickListener(this);
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

    public Comment getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < commentList.size())
            return commentList.get(position);
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
