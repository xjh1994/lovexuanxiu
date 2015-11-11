package com.greentech.ixuanxiu.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.bean.PostComment;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.util.TimeUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.sql.Time;
import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class PostAdapter extends UltimateViewAdapter {

    private Context context;
    private List<Post> postList;

    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= postList.size() : position < postList.size()) && (customHeaderView != null ? position > 0 : true)) {

            int p = customHeaderView != null ? position - 1 : position;
            final Post c = postList.get(p);

            if (null != c.getMyUser().getAvatarFile()) {
                if (null == context) {
                    Uri uri = Uri.parse((c.getMyUser().getAvatarSmall()));
                    ((ViewHolder) holder).image.setImageURI(uri);
                } else {
                    Uri uri = Uri.parse((c.getMyUser().getAvatarFile().getFileUrl(context)));
                    ((ViewHolder) holder).image.setImageURI(uri);
                }
            } else {
                Uri uri = Uri.parse((c.getMyUser().getAvatarSmall()));
                ((ViewHolder) holder).image.setImageURI(uri);
            }

            ((ViewHolder) holder).username.setText(c.getMyUser().getNick());
            ((ViewHolder) holder).title.setText(c.getName());
            try {
                ((ViewHolder) holder).time.setText(TimeUtils.friendlyFormat(TimeUtils.getDateFromString(c.getCreatedAt())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Spanned text = Html.fromHtml(c.getContent());
            ((ViewHolder) holder).content.setText(text);
            BmobQuery<PostComment> query = new BmobQuery<>();
            BmobQuery<Post> innerQuery = new BmobQuery<>();
            innerQuery.addWhereEqualTo("objectId", c.getObjectId());
            query.addWhereMatchesQuery("post", "Post", innerQuery);
            query.count(context, PostComment.class, new CountListener() {
                @Override
                public void onSuccess(int i) {
                    ((ViewHolder) holder).commentCount.setText(String.valueOf(i));
                }

                @Override
                public void onFailure(int i, String s) {
                    ((ViewHolder) holder).commentCount.setText("0");
                }
            });

            // ((ViewHolder) holder).itemView.setActivated(selectedItems.get(position, false));
        }

    }

    @Override
    public int getAdapterItemCount() {
        return postList.size();
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_list, parent, false);
        ViewHolder vh = new ViewHolder(v, mItemClickListener, mItemLongClickListener);
        return vh;
    }


    public void insert(Post course, int position) {
        insert(postList, course, position);
    }

    public void remove(int position) {
        remove(postList, position);
    }

    public void clear() {
        clear(postList);
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
        swapPositions(postList, from, to);
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

        TextView username;
        TextView title;
        SimpleDraweeView image;
        TextView time;
        TextView content;
        TextView commentCount;
        LinearLayout item;

        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        public ViewHolder(View itemView, MyItemClickListener listener, MyItemLongClickListener longClickListener) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            time = (TextView) itemView.findViewById(R.id.time);
            content = (TextView) itemView.findViewById(R.id.content);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
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

    public Post getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < postList.size())
            return postList.get(position);
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
