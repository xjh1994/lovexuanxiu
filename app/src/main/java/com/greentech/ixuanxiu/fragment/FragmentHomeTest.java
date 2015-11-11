package com.greentech.ixuanxiu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Banner;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Hot;
import com.greentech.ixuanxiu.bean.Nosign;
import com.greentech.ixuanxiu.bean.Recommend;
import com.greentech.ixuanxiu.ui.CourseDetailActivity;
import com.greentech.ixuanxiu.ui.CourseListActivity;
import com.greentech.ixuanxiu.ui.SearchCourseActivity;
import com.greentech.ixuanxiu.ui.SettingActivity;
import com.greentech.ixuanxiu.ui.WebViewActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class FragmentHomeTest extends FragmentBase {
    /**
     * 主界面
     */

    ImageLoader imageLoader = ImageLoader.getInstance();

    public static final int LIMIT = 3;

    List<Course> course = new ArrayList<>();
    List<Hot> hot = new ArrayList<>();
    List<Recommend> recommend = new ArrayList<>();
    List<Nosign> nosign = new ArrayList<>();

    View view;

    private SliderLayout sliderLayout;
    //    private TextView            indexText;
    private List<Banner> imageIdList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_home_test, container, false);
            ButterKnife.bind(this, view);
            initView(view);
        }

        return view;
    }

    private void initView(View view) {
        more0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("category", "hot");
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("category", "nosign");
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        more2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("category", "recommend");
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        more4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CourseListActivity.class));
            }
        });
        initData();
    }

    private void initViewPager(final View view) {
        /*Banner banner = new Banner();
        banner.setName("梅西");
        banner.setImageSmall("http://7d9r49.com1.z0.glb.clouddn.com/thumb_540_220_1401181512508.jpg");
        banner.save(getActivity());*/

        sliderLayout = (SliderLayout) view.findViewById(R.id.slider);
        sliderLayout.stopAutoCycle();
//        indexText = (TextView) view.findViewById(R.id.view_pager_index);

        BmobQuery<Banner> query = new BmobQuery<>();
        query.setLimit(6);
        query.order("order");
        query.findObjects(getActivity(), new FindListener<Banner>() {
            @Override
            public void onSuccess(final List<Banner> list) {
                if (list.size() > 0) {
                    imageIdList = new ArrayList<Banner>();
                    imageIdList.addAll(list);

                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        TextSliderView textSliderView = new TextSliderView(getActivity());
                        textSliderView
                                .description(list.get(i).getName())
                                .image(list.get(i).getImageSmall())
                                .setScaleType(BaseSliderView.ScaleType.Fit);
                        final int finalI = i;
                        textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {
                                Bundle data = new Bundle();
                                data.putString("url", list.get(finalI).getUrl());
                                data.putString("title", list.get(finalI).getName());
                                if (!TextUtils.isEmpty(list.get(finalI).getContent())) {
                                    data.putString("content", list.get(finalI).getName());
                                }
                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                intent.putExtras(data);
                                startActivity(intent);
                            }
                        });

                        sliderLayout.addSlider(textSliderView);
                    }
//                    sliderLayout.setCurrentPosition(0, true);
                    sliderLayout.setDuration(4000);
//                    sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                    sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
//                    sliderLayout.setCustomAnimation(new DescriptionAnimation());
                    sliderLayout.startAutoCycle(4000, 4000, false);
                }
            }

            @Override
            public void onError(int i, String s) {
                toast("幻灯加载失败 " + s);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
//            int p = (position) % imageIdList.size();
//            indexText.setText(imageIdList.get(p).getName());
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // start auto scroll when onResume
        if (null != sliderLayout) {
            sliderLayout.startAutoCycle(4000, 4000, true);
        }
    }

    @Override
    public void onStop() {
        // stop auto scroll when onStop
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    public void initData() {
        BmobQuery<Course> query0 = new BmobQuery<>();
        query0.addWhereEqualTo("state", 0);
        query0.order("-createdAt");
        query0.setLimit(LIMIT);
        query0.findObjects(getActivity(), new FindListener<Course>() {
            @Override
            public void onSuccess(List<Course> list) {
                if (list.size() > 0) {
                    hot.clear();
                    for (Course c : list) {
                        course.add(c);
                    }
                    if (null != handler) {
                        handler.sendEmptyMessage(4);
                    }
                } else {

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        BmobQuery<Hot> query = new BmobQuery<>();
        BmobQuery<Course> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("state", 0);
        query.addWhereMatchesQuery("course", "Course", innerQuery);
        query.order("-createdAt");
        query.include("course");
        query.setLimit(LIMIT);
        query.findObjects(getActivity(), new FindListener<Hot>() {
            @Override
            public void onSuccess(List<Hot> list) {
                if (list.size() > 0) {
                    hot.clear();
                    for (Hot c : list) {
                        hot.add(c);
                    }
                    if (null != handler) {
                        handler.sendEmptyMessage(0);
                    }
                } else {

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        BmobQuery<Recommend> query1 = new BmobQuery<>();
        query1.addWhereMatchesQuery("course", "Course", innerQuery);
        query1.order("-createdAt");
        query1.include("course");
        query1.setLimit(LIMIT);
        query1.findObjects(getActivity(), new FindListener<Recommend>() {
            @Override
            public void onSuccess(List<Recommend> list) {
                if (list.size() > 0) {
                    recommend.clear();
                    for (Recommend c : list) {
                        recommend.add(c);
                    }
                    if (null != handler) {
                        handler.sendEmptyMessage(1);
                    }
                } else {

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        BmobQuery<Nosign> query2 = new BmobQuery<>();
        query2.addWhereMatchesQuery("course", "Course", innerQuery);
        query2.order("-createdAt");
        query2.include("course");
        query2.setLimit(LIMIT);
        query2.findObjects(getActivity(), new FindListener<Nosign>() {
            @Override
            public void onSuccess(List<Nosign> list) {
                if (list.size() > 0) {
                    nosign.clear();
                    for (Nosign c : list) {
                        nosign.add(c);
                    }
                    if (null != handler) {
                        handler.sendEmptyMessage(2);
                    }
                } else {

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        initViewPager(view);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0:
                        name0.setText(hot.get(0).getCourse().getName());
                        Uri uri0 = Uri.parse(hot.get(0).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image0.setImageURI(uri0);
                        name1.setText(hot.get(1).getCourse().getName());
                        Uri uri1 = Uri.parse(hot.get(1).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image1.setImageURI(uri1);
                        name2.setText(hot.get(2).getCourse().getName());
                        Uri uri2 = Uri.parse(hot.get(2).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image2.setImageURI(uri2);

                        layout0.setOnClickListener(onClickListener);
                        layout1.setOnClickListener(onClickListener);
                        layout2.setOnClickListener(onClickListener);
                        break;
                    case 1:
                        name10.setText(recommend.get(0).getCourse().getName());
                        Uri uri10 = Uri.parse(recommend.get(0).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image10.setImageURI(uri10);
                        name11.setText(recommend.get(1).getCourse().getName());
                        Uri uri11 = Uri.parse(recommend.get(1).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image11.setImageURI(uri11);
                        name12.setText(recommend.get(2).getCourse().getName());
                        Uri uri12 = Uri.parse(recommend.get(2).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image12.setImageURI(uri12);

                        layout10.setOnClickListener(onClickListener);
                        layout11.setOnClickListener(onClickListener);
                        layout12.setOnClickListener(onClickListener);
                        break;
                    case 2:
                        name20.setText(nosign.get(0).getCourse().getName());
                        Uri uri20 = Uri.parse(nosign.get(0).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image20.setImageURI(uri20);
                        name21.setText(nosign.get(1).getCourse().getName());
                        Uri uri21 = Uri.parse(nosign.get(1).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image21.setImageURI(uri21);
                        name22.setText(nosign.get(2).getCourse().getName());
                        Uri uri22 = Uri.parse(nosign.get(2).getCourse().getCoverFile().getFileUrl(getActivity()));
                        image22.setImageURI(uri22);

                        layout20.setOnClickListener(onClickListener);
                        layout21.setOnClickListener(onClickListener);
                        layout22.setOnClickListener(onClickListener);
                        break;
                    case 4:
                        try {
                            name40.setText(course.get(0).getName());
                            Uri uri40 = Uri.parse(course.get(0).getCoverFile().getFileUrl(getActivity()));
                            image40.setImageURI(uri40);
                            name41.setText(course.get(1).getName());
                            Uri uri41 = Uri.parse(course.get(1).getCoverFile().getFileUrl(getActivity()));
                            image41.setImageURI(uri41);
                            name42.setText(course.get(2).getName());
                            Uri uri42 = Uri.parse(course.get(2).getCoverFile().getFileUrl(getActivity()));
                            image42.setImageURI(uri42);
                        } catch (NullPointerException e) {

                        }

                        layout40.setOnClickListener(onClickListener);
                        layout41.setOnClickListener(onClickListener);
                        layout42.setOnClickListener(onClickListener);
                        break;
                    default:
                        break;
                }
            } catch (IndexOutOfBoundsException e) {

            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle data = new Bundle();
            switch (v.getId()) {
                case R.id.layout0:
                    data.putSerializable("course", hot.get(0).getCourse());
                    break;
                case R.id.layout1:
                    data.putSerializable("course", hot.get(1).getCourse());
                    break;
                case R.id.layout2:
                    data.putSerializable("course", hot.get(2).getCourse());
                    break;
                case R.id.layout10:
                    data.putSerializable("course", recommend.get(0).getCourse());
                    break;
                case R.id.layout11:
                    data.putSerializable("course", recommend.get(1).getCourse());
                    break;
                case R.id.layout12:
                    data.putSerializable("course", recommend.get(2).getCourse());
                    break;
                case R.id.layout20:
                    data.putSerializable("course", nosign.get(0).getCourse());
                    break;
                case R.id.layout21:
                    data.putSerializable("course", nosign.get(1).getCourse());
                    break;
                case R.id.layout22:
                    data.putSerializable("course", nosign.get(2).getCourse());
                    break;
                case R.id.layout40:
                    data.putSerializable("course", course.get(0));
                    break;
                case R.id.layout41:
                    data.putSerializable("course", course.get(1));
                    break;
                case R.id.layout42:
                    data.putSerializable("course", course.get(2));
                    break;
                default:
                    break;
            }
            Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("签到").setIcon(R.drawable.ic_today).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("搜索").setIcon(R.drawable.ic_search).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getActivity(), SearchCourseActivity.class));
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("设置").setIcon(R.drawable.ic_setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    @Bind(R.id.more0)
    TextView more0;
    @Bind(R.id.name0)
    TextView name0;
    @Bind(R.id.name1)
    TextView name1;
    @Bind(R.id.name2)
    TextView name2;
    @Bind(R.id.more1)
    TextView more1;
    @Bind(R.id.name10)
    TextView name10;
    @Bind(R.id.name11)
    TextView name11;
    @Bind(R.id.name12)
    TextView name12;
    @Bind(R.id.more2)
    TextView more2;
    @Bind(R.id.name20)
    TextView name20;
    @Bind(R.id.name21)
    TextView name21;
    @Bind(R.id.name22)
    TextView name22;
    @Bind(R.id.image0)
    ImageView image0;
    @Bind(R.id.image1)
    ImageView image1;
    @Bind(R.id.image2)
    ImageView image2;
    @Bind(R.id.image10)
    ImageView image10;
    @Bind(R.id.image11)
    ImageView image11;
    @Bind(R.id.image12)
    ImageView image12;
    @Bind(R.id.image20)
    ImageView image20;
    @Bind(R.id.image21)
    ImageView image21;
    @Bind(R.id.image22)
    ImageView image22;

    @Bind(R.id.layout0)
    LinearLayout layout0;
    @Bind(R.id.layout1)
    LinearLayout layout1;
    @Bind(R.id.layout2)
    LinearLayout layout2;
    @Bind(R.id.layout20)
    LinearLayout layout20;
    @Bind(R.id.layout21)
    LinearLayout layout21;
    @Bind(R.id.layout22)
    LinearLayout layout22;
    @Bind(R.id.layout10)
    LinearLayout layout10;
    @Bind(R.id.layout11)
    LinearLayout layout11;
    @Bind(R.id.layout12)
    LinearLayout layout12;

    @Bind(R.id.layout40)
    LinearLayout layout40;
    @Bind(R.id.layout41)
    LinearLayout layout41;
    @Bind(R.id.layout42)
    LinearLayout layout42;
    @Bind(R.id.image40)
    ImageView image40;
    @Bind(R.id.image41)
    ImageView image41;
    @Bind(R.id.image42)
    ImageView image42;
    @Bind(R.id.more4)
    TextView more4;
    @Bind(R.id.name40)
    TextView name40;
    @Bind(R.id.name41)
    TextView name41;
    @Bind(R.id.name42)
    TextView name42;

}
