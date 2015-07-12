package com.chen.library.CustomDialog;

import com.chen.library.R;
import com.chen.library.ShowCoAuthorsPicActivity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class CustomDialogUtils {
	
	public static ViewContainer getViewContainer(Context context)
	{
		ViewContainer container=new ViewContainer();
		Dialog dialog=null;
		final View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
		TextView titleTextView=(TextView)view.findViewById(R.id.txt_title);
    	dialog = new Dialog(context, R.style.selectorDialog);
    	dialog.setContentView(view);
    	container.setDialog(dialog);
    	container.setTextView(titleTextView);
		return container;
	}

}
