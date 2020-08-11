package com.mobiquel.udhampur.ui.splash;

import com.mobiquel.udhampur.base.BasePresenter;

public class SplashPresenter extends BasePresenter<SplashView> implements SplashModelListener {

    private SplashModel model;

    public SplashPresenter(SplashView view) {
        super(view);
    }

    @Override
    protected void setModel() {
        model = new SplashModel(this);
    }

    @Override
    protected void destroy() {
        if (model != null)
            model.detachListener();
        model = null;
    }

    @Override
    protected void initView() {
        if (getView() != null) {
            showNextScreen();
        }
    }

    public void showNextScreen() {
        if (getView() != null && model != null) {
            if (model.isUserLoggedIn()) {
                getView().showHomeScreen();
            } else {
                getView().showLoginScreen();

            }

        }
    }

}
