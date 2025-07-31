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
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.pojo.PartyListModel;

import java.util.ArrayList;
import java.util.List;

public class UpdatePartyMaterialDalog extends Dialog {

    private Context mContext;
    private DialogListener dialogListener;
    private PartyListModel model;

    public UpdatePartyMaterialDalog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_update_party_material);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        //setCancelable(false);
        setCanceledOnTouchOutside(true);
    }

    R.id.btnUpdate
    private void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.btnUpdate:
                dialogListener.onPositiveButtonClick();
                break;

        }
    }

    public void setData(PartyListModel model,String type) {
        label.setText("Update "+type);
        this.model=model;
        et_name.setText(model.getName());
        et_description.setText(model.getDescription());
        btnUpdate.setText("Update");
    }

    public void setData(String type) {
        label.setText("Add "+type);
        btnUpdate.setText("Add");

    }
    public void setDesVisibility() {
        et_description.setVisibility(View.GONE);

    }
    public void emptyData() {
        label.setText("");
        btnUpdate.setText("");

    }
    public List<String> checkSolution() {
        List<String> data = new ArrayList<>();
        if (et_name.getText().toString().equals("")) {
            //Utils.showToast(mContext, "Please enter current password!");
            data.add("Please enter name!");

        }  else {
            data.add(et_name.getText().toString());
            data.add(et_description.getText().toString());

        }

        return data;
    }

}
