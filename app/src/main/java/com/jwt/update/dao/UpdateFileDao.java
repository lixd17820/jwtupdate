package com.jwt.update.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.update.ConfigUpdateActivity;
import com.jwt.update.DownFileThread;
import com.jwt.update.bean.UpdateFile;
import com.jwt.update.bean.WebQueryResult;
import com.parser.xml.CommParserXml;

public abstract class UpdateFileDao {

	protected final int timeoutConnection = 30000;
	protected final int timeoutSocket = 30000;

	// public String u;

	protected String url = "ydjw/services/login/updateFileVersion";
	protected String checkUrl = "ydjw/services/login/checkJwtUser";
	protected String checkMd5 = "ydjw/services/ydjw/checkJwtUserNew";

	/**
	 * 最近的验证、更新、警员信息一次请求返回的方法
	 * 
	 * @param yhbh
	 * @param mm
	 * @param sbid
	 * @return
	 */
	public WebQueryResult<String> checkUserAndUpdate(String yhbh, String mm,
			String sbid, boolean isCheckMd5) {
		WebQueryResult<String> result = new WebQueryResult<String>();
		result.setStatus(HttpStatus.SC_BAD_REQUEST);
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost request = new HttpPost(getUrl()
				+ (isCheckMd5 ? checkMd5 : checkUrl));
		// writeDisk(request.getURI().toString());
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("yhbh", yhbh));
		postParams.add(new BasicNameValuePair("sbid", sbid));
		postParams.add(new BasicNameValuePair("mm", mm));
		try {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParams, "utf-8");
			request.addHeader("Content-type", "application/xml");
			request.setEntity(formEntity);
			HttpResponse response = httpclient.execute(request);
			int status = response.getStatusLine().getStatusCode();
			result.setStatus(status);
			// writeDisk("return code", "return code is " + status);
			// 返回数据正常
			String html = "";
			if (status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent(), "utf-8"));
					String s = null;
					while ((s = reader.readLine()) != null) {
						html += s;
					}
					result.setResult(html);

				}
				Log.e("UpdateFileDao",html);
			}

		} catch (Exception e) {
			// writeDisk(e.toString());
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		return result;
	}

	/**
	 * 向新245服务器调用版本更新查询
	 *
	 * @return
	 */
	public WebQueryResult<List<UpdateFile>> updateInfoRestful() {
		WebQueryResult<List<UpdateFile>> res = new WebQueryResult<List<UpdateFile>>();
		int status = HttpStatus.SC_BAD_REQUEST;
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpGet request = new HttpGet(getUrl() + url);
		try {
			HttpResponse response = httpclient.execute(request);
			status = response.getStatusLine().getStatusCode();
			Log.e("return code", "return code is " + status);
			// 返回数据正常
			if (status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent(), "utf-8"));
					String s = null;
					String result = "";
					while ((s = reader.readLine()) != null) {
						result += s;
					}
					Log.e("RestfulWebServiceDao", result);
					if (!TextUtils.isEmpty(result)) {
						List<UpdateFile> g = CommParserXml.ParseXmlToListObj(
								result, UpdateFile.class);
						res.setResult(g);
					}
				}
			}
		} catch (ClientProtocolException e1) {
			Log.e("exception", "ClientProtocolException");
			e1.printStackTrace();
		} catch (IOException e1) {
			Log.e("exception", "IOException");
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		res.setStatus(status);
		return res;
	}

	private int downFile(String url, File file) {
		int writeByte = 0;
		int status = HttpStatus.SC_BAD_REQUEST;
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpClient client = new DefaultHttpClient(httpParameters);

		HttpGet request = new HttpGet(url);
		try {

			HttpResponse response = client.execute(request);
			status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				// long fileSize = response.getEntity().getContentLength();
				BufferedInputStream in = new BufferedInputStream(response
						.getEntity().getContent());

				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(file));
				byte[] b = new byte[1024];
				int l = 0;
				while ((l = in.read(b)) > 0) {
					out.write(b, 0, l);
					writeByte += l;
				}
				out.flush();
				in.close();
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writeByte;
	}

	public int downloadApkFile(UpdateFile uf, String dir, Handler handler,
			int fileIndex) {
		String fileId = uf.getId();
		String fileName = uf.getFileName();
		int fileSize = Integer.valueOf(uf.getHashValue());
		String pack = uf.getPackageName();
		// 文件过大，分块下载
		if (fileSize > 100 * 1024) {
			return downloadSmallFile(fileId, fileName, fileSize, pack, dir,
					handler, fileIndex);
		}
		int writeByte = 0;
		int status = HttpStatus.SC_BAD_REQUEST;
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpClient client = new DefaultHttpClient(httpParameters);

		HttpGet request = new HttpGet(getFileUrl() + pack);
		try {

			HttpResponse response = client.execute(request);
			status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				// long fileSize = response.getEntity().getContentLength();
				BufferedInputStream in = new BufferedInputStream(response
						.getEntity().getContent());

				File file = new File(dir);
				if (!file.exists())
					file.mkdirs();
				file = new File(file, fileName);
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(file));
				byte[] b = new byte[1024 * 1024];
				int l = 0;
				while ((l = in.read(b)) > 0) {
					out.write(b, 0, l);
					writeByte += l;
					int step = writeByte * 100 / fileSize;
					LoginDao.sendData(handler, DownFileThread.DOWNLOADING_APK,
							fileIndex, step);
				}
				out.flush();
				in.close();
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writeByte;
	}

	private int downloadSmallFile(String fileId, String fileName, int fileSize,
			String pack, String dir, Handler handler, int fileIndex) {
		File fdir = new File(dir);
		if (!fdir.exists())
			fdir.mkdirs();
		List<File> files = new ArrayList<File>();
		int count = fileSize / (100 * 1024) + 1;
		for (int i = 0; i < count; i++) {
			String url = getFileUrl() + pack + "&index=" + i;
			File sf = new File(dir, "temp_" + i);
			int wb = downFile(url, sf);
			boolean isOk = false;
			if (i < count - 1) {
				isOk = wb == 100 * 1024;
			} else {
				isOk = wb == fileSize - i * 100 * 1024;
			}
			LoginDao.sendData(handler, DownFileThread.DOWNLOADING_APK,
					fileIndex, (i + 1) * 100 / count);
			if (isOk)
				files.add(sf);
			else
				return 0;
		}
		if (files.size() != count)
			return 0;
		int writeByte = 0;
		File bigFile = new File(fdir, fileName);
		try {
			BufferedOutputStream bout = new BufferedOutputStream(
					new FileOutputStream(bigFile));
			byte[] b = new byte[1024];
			for (File file : files) {
				BufferedInputStream bin = new BufferedInputStream(
						new FileInputStream(file));
				int len = 0;
				while ((len = bin.read(b)) > 0) {
					bout.write(b, 0, len);
					writeByte += len;
				}
				bout.flush();
				bin.close();
			}
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writeByte;
	}

	public abstract String getUrl();

	public abstract String getFileUrl();

	// public static void writeDisk(String message) {
	// File f = new File("/mnt/sdcard/log.txt");
	// try {
	// FileWriter fw = new FileWriter(f, true);
	// fw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	// .format(new Date()) + "\n");
	// fw.write(message + "\n");
	// fw.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// public static void writeDisk(String tag, String message) {
	// File f = new File("/mnt/sdcard/log.txt");
	// try {
	// FileWriter fw = new FileWriter(f, true);
	// fw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	// .format(new Date()) + "\n");
	// fw.write(tag + "\n");
	// fw.write(message + "\n");
	// fw.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

}
