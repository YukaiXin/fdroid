package com.feimeng.fdroiddemo.base;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.mvp.FDApp;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.SP;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.BuildConfig;
import com.feimeng.fdroiddemo.data.Constants;
import com.feimeng.fdroiddemo.mvp.model.file.FileManager;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Author: Feimeng
 * Time:   2016/3/18
 * Description: 全局Application
 */
public class BaseApp extends FDApp implements Consumer<Throwable> {
    /**
     * 配置 在UI线程调用
     */
    @Override
    protected void config() {
        // 初始化 Log
        L.init(BuildConfig.DEBUG, L.V);
        // 初始化 SharedPreferences
        SP.init(this, Constants.SP_NAME);
        // 初始化Toast
        T.init(true);
        FDConfig.SHOW_HTTP_LOG = BuildConfig.DEBUG;
        FDConfig.SHOW_HTTP_EXCEPTION_INFO = BuildConfig.DEBUG;
        FDConfig.READ_TIMEOUT = 120;
        FDConfig.WRITE_TIMEOUT = 120;
        FDConfig.CONNECT_TIMEOUT = 30;
        FDConfig.INFO_EOF_EXCEPTION = "数据格式异常";
        FDConfig.INFO_TIMEOUT_EXCEPTION = "请求超时";
        FDConfig.INFO_CONNECT_EXCEPTION = "无法连接服务器";
        FDConfig.INFO_UNKNOWN_EXCEPTION = "抱歉！遇到错误了";
    }

    /**
     * 配置 在子线程调用
     */
    @Override
    protected void configAsync() {
        RxJavaPlugins.setErrorHandler(this); // 处理RxJava取消订阅后抛出的异常
        FileManager.init(this); // 文件管理器
    }

    @Override
    public void accept(Throwable throwable) {
        // 处理RxJava取消订阅后抛出的异常
        throwable.printStackTrace();
    }

    /**
     * APP销毁时调用
     */
    public void onAppDestroy() {
    }
}