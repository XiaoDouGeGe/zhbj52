package com.itheima.zhbj52;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itheima.zhbj52.utils.PrefUtils;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private static final int[] mImageIds = new int[]{R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3};
    private ViewPager vpGuide;
    private ArrayList<ImageView> mImageViewList;
    private LinearLayout llPointGroup;//引导圆点的父控件
    private int mPointWidth;//圆点间的距离
    private View viewRedPoint;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题

        setContentView(R.layout.activity_guide);

        vpGuide = (ViewPager) findViewById(R.id.vp_guide);
        llPointGroup = (LinearLayout) findViewById(R.id.ll_point_group);
        viewRedPoint = findViewById(R.id.view_red_point);
        btnStart = (Button) findViewById(R.id.but_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新sp，表示已经展示了新手引导
                PrefUtils.setBoolean(GuideActivity.this,"is_user_guide_showed",true);

                //跳转到主页面
                startActivity(new Intent(GuideActivity.this,MainActivity.class));
                finish();
            }
        });

        initViews();

        vpGuide.setAdapter(new GuideAdapter());

        vpGuide.setOnPageChangeListener(new GuidePageListener());
    }

    /*
     * 初始化界面
     */
    private void initViews(){
        mImageViewList = new ArrayList<ImageView>();
        //初始化引导页的页面
        for (int i=0;i<mImageIds.length;i++){
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);//设置引导页背景
            mImageViewList.add(image);
        }

        //初始化引导页的小圆点
        for (int i=0;i<mImageIds.length;i++){
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);//设置引导页背景

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10,10);
            if(i>0){
                params.leftMargin = 10;//设置圆点间隔
            }
            point.setLayoutParams(params);//设置圆点的大小
            llPointGroup.addView(point);//将圆点添加给线性布局
        }



        //获取视图树
        llPointGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //当layout执行结束之后回调此方法
            @Override
            public void onGlobalLayout() {
                System.out.println("layout 结束");
                llPointGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //measure(测量大小)  layout（界面位置）  ondraw
                mPointWidth = llPointGroup.getChildAt(1).getLeft() - llPointGroup.getChildAt(0).getLeft();
                System.out.println(mPointWidth);
            }
        });
    }

    /**
     * PagerAdapter适配器
     */
    class GuideAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViewList.get(position));
            return mImageViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    class GuidePageListener implements ViewPager.OnPageChangeListener{

        //滑动事件
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            System.out.println("当前位置："+position+"；百分比："+positionOffset+"；移动距离："+positionOffsetPixels);
            int len = (int) (mPointWidth * positionOffset) + position * mPointWidth;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewRedPoint.getLayoutParams();//获得当前小红点的布局参数
            params.leftMargin = len;//设置左边距

            viewRedPoint.setLayoutParams(params);//重新给小红点设置布局参数
        }

        //某个页面被选中
        @Override
        public void onPageSelected(int position) {
            if (position == mImageIds.length - 1){ // 最后一个页面
                btnStart.setVisibility(View.VISIBLE); // 开始显示“体验按钮”
            }else{
                btnStart.setVisibility(View.INVISIBLE);
            }

        }

        //滑动状态发生改变
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
