package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.os.Bundle;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Category;
import com.greentech.ixuanxiu.bean.Constant;
import com.greentech.ixuanxiu.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import me.gujun.android.taggroup.TagGroup;

/**
 * Created by xjh1994 on 2015/11/6.
 */
public class CategoryActivity extends BaseActivity {
    /**
     * 课程分类
     */

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        BmobQuery<Constant> query = new BmobQuery<>();
        query.addWhereEqualTo("name", "category");
        query.findObjects(this, new FindListener<Constant>() {
            @Override
            public void onSuccess(List<Constant> list) {
                if (list.size() > 0) {
                    String[] tags = list.get(0).getValue().split(",");
                    String[] temp = new String[] {"..."};

                    try {
                        tagGroup.setTags(Utils.concat(tags, temp));
                    } catch (NullPointerException e) {

                    }
                    tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
                        @Override
                        public void onTagClick(String tag) {
                            Bundle data = new Bundle();
                            if (tag.equals("...")) {
                                data.putString("category", "all");
                            } else {
                                data.putString("category", tag);
                            }
                            Intent intent = new Intent(CategoryActivity.this, CourseListActivity.class);
                            intent.putExtras(data);
                            startActivity(intent);
                        }
                    });
                } else {
                    toast("获取分类失败");
                }
            }

            @Override
            public void onError(int i, String s) {
                toast("获取分类失败 " + s);
            }
        });
    }

    @Bind(R.id.tag_group)
    TagGroup tagGroup;
}
