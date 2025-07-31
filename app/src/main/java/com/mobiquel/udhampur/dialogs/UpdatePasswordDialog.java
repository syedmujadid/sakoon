package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;

import java.util.ArrayList;
import java.util.List;

public class UpdatePasswordDialog extends Dialog {

    private Context mContext;
    private DialogListener dialogListener;
    private String currentPwd = "";
    private boolean isPwdHidden1 = true;
    private boolean isPwdHidden2 = true;
    private boolean isPwdHidden3 = true;

    public UpdatePasswordDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_update_pwd);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        //setCancelable(false);
        setCanceledOnTouchOutside(true);

        showPwd2.setImageResource(R.drawable.showpassword_hide);
        showPwd3.setImageResource(R.drawable.showpassword_hide);
        showPwd1.setImageResource(R.drawable.showpassword_hide);
    }

    R.id.submit, R.id.close,R.id.showPwd1, R.id.showPwd2,R.id.showPwd3
    private void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.submit:
                dialogListener.onPositiveButtonClick();
                break;
            case R.id.close:
                dialogListener.onNegativeButtonClick();
                break;
            case R.id.showPwd1:
                if (isPwdHidden1) {
                    isPwdHidden1 = false;
                    et_user_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPwd1.setImageResource(R.drawable.showpassword);
                } else {
                    isPwdHidden1 = true;
                    et_user_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPwd1.setImageResource(R.drawable.showpassword_hide);
                }

                break;
            case R.id.showPwd2:
                if (isPwdHidden2) {
                    isPwdHidden2 = false;
                    etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPwd2.setImageResource(R.drawable.showpassword);
                } else {
                    isPwdHidden2 = true;
                    etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPwd2.setImageResource(R.drawable.showpassword_hide);
                }

                break;
            case R.id.showPwd3:
                if (isPwdHidden3) {
                    isPwdHidden3 = false;
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPwd3.setImageResource(R.drawable.showpassword);
                } else {
                    isPwdHidden3 = true;
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPwd3.setImageResource(R.drawable.showpassword_hide);
                }

                break;

        }
    }

    public List<String> checkSolution() {
        List<String> data=new ArrayList<>();
        if (et_user_password.getText().toString().equals("")) {
            //Utils.showToast(mContext, "Please enter current password!");
            data.add("Please enter current password!");
            data.add("0");
        }
        else if (!et_user_password.getText().toString().equals(currentPwd)) {
            //Utils.showToast(mContext, "Please enter correct password!");
            data.add("Please enter correct password!");
            data.add("0");
        }
        else if (etNewPassword.getText().toString().equals("")) {
            //Utils.showToast(mContext, "Please enter new password!");
            data.add("Please enter new password!");
            data.add("0");
        }
        else if (etConfirmPassword.getText().toString().equals("")) {
            //Utils.showToast(mContext, "Please confirm password!");
            data.add("Please confirm password!");
            data.add("0");
        }
        else if (!etConfirmPassword.getText().toString().equals(etNewPassword.getText().toString())) {
            //Utils.showToast(mContext, "Password not matched!");
            data.add("Password not matched!");
            data.add("0");
        }
        else{
            data.add(etNewPassword.getText().toString());
            data.add("1");
        }

        return  data;
    }
public void setData(String pwd){
        currentPwd=pwd;
}
}
