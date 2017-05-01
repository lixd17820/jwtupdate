package com.jwt.update;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jwt.update.bean.UpdateFile;
import com.jwt.update.bean.WebQueryResult;
import com.jwt.update.dao.DaoFactory;

public class UpdateThread extends Thread {
	private Handler mHandler;
	private Context self;

	public UpdateThread(Handler handler, Context context) {
		this.mHandler = handler;
	}

	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		// 目前处于登录阶段
		// int state = LOGIN_STATE;
		WebQueryResult<List<UpdateFile>> files = DaoFactory.getDao()
				.updateInfoRestful();
		Message msg = mHandler.obtainMessage();
		Bundle data = new Bundle();
		data.putSerializable("files", files);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
}
