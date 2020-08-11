package com.mobiquel.udhampur.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.pojo.BeneficiaryModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class View_Beneficiary_Dialog extends Dialog {

    private Context context;
    private Activity mActivity;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.personName)
    EditText personName;
    @BindView(R.id.maleRg)
    RadioButton maleRg;
    @BindView(R.id.femaleRg)
    RadioButton femaleRg;
    @BindView(R.id.genderRadioGroup)
    RadioGroup genderRadioGroup;
    @BindView(R.id.adharNumber)
    EditText adharNumber;
    @BindView(R.id.adharAddress)
    EditText adharAddress;
    @BindView(R.id.contactNumber)
    EditText contactNumber;
    @BindView(R.id.pinCode)
    EditText pinCode;
    @BindView(R.id.percentageShare)
    Spinner percentageShare;
    @BindView(R.id.accountHolderName)
    EditText accountHolderName;
    @BindView(R.id.applicantRelation)
    Spinner applicantRelation;
    @BindView(R.id.accountNumber)
    EditText accountNumber;
    @BindView(R.id.bankame)
    EditText bankame;
    @BindView(R.id.branchName)
    EditText branchName;
    @BindView(R.id.ifscCode)
    EditText ifscCode;
    @BindView(R.id.update)
    TextView update;
    private DialogListener dialogListener;
    private String[] perceArray = new String[4];
    private String[] relArray = new String[8];

    public View_Beneficiary_Dialog(Context context, DialogListener dialogListener) {
        super(context, R.style.BottomSheetDialogStyle_Article);
        this.context = context;
        this.dialogListener = dialogListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_benefeciary);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {
        perceArray[0] = "100";
        perceArray[1] = "75";
        perceArray[2] = "50";
        perceArray[3] = "25";
        relArray = context.getResources().getStringArray(R.array.relationArray);
    }

    private void setListeners() {

    }


    public void setData(BeneficiaryModel model, String status, int pos) {
        title.setText("Beneficiary #" + pos);
        personName.setText(model.getName());
        if (model.getGender().equals("Male")) {
            maleRg.setChecked(true);
            femaleRg.setChecked(false);
        } else {
            maleRg.setChecked(false);
            femaleRg.setChecked(true);
        }
        adharNumber.setText(model.getAdharNumber());
        adharAddress.setText(model.getAddress());
        contactNumber.setText(model.getContactNumber());
        pinCode.setText(model.getPinCode());
        accountHolderName.setText(model.getAccountHolderName());
        accountNumber.setText(model.getAccountNumber());
        ifscCode.setText(model.getIfscCode());
        branchName.setText(model.getBranchName());
        bankame.setText(model.getBankName());

        for (int i = 0; i < relArray.length; i++) {
            if (relArray.equals(model.getRelation())) {
                applicantRelation.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < perceArray.length; i++) {
            if (perceArray.equals(model.getShare())) {
                percentageShare.setSelection(i);
                break;
            }
        }

        if (status.equals("VIEW")) {
            personName.setEnabled(false);
            maleRg.setEnabled(false);
            femaleRg.setEnabled(false);
            adharAddress.setEnabled(false);
            adharNumber.setEnabled(false);
            accountNumber.setEnabled(false);
            accountHolderName.setEnabled(false);
            branchName.setEnabled(false);
            ifscCode.setEnabled(false);
            bankame.setEnabled(false);
            contactNumber.setEnabled(false);
            pinCode.setEnabled(false);
            update.setVisibility(View.GONE);
        }

    }

    public BeneficiaryModel getData() {
        BeneficiaryModel model = new BeneficiaryModel();
        model.setName(personName.getText().toString());
        model.setPinCode(pinCode.getText().toString());
        model.setAddress(adharAddress.getText().toString());
        model.setAdharNumber(adharNumber.getText().toString());
        if (maleRg.isChecked())
            model.setGender("Male");
        else
            model.setGender("Female");
        model.setAccountHolderName(accountHolderName.getText().toString());
        model.setBankName(bankame.getText().toString());
        model.setBranchName(branchName.getText().toString());
        model.setIfscCode(ifscCode.getText().toString());
        model.setRelation(applicantRelation.getSelectedItem().toString());
        model.setShare(percentageShare.getSelectedItem().toString());
        model.setAccountNumber(accountNumber.getText().toString());
        model.setContactNumber(contactNumber.getText().toString());
        return model;
    }

    @OnClick({R.id.update, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.update:
                dialogListener.onPositiveButtonClick();
                break;
            case R.id.back:
                dismiss();
                break;
        }
    }

}
