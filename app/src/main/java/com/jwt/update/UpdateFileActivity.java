package com.jwt.update;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.jwt.update.bean.OneLineSelectBean;
import com.jwt.update.bean.UpdateFile;
import com.jwt.update.bean.WebQueryResult;
import com.jwt.update.dao.GlobalMethod;
import com.jwt.update.dao.LoginDao;

import org.apache.http.HttpStatus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class UpdateFileActivity extends ListActivity {

    private UpdateFileActivity self;
    private List<OneLineSelectBean> listBeans;
    private List<UpdateFile> ufs;
    private static final int GET_UPFILE = 10;
    private ProgressDialog progressDialog = null;
    private List<UpdateFile> needUps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_two_btn_show_list);
        self = this;
        setTitle("系统更新检查");
        needUps = new ArrayList<UpdateFile>();
        // createZhcxMenus();
        listBeans = new ArrayList<OneLineSelectBean>();
        CommOnelineImgListAdapter adapter = new CommOnelineImgListAdapter(self,
                listBeans);
        getListView().setAdapter(adapter);

        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long arg3) {
                // 单选,修改其他为不选
                for (int i = 0; i < listBeans.size(); i++) {
                    OneLineSelectBean c = listBeans.get(i);
                    if (i == position)
                        c.setSelect(!c.isSelect());
                    else
                        c.setSelect(false);
                }
                CommOnelineImgListAdapter ad = (CommOnelineImgListAdapter) parent
                        .getAdapter();
                ad.notifyDataSetChanged();
            }
        });
        findViewById(R.id.btn_left).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startDownloadThread();
                    }
                });
        findViewById(R.id.btn_right).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        listBeans.clear();
                        ufs.clear();
                        CommOnelineImgListAdapter ad = (CommOnelineImgListAdapter) getListView()
                                .getAdapter();
                        ad.notifyDataSetChanged();
                        startReferView();
                    }
                });
        startReferView();
    }

    private void startReferView() {
        UpfileHandler handler = new UpfileHandler(self, GET_UPFILE);
        UpdateThread thread = new UpdateThread(handler, self);
        thread.start();
    }

    private int getSelectItem() {
        int position = -1;
        int i = 0;
        while (listBeans.size() > 0 && i < listBeans.size()) {
            if (listBeans.get(i).isSelect()) {
                position = i;
                break;
            }
            i++;
        }
        return position;
    }

    private void createContents() {
        if (listBeans == null)
            listBeans = new ArrayList<OneLineSelectBean>();
        else
            listBeans.clear();
        for (UpdateFile uf : ufs) {
            String versionName = uf.getVersionName();
            String content = uf.getFileName();
            content += " 大小：" + (Integer.valueOf(uf.getHashValue()) / 1024)
                    + "KB\n";
            content += "服务器版本：" + versionName;
            double version = LoginDao.getApkVerionName(uf.getPackageName(),
                    self);
            content += "当前版本：" + version;
            content += " "
                    + ((version < LoginDao.str2Double(versionName)) ? " X"
                    : " √");
            listBeans.add(new OneLineSelectBean(content));
        }
        //double sslVersion = LoginDao.getApkVerion("koal.ssl", self);
        //double vn = LoginDao.getApkVerionName("koal.ssl", self);
       // Log.e("koal.ssl", sslVersion+ "," + vn);
        //LoginDao.getInstalledApk(self);
        CommOnelineImgListAdapter ad = (CommOnelineImgListAdapter) getListView()
                .getAdapter();
        ad.notifyDataSetChanged();
    }

    static class UpfileHandler extends Handler {

        private final WeakReference<UpdateFileActivity> myActivity;
        private int cata;

        public UpfileHandler(UpdateFileActivity activity, int cata) {
            myActivity = new WeakReference<UpdateFileActivity>(activity);
            this.cata = cata;
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateFileActivity ac = myActivity.get();
            if (ac != null) {
                if (cata == GET_UPFILE) {
                    ac.referListView(msg);
                }
            }
        }
    }

    public void referListView(Message msg) {
        Bundle data = msg.getData();
        if (data == null)
            return;
        WebQueryResult<List<UpdateFile>> files = (WebQueryResult<List<UpdateFile>>) data
                .getSerializable("files");
        String err = getErrorMessageFromWeb(files);
        if (!TextUtils.isEmpty(err)) {
            Toast.makeText(self, err, Toast.LENGTH_LONG).show();
            return;
        }
        ufs = files.getResult();
        if (ufs == null)
            return;
        createContents();
    }

    public <E> String getErrorMessageFromWeb(WebQueryResult<E> webResult) {
        String err = "";
        if (webResult == null)
            return "网络连接失败，请检查配查或与管理员联系！";
        if (webResult.getStatus() != HttpStatus.SC_OK) {
            // 服务器返回数据正确性验证， 网络状态正常
            if (webResult.getStatus() == 204) {
                return "未查询到符合条件的记录！";
            } else if (webResult.getStatus() == 500) {
                return "该查询在服务器不能实现，请与管理员联系！";
            } else if (webResult.getStatus() == HttpStatus.SC_BAD_REQUEST) {
                return "网络连接错误";
            } else {
                return "服务器出现未知错误";
            }
        }

        if (webResult.getResult() == null) {
            return "未能获取数据";
        }
        return err;
    }

    private void startDownloadThread() {
        int pos = getSelectItem();
        if (pos < 0 || ufs == null || ufs.isEmpty()) {
            GlobalMethod.showErrorDialog("请选择一个文件操作", self);
            return;
        }
        if (needUps == null)
            needUps = new ArrayList<UpdateFile>();
        needUps.clear();
        UpdateFile f = ufs.get(pos);
        needUps.add(f);
        showProgress("正在下载文件");
        DownFileThread dfThread = new DownFileThread(new DownFileHandler(self,
                ConfigUpdateActivity.DOWNFILE));
        dfThread.doStart(needUps);
    }

    private void showProgress(String message) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(self);
        progressDialog.setTitle("提示");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMessage(message);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    static class DownFileHandler extends Handler {
        private final WeakReference<UpdateFileActivity> myActivity;
        private int cata;

        public DownFileHandler(UpdateFileActivity activity, int cata) {
            myActivity = new WeakReference<UpdateFileActivity>(activity);
            this.cata = cata;
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateFileActivity ac = myActivity.get();
            if (ac != null) {
                if (cata == ConfigUpdateActivity.DOWNFILE)
                    ac.operDownFileMessage(msg);

            }
        }
    }

    private void operDownFileMessage(Message msg) {
        int err = msg.arg1;
        int what = msg.what;
        int step = msg.arg2;
        if (err == DownFileThread.DOWNLOADING_APK) {
            progressDialog.setMessage("正在下载第" + (what + 1) + "个共"
                    + needUps.size() + "个文件");
            progressDialog.setProgress(step);
        } else {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (err == DownFileThread.DOWNLOAD_OK && needUps.size() > 0) {
                installApk(needUps.get(0));
            }
        }
    }

    private void installApk(UpdateFile apk) {
        File f = new File(ConfigUpdateActivity.outSideDir, apk.getFileName());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + f),
                "application/vnd.android.package-archive");
        // 安装的结果将返回进入验证
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("update active return", "" + requestCode + "/" + resultCode);
        if (requestCode == 100) {
            listBeans.clear();
            ufs.clear();
            CommOnelineImgListAdapter ad = (CommOnelineImgListAdapter) getListView()
                    .getAdapter();
            ad.notifyDataSetChanged();
            startReferView();
        }
    }

}
