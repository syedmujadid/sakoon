package com.mobiquel.udhampur.base;

import android.os.Bundle;
import android.view.View;

import com.mobiquel.udhampur.pojo.FailureResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Created by Navjot Singh
 * on 2/3/19.
 * this Fragment class is the parent class for all the fragments in the application
 * it contains methods to be used by child fragments
 */

public abstract class BaseFragment extends Fragment implements BaseView {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }

    public abstract void initVariables();

    public abstract void setListeners();

    public boolean isFragmentAdded() {
        return (getActivity() != null && !getActivity().isFinishing() && isAdded());
    }

    @Override
    public void showNoNetworkError() {
        if (isFragmentAdded())
            ((BaseActivity) getActivity()).showNoNetworkError();
    }

    @Override
    public void showSnackBar(String message) {
        if (isFragmentAdded())
            ((BaseActivity) getActivity()).showSnackBar(message);
    }

    @Override
    public void showProgressBar() {
        if (isFragmentAdded())
            ((BaseActivity) getActivity()).showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        if (isFragmentAdded()) {
            ((BaseActivity) getActivity()).hideProgressBar();
        }
    }

    @Override
    public void showSpecificError(FailureResponse failureResponse) {
        if (isFragmentAdded()) {
            ((BaseActivity) getActivity()).showSpecificError(failureResponse);
        }
    }

    @Override
    public boolean isNetworkAvailable() {
        return (isFragmentAdded() && ((BaseActivity) getActivity()).isNetworkAvailable());
    }

}