package com.mobiquel.udhampur.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseActivity;
import com.mobiquel.udhampur.ui.home.HomeActivity;
import com.mobiquel.udhampur.ui.login.LoginActivity;

public class SplashActivity extends BaseActivity implements SplashView {

    private static final int SPLASH_HOLD_TIME = 2500;

    private SplashPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initVariables() {
        mPresenter = new SplashPresenter(this);

    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void showHomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.putExtra("POS","0" );
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_out, R.anim.right_in);
                    finish();
                }
            }
        }, SPLASH_HOLD_TIME);
    }

    @Override
    public void showLoginScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.putExtra("SOURCE","SPLASH");
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_out, R.anim.right_in);
                    finish();
                }
            }
        }, SPLASH_HOLD_TIME);

    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }
}
