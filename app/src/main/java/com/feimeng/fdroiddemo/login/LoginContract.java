package com.feimeng.fdroiddemo.login;

import com.feimeng.fdroid.mvp.FDPresenter;
import com.feimeng.fdroid.mvp.FDView;

public interface LoginContract {
    interface View extends FDView {

    }

    abstract class Presenter extends FDPresenter<View> {
        public abstract void login();
    }
}
