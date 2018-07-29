package com.li.updateversion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.li.updateversion.bean.VersionBean;
import com.li.updateversion.util.HttpUrl;
import com.li.updateversion.util.LogUtil;
import com.li.updateversion.util.ToastManager;
import com.li.updateversion.util.Util;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

@SuppressLint("HandlerLeak")
public class CheckUpdateActivity extends Activity {
    private Button checkout_yes;// 立即更新按键
    private Button checkout_no;// 取消按键
    private TextView checkout_text;// 中间要显示的文本

    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 记录进度条数量 */
    private int progress;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;//展示下载进度的dialog
    private VersionBean versionBean;//从上个页面传递过来的
    private String fileName = "";//下载之后显示的应用名称

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        Intent intent = getIntent();
        if (intent != null) {
            //获取传递过来的版本信息
            versionBean = (VersionBean) intent.getSerializableExtra("versionBean");
        } else {
            //如果没有则直接finish掉当前activity
            CheckUpdateActivity.this.finish();
        }
        setContentView(R.layout.check_update_activity);
        init();
    }

    private void init() {
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText("检查更新");
        checkout_yes = (Button) findViewById(R.id.checkupdate_yes);
        checkout_yes.setText("立即更新");
        checkout_no = (Button) findViewById(R.id.checkupdate_no);
        checkout_no.setText("取消");
        checkout_text = (TextView) findViewById(R.id.checkupdate_text);
        checkout_text.setText("最新版本：" + versionBean.versionName + ","
                + "请点击更新！");
        checkout_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {// 跳转网址
                    if (Util.judgeInternet(CheckUpdateActivity.this)) {
                        showDownloadDialog();
                    }
                } catch (Exception e) {
                    LogUtil.e(CheckUpdateActivity.class,
                            "checkout_yes.setOnClickListener", e);
                }
            }
        });
        checkout_no.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CheckUpdateActivity.this.finish();
            }
        });
    }

    /**
     * 显示软件下载对话框
     */
    @SuppressWarnings("deprecation")
    private void showDownloadDialog() {
        try {
            LayoutInflater inflater = LayoutInflater
                    .from(CheckUpdateActivity.this);
            View v = inflater.inflate(R.layout.softupdate_progress, null);// 得到加载view
            LinearLayout layout = (LinearLayout) v
                    .findViewById(R.id.dialog_view);// 加载布局
            // main.xml中的ImageView
            mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
            TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
            tipTextView.setText(getString(R.string.down_loading));// 设置加载信息

            mDownloadDialog = new Dialog(CheckUpdateActivity.this,
                    R.style.loading_dialog);// 创建自定义样式dialog

            mDownloadDialog.setCancelable(false);// 不可以用“返回键”取消
            mDownloadDialog.setContentView(layout,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
            judePermission();
        } catch (Exception e) {
            LogUtil.e(CheckUpdateActivity.class, "showDownloadDialog()", e);
        }
    }

    //判断权限
    public void judePermission(){
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .rationale(null)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //获取权限成功
                        if (mDownloadDialog != null) {
                            mDownloadDialog.show();
                        }
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        //获取权限失败
                        if (AndPermission.hasAlwaysDeniedPermission(CheckUpdateActivity.this, permissions)) {
                            //直接跳转到权限设置页面
                            showSettingDialog(CheckUpdateActivity.this, permissions);
                        }
                    }
                })
                .start();
    }

    /**
     * 开始下载Apk
     */
    private void downLoadApk(){
        try {
            fileName = System.currentTimeMillis() + "download.apk";
            RequestParams requestParams = new RequestParams(versionBean.appAddress);
            requestParams.setSaveFilePath(HttpUrl.PATH + "/" + fileName);
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {
                    LogUtil.v(getClass(), "onWaiting---"  );
                }

                @Override
                public void onStarted() {
                    LogUtil.v(getClass(), "onStart---");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    LogUtil.v(getClass(), "onLoading---" + ":" + current + "/" + total);
                    // 计算进度条位置
                    progress = (int) (((float) current / total) * 100);
                    // 更新进度
                    handler.sendEmptyMessage(DOWNLOAD);
                }

                @Override
                public void onSuccess(File result) {
                    LogUtil.v(getClass(), "onSuccess---" + ":" + "downloaded:" + result.getPath());
                    handler.sendEmptyMessage(DOWNLOAD_FINISH);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ToastManager.showToast(CheckUpdateActivity.this, "onError---" + ":" + ex.toString());
                    LogUtil.v(getClass(), "onError---" + ":" + ex.toString());
                    if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
                        // 取消下载对话框显示
                        mDownloadDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(CancelledException cex) {
                    LogUtil.v(getClass(), "onCancelled---" + ":" + cex.toString());
                }

                @Override
                public void onFinished() {
                    LogUtil.v(getClass(), "onFinished---");
                }
            });
        } catch (Exception e) {
            LogUtil.e(getClass(), "downLoadApk", e);
        }
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DOWNLOAD ://下载当中
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH ://下载完成
                    if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
                        // 取消下载对话框显示
                        mDownloadDialog.dismiss();
                    }
                    // 安装文件
                    installApk();
                    break;
            }
        }
    };

    //展示是否要跳转到权限设置页面
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.please_open_permission_on_setting_str, TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context, R.style.permissition_setting_dialog)
                .setCancelable(true)
                .setTitle(R.string.permission_setting_str)
                .setMessage(message)
                .setPositiveButton(R.string.setting_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    //跳转到权限设置界面
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        judePermission();
                    }
                })
                .start();
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(HttpUrl.PATH, fileName);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(CheckUpdateActivity.this, getPackageName() + ".fileprovider", apkfile);
            i.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                    "application/vnd.android.package-archive");
        }
        CheckUpdateActivity.this.startActivity(i);
        CheckUpdateActivity.this.finish();
    }

}
