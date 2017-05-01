package com.jwt.update;

import java.util.List;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.update.bean.UpdateFile;
import com.jwt.update.dao.DaoFactory;
import com.jwt.update.dao.LoginDao;
import com.jwt.update.dao.UpdateFileDao;

public class DownFileThread extends Thread {
	private Handler mHandler;
	private List<UpdateFile> needUps;

	public final static int ALL_RIGHT = 100;
	public final static int NETWORK_ERROR = 0;
	public final static int LOG_UNPASS = 1;
	public final static int DTAT_NOT_OK = 2;
	public final static int IS_NEW_VERSION = 3;
	public final static int NOT_NEED_INSTALL = 4;
	public final static int NO_SDCARD = 5;
	public final static int DOWNLOAD_FAIL = 6;
	public final static int DOWNLOAD_OK = 7;
	public final static int START_DOWN_APK = 8;
	public final static int DOWNLOADING_APK = 9;
	public final static int DOWN_APK_STATE = 20;

	public DownFileThread(Handler handler) {
		this.mHandler = handler;
	}

	public void doStart(List<UpdateFile> needUps) {
		this.needUps = needUps;
		this.start();
	}

	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		UpdateFileDao dao = DaoFactory.getDao();
		int writeCount = 0;
		int i = 0;
		UpdateFile owner = null;
		for (UpdateFile uf : needUps) {
			if (TextUtils.equals("com.jwt.update", uf.getPackageName())) {
				owner = uf;
				break;
			}
		}
		if (owner != null) {
			needUps.clear();
			needUps.add(owner);
		}
		Log.e("Need update", "Need update " + needUps.size());
		for (UpdateFile uf : needUps) {
			int writeByte = dao.downloadApkFile(uf,
					ConfigUpdateActivity.outSideDir, mHandler, i);
			if (writeByte > 0)
				writeCount++;
			i++;
		}
		int errorMessage = (writeCount == needUps.size()) ? DOWNLOAD_OK
				: DOWNLOAD_FAIL;
		LoginDao.sendData(mHandler, errorMessage, DOWN_APK_STATE, 0);
	}

}
