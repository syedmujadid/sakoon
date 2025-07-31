package com.mobiquel.udhampur.ui.login;

import com.mobiquel.udhampur.base.BasePresenter;

public class LoginPresenter extends BasePresenter<LoginView> implements LoginAPIModelListener {

   // private SplashModel model;

    public LoginPresenter(LoginView view) {
        super(view);
    }

    @Override
    protected void setModel() {
       // model = new SplashModel(this);
    }

    @Override
    protected void destroy() {
        /*if (model != null)
            model.detachListener();
        model = null;*/
    }

    @Override
    protected void initView() {
        if (getView() != null) {
        }
    }

    public void signIn(){
        if(getView()!=null){
            if(getView().isNetworkAvailable()){
                getView().callLoginAPI();
            }
            else {
                getView().showNoNetworkError();
            }
        }
    }
    public void getPwd(){
        if(getView()!=null){
            if(getView().isNetworkAvailable()){
                getView().callGetPwdAPI();
            }
            else {
                getView().showNoNetworkError();
            }
        }
    }

}
