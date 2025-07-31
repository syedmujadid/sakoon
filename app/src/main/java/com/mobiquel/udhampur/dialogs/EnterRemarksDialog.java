package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;

public class EnterRemarksDialog extends Dialog {

    private Context mContext;
    private DialogListener dialogListener;

    public EnterRemarksDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_write_remark);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
       //setCancelable(false);
        setCanceledOnTouchOutside(true);

    }

    R.id.submit, R.id.close
    private void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.submit:
                dialogListener.onPositiveButtonClick();
                break;
            case R.id.close:
                dialogListener.onNegativeButtonClick();
                break;

        }
    }

    public void setEditTextData(String data)
    {
        if(data.equals("")){

        }
        else{
            solutionValue.setText(data);
        }
    }
    public String getSolution(){
        String a=solutionValue.getText().toString();
        solutionValue.setText("");
        return  a;
    }

}
