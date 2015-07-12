package com.chen.library;

import java.util.ArrayList;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.CustomDialog.ViewContainer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class ViewAcademicPaperContentsActivity extends Activity {
	private WebView webView;
	private ImageView backImageView;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_academic_paper_contents);

		Intent intent = getIntent();
		String urlPathString = intent.getExtras().getString("contentsUrl");

		ViewContainer container = CustomDialogUtils.getViewContainer(this);
		container.getTextView().setText("Мгдижа...");
		dialog = container.getDialog();
		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ViewAcademicPaperContentsActivity.this.finish();
			}
		});

		webView = (WebView) this.findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		if (webView != null) {
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					dialog.dismiss();
				}
			});

			loadUrl(urlPathString);

		}
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
