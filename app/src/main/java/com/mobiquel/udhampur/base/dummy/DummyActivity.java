package com.mobiquel.udhampur.base.dummy;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mobiquel.udhampur.base.BaseActivity;

public class DummyActivity extends BaseActivity implements DummyView{

    private DummyPresenter mPresenter;

    @Override
    protected int getResourceId() {
        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new DummyPresenter(this);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void setListeners() {

    }
}
