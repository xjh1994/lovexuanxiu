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

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.PostComment;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.util.TimeUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.text.ParseException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class PostCommentAdapter extends UltimateViewAdapter {

    private Context context;
    private List<PostComment> postCommentList;

    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;

    public PostCommentAdapter(Context context, List<PostComment> postCommentList) {
        this.context = context;
        this.postCommentList = postCommentList;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= postCommentList.size() : position < postCommentList.size()) && (customHeaderView != null ? position > 0 : true)) {
            int p = customHeaderView != null ? position - 1 : position;
            PostComment c = postCommentList.get(p);
//            Post post = c.getPost();
            MyUser myUser = c.getMyUser();
            String content = c.getContent();

            if (null != myUser.getAvatarFile()) {
                if (null == context) {
                    Uri uri = Uri.parse((myUser.getAvatarSmall()));
                    ((ViewHolder) holder).image.setImageURI(uri);
                } else {
                    Uri uri = Uri.parse((myUser.getAvatarFile().getFileUrl(context)));
                    ((ViewHolder) holder).image.setImageURI(uri);
                }
            } else {
                Uri uri = Uri.parse((myUser.getAvatarSmall()));
                ((ViewHolder) holder).image.setImageURI(uri);
            }
            ((ViewHolder) holder).username.setText(myUser.getNick());
            ((ViewHolder) holder).content.setText(content);
            try {
                ((ViewHolder) holder).time.setText(TimeUtils.friendlyFormat(TimeUtils.getDateFromString(c.getCreatedAt())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // ((ViewHolder) holder).itemView.setActivated(selectedItems.get(position, false));
        }

    }

    @Override
    public int getAdapterItemCount() {
        return postCommentList.size();
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_comment_list, parent, false);
        ViewHolder vh = new ViewHolder(v, mItemClickListener, mItemLongClickListener);
        return vh;
    }


    public void insert(PostComment post, int position) {
        insert(postCommentList, post, position);
    }

    public void remove(int position) {
        remove(postCommentList, position);
    }

    public void clear() {
        clear(postCommentList);
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
        swapPositions(postCommentList, from, to);
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
        
        SimpleDraweeView image;
        TextView username;
        TextView time;
        TextView content;
//        LinearLayout item;

        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        public ViewHolder(View itemView, MyItemClickListener listener, MyItemLongClickListener longClickListener) {
            super(itemView);

            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            username = (TextView) itemView.findViewById(R.id.username);
            time = (TextView) itemView.findViewById(R.id.time);
            content = (TextView) itemView.findViewById(R.id.content);

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

    public PostComment getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < postCommentList.size())
            return postCommentList.get(position);
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
