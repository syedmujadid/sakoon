package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mobiquel.udhampur.pojo.SlipListModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UpdateSlipDialog extends Dialog {

    private Context mContext;
    private List<String> unitOptions, partyOptions, materialOptions;
    private DialogListener dialogListener;
    private SlipListModel model;

    public UpdateSlipDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_update_slip);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        //setCancelable(false);
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    totalAmount.setText("Total Amount: 0");
                } else {
                    if (rate.getText().toString().equals("")) {
                        totalAmount.setText("Total Amount: 0");
                    } else {
                        DecimalFormat df = new DecimalFormat("0.00");
                        float quantity, ratevalue, finalValue;
                        if (editable.toString().contains("."))
                            quantity = Float.parseFloat(editable.toString());
                        else
                            quantity = (float) Integer.parseInt(editable.toString());
                        if (rate.getText().toString().contains("."))
                            ratevalue = Float.parseFloat(rate.getText().toString());
                        else
                            ratevalue = (float) Integer.parseInt(rate.getText().toString());

                        finalValue = quantity * ratevalue;
                        totalAmount.setText("Total Amount: Rs " + df.format(finalValue));
                    }
                }

            }
        });
        rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    totalAmount.setText("Total Amount: 0");
                } else {
                    if (quantity.getText().toString().equals("")) {
                        totalAmount.setText("Total Amount: 0");
                    } else {
                        DecimalFormat df = new DecimalFormat("0.00");
                        float quantityValue, ratevalue, finalValue;
                        if (editable.toString().contains("."))
                            ratevalue = Float.parseFloat(editable.toString());
                        else
                            ratevalue = (float) Integer.parseInt(editable.toString());
                        if (quantity.getText().toString().contains("."))
                            quantityValue = Float.parseFloat(quantity.getText().toString());
                        else
                            quantityValue = (float) Integer.parseInt(quantity.getText().toString());

                        finalValue = quantityValue * ratevalue;
                        totalAmount.setText("Total Amount: Rs " + df.format(finalValue));
                    }
                }

            }
        });
        setCanceledOnTouchOutside(true);
    }

    R.id.btnUpdate, R.id.btnCancel
    private void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.btnUpdate:
                dialogListener.onPositiveButtonClick();
                break;

            case R.id.btnCancel:
                dialogListener.onNegativeButtonClick();
                break;

        }
    }

    public void setData(List<String> unitOptions, List<String> partyOptions, List<String> materialOptions, SlipListModel model) {
        this.model = model;
        this.unitOptions = unitOptions;
        this.partyOptions = partyOptions;
        this.materialOptions = materialOptions;

/* CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mContext, partyOptions);
        partySpinner.setAdapter(adapter);
        CustomSpinnerAdapter adapter2 = new CustomSpinnerAdapter(mContext, materialOptions);
        materialSpinner.setAdapter(adapter2);
        CustomSpinnerAdapter adapter3 = new CustomSpinnerAdapter(mContext, unitOptions);
        unitSpinner.setAdapter(adapter3);*/
        quantity.setText(model.getQuantityWeight());
        rate.setText(model.getRate());
        vehicleNumber.setText(model.getVehicleNo());
        totalAmount.setText("Total Amount: Rs "+model.getTotalAmount());
        for (int i = 0; i < partyOptions.size(); i++) {
            if (partyOptions.get(i).equals(model.getPartyName()))
                partySpinner.setSelection(i);
        }

        for (int i = 0; i < materialOptions.size(); i++) {
            if (materialOptions.get(i).equals(model.getMaterialName()))
                materialSpinner.setSelection(i);
        }

        for (int i = 0; i < unitOptions.size(); i++) {
            if (unitOptions.get(i).equals(model.getUnit()))
                unitSpinner.setSelection(i);
        }

    }

    public List<String> getPos() {
        List<String> data = new ArrayList<>();
        data.add(String.valueOf(partySpinner.getSelectedItemPosition()));
        data.add(String.valueOf(materialSpinner.getSelectedItemPosition()));
        return data;
    }

    public SlipListModel getData() {
        model.setQuantityWeight(quantity.getText().toString());
        model.setVehicleNo(vehicleNumber.getText().toString());
        model.setRate(rate.getText().toString());
        model.setPartyName(partyOptions.get(partySpinner.getSelectedItemPosition()));
        model.setMaterialName(materialOptions.get(materialSpinner.getSelectedItemPosition()));
        model.setUnit(unitOptions.get(unitSpinner.getSelectedItemPosition()));
        model.setTotalAmount(totalAmount.getText().toString().split("Rs")[1].trim());
        return model;
    }

}
