package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.pojo.LocationListModel;
import com.mobiquel.udhampur.ui.CustomSpinnerAdapter_Model;

import java.util.ArrayList;
import java.util.List;

public class UpdatePartyDialog extends Dialog {

    private Context mContext;
    private DialogListener dialogListener;
    private List<LocationListModel> mLocationList;

    public UpdatePartyDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_update_user);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        //setCancelable(false);
        setCanceledOnTouchOutside(true);
    }

    R.id.btn_sign_in
    private void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.btn_sign_in:
                dialogListener.onPositiveButtonClick();
                break;

        }
    }

    public List<String> checkSolution() {
        List<String> data = new ArrayList<>();
        if (etUserName.getText().toString().equals("")) {
            //Utils.showToast(mContext, "Please enter current password!");
            data.add("Please enter name!");

        } else {
            data.add(etUserName.getText().toString());
            data.add(etDescription.getText().toString());
            data.add(mLocationList.get(locationSpinner.getSelectedItemPosition()).getLocationId());
            data.add(mLocationList.get(locationSpinner.getSelectedItemPosition()).getLocationName());
        }

        return data;
    }

    public void setData(List<LocationListModel> locationListModelList, String username, String mobile,String locId) {
        etUserName.setText(username);
        etDescription.setText(mobile);
        this.mLocationList = locationListModelList;
        CustomSpinnerAdapter_Model adapter = new CustomSpinnerAdapter_Model(mContext, locationListModelList);
        locationSpinner.setAdapter(adapter);
        for(int i=0;i<locationListModelList.size();i++){
            if(locationListModelList.get(i).getLocationId().equals(locId))
                locationSpinner.setSelection(i);
        }
    }
    public void setAddPartyData(){
        btnSignIn.setText("ADD");
        label.setText("ADD PARTY");
        etUserMobile.setVisibility(View.GONE);
        etDescription.setVisibility(View.VISIBLE);
    }
    public void setUpdatePartyData(){
        btnSignIn.setText("Update");
        label.setText("UPDATE PARTY");
    }
    public void setAddData(List<LocationListModel> locationListModelList) {
        btnSignIn.setText("ADD");
        label.setText("ADD USER");
        this.mLocationList = locationListModelList;
        CustomSpinnerAdapter_Model adapter = new CustomSpinnerAdapter_Model(mContext, locationListModelList);
        locationSpinner.setAdapter(adapter);

    }
}
