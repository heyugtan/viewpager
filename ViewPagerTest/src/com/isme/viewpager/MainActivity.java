package com.isme.viewpager;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnPageChangeListener{

	// 控件
	private ViewPager viewPager;
	private TextView tvTitle;
	private LinearLayout layoutDots;

	// 图片
	private int[] imgId;;
	private List<ImageView> imageList;

	// 文字
	private List<String> titleList;
	
	// 小点
	private List<View> dotsList;
	private View dot;
	
	private int oldPosition;
	private int currentPosition;
	
	private ViewPagerAdapter adapter;
	
	private Runnable runnable;
	private int autoChangeTime = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 轮播图片
		viewPager = (ViewPager) findViewById(R.id.vp_ad);
		viewPager.setOnPageChangeListener(this);
		// 文字
		tvTitle = (TextView) findViewById(R.id.tv_title);
		// 小点
		layoutDots = (LinearLayout) findViewById(R.id.layout_dots);

		initData();
		
		pagerPlay();
	}

	/**
	 * -------------------自动播放 pager
	 */
	private void pagerPlay() {
		runnable = new Runnable() {
			
			@Override
			public void run() {
				oldPosition = viewPager.getCurrentItem();
				int next = (oldPosition) +1;
				if(next >= adapter.getCount())
				{
					next = 0;
				}
				
				handlerPager.sendEmptyMessage(next);
			}
		};
		
		handlerPager.postDelayed(runnable, autoChangeTime);
	}

	private void initData() {

		initImage();
		initTitle();
		initDots();

		tvTitle.setText(titleList.get(0));
		
		adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
	}

	/**
	 * ------------------------------------初始化 小点
	 */
	private void initDots() {
		dotsList = new ArrayList<View>();
		
		LinearLayout.LayoutParams dotsParams = new LinearLayout.LayoutParams(40, 12);
		dotsParams.setMargins(12, 3, 12, 3);
		
		for(int i=0; i<imgId.length; i++)
		{
			dot = new View(this);
			dot.setLayoutParams(dotsParams);
			if(0 == i)
			{
				dot.setBackgroundResource(R.drawable.dot_focus);
			}
			else{
				dot.setBackgroundResource(R.drawable.dot_normal);
			}
			dotsList.add(dot);
			
			// 给小点设点击事件
			dotsList.get(i).setId(i);
			dotsList.get(i).setOnClickListener(new DotClickListener());
			
			layoutDots.addView(dotsList.get(i));
		}
	}

	/**
	 * ----------------------------------初始化 轮播 标题
	 */
	private void initTitle() {
		titleList = new ArrayList<String>();
		for(int i=0; i<imgId.length; i++)
		{
			titleList.add("第"+i+"张");
		}
	}

	/**
	 * --------------------------------初始化 轮播图片
	 */
	private void initImage() {
		imgId = new int[] { R.drawable.order, R.drawable.tran, R.drawable.yue,
				R.drawable.intro };

		imageList = new ArrayList<ImageView>();
		for (int i = 0; i < imgId.length; i++) {
			ImageView img = new ImageView(this);
			img.setImageResource(imgId[i]);
			imageList.add(img);
		}

		// 最后单个图片的点击事件
		ImageView image = imageList.get(imageList.size() - 1);
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, JumpActivity.class));
			}
		});
	}

	/**
	 * viewPager的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(imageList.get(position));
			return imageList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageList.get(position));
		}

	}

	/**
	 * ----------------------------小点 的点击事件
	 * 								点击哪一个点 跳转到那个图片
	 */
	class DotClickListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			int position = v.getId();
			Log.i("isme", String.valueOf(position));
			setCurrentView(position);
		}
	}
	
	/**
	 * -----------------------------viewPager 轮播的监听
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		setCurrentPage(position);
		handlerPager.removeCallbacks(runnable);
		handlerPager.postDelayed(runnable, autoChangeTime);
	}

	/**
	 * 	--------------------------设置当前界面
	 * @param position
	 */
	private void setCurrentView(int position) {
		if(position < 0)
		{
			return ;
		}
		viewPager.setCurrentItem(position);
	}
	
	/**
	 * 	-------------------------改变Pager页面     标题  小点
	 * @param position
	 */
	private void setCurrentPage(int position) {
		currentPosition = position;
		for(int i=0; i<imageList.size(); i++)
		{
			if(currentPosition == i)
			{
				tvTitle.setText(titleList.get(currentPosition));
				dotsList.get(i).setBackgroundResource(R.drawable.dot_focus);
			}
			else{
				dotsList.get(i).setBackgroundResource(R.drawable.dot_normal);
			}
		}
	}
	
	
	@SuppressLint("HandlerLeak") 
	private Handler handlerPager = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			setCurrentPage(msg.what);
			viewPager.setCurrentItem(msg.what);
		}
	};
	
	@Override
	protected void onDestroy() {
		if(runnable != null){
			handlerPager.removeCallbacks(runnable);
		}
		
		super.onDestroy();
	};
	
}
