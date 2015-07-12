package com.chen.library;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.library.Login.LoginUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainPageActivity extends Activity {
	private ImageView libSearchImageView;
	private TextView databaseSearchTextView;
	private ImageView dblpSearchImageView;
	private TextView mylibTextView;
	private TextView paperSearchTextView;
	private ImageButton showMenuButton;
	private SlidingMenu menu;
	private TextView txtUserInfo;
	private TextView txtBorrowInfo;
	private TextView txtLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_page);
		
		showMenuButton = (ImageButton) this.findViewById(R.id.showMenu);

		menu = new SlidingMenu(this);// 直接new，而不是getSlidingMenu

		menu.setMode(SlidingMenu.RIGHT);

		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		menu.setShadowDrawable(R.drawable.shadow);

		menu.setBehindWidth(400);// 设置SlidingMenu菜单的宽度

		menu.setFadeDegree(0.35f);

		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);// 必须调用

		menu.setMenu(R.layout.menu_layout_left);// 就是通俗的layout布局
		txtUserInfo = (TextView) menu.findViewById(R.id.txtUserInfo);
		txtBorrowInfo = (TextView) menu.findViewById(R.id.txtBorrowInfo);
		txtLogout = (TextView) menu.findViewById(R.id.txtLogout);
		txtLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MainPageActivity.this.finish();
			}
		});

		txtBorrowInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (LoginUtils.isLogin) {

					MainPageActivity.this
							.startActivity(new Intent(MainPageActivity.this,
									CurrentBorrowActivity.class));
					;

				} else {
					Toast.makeText(MainPageActivity.this, "请先登录！",
							Toast.LENGTH_SHORT).show();
					MainPageActivity.this.startActivity(new Intent(
							MainPageActivity.this, LoginActivity.class));
					;
				}
			}
		});
		txtUserInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (LoginUtils.isLogin) {

					MainPageActivity.this.startActivity(new Intent(
							MainPageActivity.this, MyInfomationActivity.class));
					;

				} else {
					Toast.makeText(MainPageActivity.this, "请先登录！",
							Toast.LENGTH_SHORT).show();
					MainPageActivity.this.startActivity(new Intent(
							MainPageActivity.this, LoginActivity.class));
					;
				}
			}
		});
		showMenuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				menu.showMenu();

			}
		});

		libSearchImageView = (ImageView) findViewById(R.id.wust_search_img);
		databaseSearchTextView = (TextView) findViewById(R.id.database_search_txt);
		dblpSearchImageView = (ImageView) findViewById(R.id.dblp_search_img);
		mylibTextView = (TextView) findViewById(R.id.mylib_txt);
		paperSearchTextView=(TextView) findViewById(R.id.search_paper_txt);
		paperSearchTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainPageActivity.this,
						SearchPaperActivity.class);
				MainPageActivity.this.startActivity(intent);
			}
		});
		libSearchImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainPageActivity.this,
						SearchBookActivity.class);
				MainPageActivity.this.startActivity(intent);
			}
		});
		databaseSearchTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainPageActivity.this,
						EAuthorSearchActivity.class);
				MainPageActivity.this.startActivity(intent);
			}
		});
		dblpSearchImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainPageActivity.this,
						CDblpSearchActivity.class);
				MainPageActivity.this.startActivity(intent);
			}
		});
		mylibTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intentMylib = new Intent(MainPageActivity.this,
						MyLibraryActivity.class);
				Intent intentLogin= new Intent(MainPageActivity.this,
						LoginActivity.class);
				if (LoginUtils.isLogin) {
					MainPageActivity.this.startActivity(intentMylib);
				} else {
					MainPageActivity.this.startActivity(intentLogin);
				}
			}
		});
	}
}
