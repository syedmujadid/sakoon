package com.mobiquel.udhampur.ui.home.listcase;

import com.mobiquel.udhampur.base.BasePresenter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListDataPresenter extends BasePresenter<ListDataView> implements ListDataModelListener {

    private ListDataModel model;

    public ListDataPresenter(ListDataView view) {
        super(view);
    }

    @Override
    protected void setModel() {
        model = new ListDataModel(this);
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


    public void getListData() {
        if (getView()!=null){
            if (getView().isNetworkAvailable()){
                getView().showProgressBar();
                model.getListData("0");
            }
            else
            {
                getView().showNoNetworkError();
            }
        }
    }
    public void getDataFromDAO() {
        if (getView()!=null){
          getView().getListOfUsers();
        }
    }
    public void getListOfUsers() {
        if (getView()!=null){
            if (getView().isNetworkAvailable()){
                getView().getListOfUsers();

            }
            else
            {
                getView().showNoNetworkError();
            }
        }
    }


    @Override
    public void handleListResult(JSONObject body) {
        if (getView()!=null){

        }
    }

    @Override
    public void handleFailureResponse() {
        if (getView()!=null){
           getView().showSnackBar("Error Loading data!");
        }
    }
}
