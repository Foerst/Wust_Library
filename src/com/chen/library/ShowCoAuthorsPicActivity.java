package com.chen.library;

import java.security.PublicKey;
import java.util.ArrayList;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.CustomDialog.ViewContainer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowCoAuthorsPicActivity extends Activity {
	final String urlPathString = "http://1.arbortest.sinaapp.com/1/arbortest/draw.jsp?authors=";
	private WebView webView;
	private ImageView backImageView;
	private Dialog dialog;
	private TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_coauthors_pic);
//		final View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
//    	dialog = new Dialog(this, R.style.selectorDialog);
//    	dialog.setContentView(view);
		TextView titleTextView=null;
		ViewContainer container=CustomDialogUtils.getViewContainer(this);
		container.getTextView().setText("Мгдижа...");
		dialog=container.getDialog();
		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowCoAuthorsPicActivity.this.finish();
			}
		});

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras().getBundle("bundle");
		ArrayList<String> dataArrayList = bundle
				.getStringArrayList("coAuthors");

		String tString = "";
		for (int i = 0; i < dataArrayList.size(); i++) {
			tString += dataArrayList.get(i).toString() + ",";

		}
		tString = tString.substring(0, tString.length() - 1);
		System.out.print("------------>" + tString);

		webView = (WebView) this.findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		if (webView != null) {
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					dialog.dismiss();
				}
			});

			loadUrl(urlPathString + tString);

		}
		// webView.getSettings().setJavaScriptEnabled(true);

		// webView.loadUrl(urlPathString + tString);

	}

	public void loadUrl(String url) {
		if (webView != null) {
			webView.loadUrl(url);
			dialog.show();
			webView.reload();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			this.finish();

		}

		return false;

	}
}
