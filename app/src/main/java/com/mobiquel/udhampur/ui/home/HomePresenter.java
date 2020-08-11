package com.mobiquel.udhampur.ui.home;

import com.mobiquel.udhampur.base.BasePresenter;

public class HomePresenter extends BasePresenter<HomeView> implements HomeModelListener{


    private HomeModel model;
    public HomePresenter(HomeView view) {
        super(view);
    }

    @Override
    protected void setModel() {
        model = new HomeModel(this);
    }

    @Override
    protected void destroy() {
        if (model != null)
            model.detachListener();
        model = null;
    }

    @Override
    protected void initView() {

    }

    void onLogout() {
        if (getView() != null) {

              /*  if (getView().isNetworkAvailable()) {
                    getView().showProgressBar();
                    model.logout();
                } else {
                    getView().showNoNetworkError();
                }*/

        }
    }


    public void setUpBottomNavigationView() {
        if (getView()!=null){
            getView().setUpBottomNavigationView();
        }
    }




    @Override
    public void handleLogoutResult() {
        if (getView() != null) {
            getView().hideProgressBar();
          /*  if (body != null) {
                if(body.getLogoutStatus().equals("success"))
                    getView().showLoginSignUp();
                else
                    getView().showErrorMessage();

            }*/
        }
    }
}