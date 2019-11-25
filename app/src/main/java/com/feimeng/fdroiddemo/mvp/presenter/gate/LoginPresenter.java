package com.feimeng.fdroiddemo.mvp.presenter.gate;

import com.feimeng.fdroid.mvp.model.api.bean.ApiFinish;
import com.feimeng.fdroid.utils.FastTask;
import com.feimeng.fdroiddemo.data.dto.LoginDto;
import com.feimeng.fdroiddemo.mvp.model.api.ApiWrapper;

public class LoginPresenter extends LoginContract.Presenter {
    @Override
    public void loginBySync(String phone, String password) {
        new FastTask<String>() {
            @Override
            public String task() throws Exception {
                // 模拟耗时操作
                Thread.sleep(1000);
                // 发起同步登录请求
                LoginDto loginDto = ApiWrapper.getInstance().login_(phone, password);
                return "欢迎您！" + loginDto.nickname;
            }
        }.runIO(ApiWrapper.getInstance().subscriber(new ApiFinish<String>() {
            @Override
            public void start() {
                showDialog();
            }

            @Override
            public void success(String result) {
                if (isActive()) mView.login(result, null);
            }

            @Override
            public void fail(Throwable error, String info) {
                if (isActive()) mView.login(null, info);
            }

            @Override
            public void stop() {
                hideDialog();
            }
        }));
    }

    @Override
    public void loginByAsync(String phone, String password) {
        // 发起异步登录请求
        ApiWrapper.getInstance().login(phone, password)
                .map(loginDto -> "欢迎您！" + loginDto.nickname)
                .subscribe(ApiWrapper.getInstance().subscriber(new ApiFinish<String>() {
                    @Override
                    public void start() {
                        showDialog();
                    }

                    @Override
                    public void success(String result) {
                        if (isActive()) mView.login(result, null);
                    }

                    @Override
                    public void fail(Throwable error, String info) {
                        if (isActive()) mView.login(null, info);
                    }

                    @Override
                    public void stop() {
                        hideDialog();
                    }
                }));
    }
}
