package com.jwt.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jwt.update.bean.WebQueryResult;
import com.jwt.update.dao.DaoFactory;

public class LoginUpdateThread extends Thread {
	private Handler mHandler;
	private String mjjh;
	private String mm;
	private String serial;
	private boolean isCheckMd5 = true;
	private Context self;
	private ProgressDialog progressDialog;

	public LoginUpdateThread(Handler handler) {
		this.mHandler = handler;
	}

	public void doStart(String mjjh, String mm, String serial,
			boolean isCheckMd5, Context context) {
		// 显示进度对话框
		this.isCheckMd5 = isCheckMd5;
		this.self = context;
		progressDialog = new ProgressDialog(self);
		progressDialog.setTitle("提示");
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(isCheckMd5 ? "正在验证系统..." : "正在登录系统...");
		progressDialog.setMax(100);
		progressDialog.show();
		this.mjjh = mjjh;
		this.mm = mm;
		this.serial = serial;
		this.start();

	}

	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		// 目前处于登录阶段
		// int state = LOGIN_STATE;
		WebQueryResult<String> login = DaoFactory.getDao().checkUserAndUpdate(
				mjjh, mm, serial, isCheckMd5);
		progressDialog.setProgress(50);
		Message msg = mHandler.obtainMessage();
		Bundle data = new Bundle();
		data.putSerializable("login", login);
		msg.setData(data);
		mHandler.sendMessage(msg);
		progressDialog.dismiss();
	}
}
