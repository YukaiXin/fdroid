package com.feimeng.fdroid.mvp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feimeng.fdroid.R;
import com.feimeng.fdroid.utils.ActivityPageManager;
import com.feimeng.fdroid.widget.FDLoadingDialog;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

/**
 * Activity基类
 * Created by feimeng on 2017/1/20.
 *
 * @param <V> 视图
 * @param <P> 控制器
 * @param <D> 初始化结果
 */
public abstract class FDActivity<V extends FDView<D>, P extends FDPresenter<V, D>, D> extends RxAppCompatActivity implements FDView<D> {
    protected P mPresenter;

    /**
     * 对话框
     */
    private Dialog mLoading; // 加载弹窗
    private int mLoadCount; // 加载次数

    /**
     * 实例化控制器
     */
    protected abstract P initPresenter();

    private boolean mStarted;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPageManager.getInstance().addActivity(this);
        // 绑定控制器
        if ((mPresenter = initPresenter()) != null) mPresenter.attach((V) this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        afterContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        afterContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        afterContentView();
    }

    @Override
    public void init(D initData, Throwable e) {
    }

    /**
     * 获取关联的RxLifecycle
     */
    public <T> LifecycleTransformer<T> getLifecycle(@NonNull ActivityEvent event) {
        return bindUntilEvent(event);
    }

    /**
     * 绘制对话框
     * 一般用于网络访问时显示(子类可重写，使用自定义对话框)
     *
     * @param message 提示的信息
     * @return Dialog 对话框
     */
    protected Dialog createLoadingDialog(@Nullable String message) {
        return new FDLoadingDialog(this, R.style.Theme_AppCompat_Dialog, message);
    }

    protected void updateLoadingDialog(@Nullable Dialog dialog, @Nullable String message) {
        if (dialog != null) ((FDLoadingDialog) dialog).updateLoadingDialog(message);
    }

    /**
     * 显示对话框
     */
    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    public void showLoadingDialog(String message) {
        showLoadingDialog(message, true);
    }

    /**
     * 显示对话框 showLoadingDialog()和hideLoadingDialog()必须成对调用
     */
    public synchronized void showLoadingDialog(String message, boolean cancelable) {
        mLoadCount++;
        if (mLoading == null) {
            mLoading = createLoadingDialog(message);
            mLoading.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mPresenter != null) mPresenter.onDialogDismiss();
                    updateLoadingDialog(null, null);
                    mLoading = null;
                    mLoadCount = 0;
                }
            });
        } else {
            updateLoadingDialog(mLoading, message);
        }
        mLoading.setCancelable(cancelable);
        mLoading.show();
    }

    /**
     * 隐藏对话框
     */
    public synchronized void hideLoadingDialog() {
        mLoadCount = Math.max(0, mLoadCount - 1);
        if (mLoadCount > 0) return;
        if (mLoading != null) {
            mLoading.setOnDismissListener(null);
            mLoading.dismiss();
            mLoading = null;
        }
    }

    public synchronized void cancelLoadingDialog() {
        mLoadCount = 1;
        hideLoadingDialog();
    }


    /**
     * 拿到最新Activity
     *
     * @return BaseActivity
     */
    public static FDActivity getLatestActivity() {
        return ActivityPageManager.getInstance().currentActivity();
    }

    /**
     * 结束所有Activity
     */
    public static void finishAll() {
        ActivityPageManager.getInstance().finishAllActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStarted = false;
    }

    public boolean isStarted() {
        return mStarted;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LoadCount", mLoadCount);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLoadCount = savedInstanceState.getInt("LoadCount");
    }

    @Override
    protected void onDestroy() {
        if (mLoading != null) {
            mLoading.dismiss();
            mLoading = null;
        }
        super.onDestroy();
        ActivityPageManager.getInstance().removeActivity(this);
        // 解绑控制器
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
    }

    private void afterContentView() {
        if (mPresenter != null && mPresenter.isActive()) mPresenter.afterContentView();
    }
}
