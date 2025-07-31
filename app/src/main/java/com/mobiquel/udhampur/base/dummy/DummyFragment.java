package com.mobiquel.udhampur.base.dummy;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseFragment;

public class DummyFragment extends BaseFragment implements DummyView {

    private DummyPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPresenter = new DummyPresenter(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void initVariables() {

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        }

}
