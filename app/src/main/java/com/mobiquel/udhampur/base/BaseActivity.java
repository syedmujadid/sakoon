package com.mobiquel.udhampur.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.FailureResponse;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    private FrameLayout flBaseContainer;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        flBaseContainer = findViewById(R.id.fl_base_container);
        setLayout();
        ButterKnife.bind(this);

        initVariables();
        setListeners();

    }


    /**
     * Method to add child activity layout
     */
    private void setLayout() {
        if (getResourceId() != -1) {
            removeLayout();
            getLayoutInflater().inflate(getResourceId(), flBaseContainer, true);
        }
    }


    /**
     * Method to remove layouts
     */
    private void removeLayout() {
        if (flBaseContainer != null && flBaseContainer.getChildCount() >= 1)
            flBaseContainer.removeAllViews();
    }

    // returns layout resource id for child activity
    protected abstract int getResourceId();

    protected abstract void initVariables();

    protected abstract void setListeners();


    /**
     * method ot make activity full screen
     */
    protected void makeActivityFullscreen() {
        if (!isFinishing() && getWindow() != null) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * method ot make activity non full screen
     */
    protected void makeActivityNonFullscreen() {
        if (!isFinishing() && getWindow() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_VISIBLE
                );
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE)
                && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrCoords[] = new int[2];
            view.getLocationOnScreen(scrCoords);
            float x = ev.getRawX() + view.getLeft() - scrCoords[0];
            float y = ev.getRawY() + view.getTop() - scrCoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow((getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
        }
        return false;
    }


    @Override
    public void showSnackBar(String message) {
        if (!isFinishing() && message != null) {
            Snackbar snackbar = Snackbar.make(flBaseContainer, message, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            snackbar.show();
        }
    }


    @Override
    public void showProgressBar() {
        if (!isFinishing()) {
            hideProgressBar();
            mProgressDialog = new Dialog(this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_progressbar, null);
            mProgressDialog.setContentView(view);

            view.setVisibility(View.VISIBLE);
            mProgressDialog.setCancelable(false);

            if (mProgressDialog.getWindow() != null) {
                mProgressDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));
                mProgressDialog.getWindow().setDimAmount(0);
                mProgressDialog.getWindow().setGravity(Gravity.CENTER);
            }
            mProgressDialog.show();
        }
    }

    @Override
    public void hideProgressBar() {
        if (!isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    @Override
    public void showNoNetworkError() {
        if (!isFinishing()) {
            showSnackBar(getString(R.string.msg_check_internet_connection));
        }
    }


    @Override
    public void showSpecificError(FailureResponse failureResponse) {
        if (failureResponse != null) {
            showSnackBar(failureResponse.getMessage());
        }
    }

}
