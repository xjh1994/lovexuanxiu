package com.greentech.ixuanxiu.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Banner;
import com.greentech.ixuanxiu.fragment.FragmentSearch;
import com.greentech.ixuanxiu.fragment.FragmentWebView;
import com.greentech.ixuanxiu.ui.WebViewActivity;
import com.jakewharton.salvage.RecyclingPagerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class BannerPagerAdapter extends RecyclingPagerAdapter {

    private Context       context;
    private List<Banner> imageIdList;

    private int           size;
    private boolean       isInfiniteLoop;

    public BannerPagerAdapter(Context context, List<Banner> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList;
        this.size = imageIdList.size();
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : imageIdList.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_banner, container, false);
            holder.imageView = (SimpleDraweeView) view.findViewById(R.id.image);
            holder.intro = (TextView) view.findViewById(R.id.intro);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
//        holder.imageView.setImageResource(imageIdList.get(getPosition(position)));
        Uri uri = Uri.parse(imageIdList.get(getPosition(position)).getImageSmall());
        holder.imageView.setImageURI(uri);
        holder.intro.setText(imageIdList.get(getPosition(position)).getName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, WebViewActivity.class));
//                ((MaterialNavigationDrawer)context).setFragmentChild(new FragmentWebView(), "搜索");
            }
        });

        return view;
    }

    private static class ViewHolder {

        SimpleDraweeView imageView;
        TextView intro;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public BannerPagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}
