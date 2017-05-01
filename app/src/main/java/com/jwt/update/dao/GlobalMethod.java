package com.jwt.update.dao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

public class GlobalMethod {

	public static void showDialogTwoListener(String title, String message,
			String bt1, String bt2,
			android.content.DialogInterface.OnClickListener listener,
			Context context) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton(bt1, listener);
		ad.setNegativeButton(bt2, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		ad.setCancelable(false);
		ad.show();
	}

	public static void showDialogWithListener(String title, String message,
			String bt1,
			android.content.DialogInterface.OnClickListener listener,
			Context context) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton(bt1, listener);
		ad.setCancelable(false);
		ad.show();
	}

	/**
	 * 显示一个对话框
	 * 
	 * @param title
	 * @param message
	 * @param bt
	 * @param context
	 */
	public static void showDialog(String title, String message, String bt,
			Context context) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		String button1String = bt;
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton(button1String, new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		ad.setCancelable(true);
		ad.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				// eatenByGrue();
			}
		});
		ad.show();
	}

	public static void showErrorDialog(String message, Context context) {
		showDialog("错误信息", message, "确定", context);
	}
}
