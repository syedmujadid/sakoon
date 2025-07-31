package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;

public class UpdateAppDialog extends Dialog {

    private Context mContext;
    private DialogListener dialogListener;

    public UpdateAppDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_app_update);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    R.id.tv_update
    private void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.tv_update:
                dialogListener.onPositiveButtonClick();
                break;

        }
    }

    public void setData(String s1,String s2){
        tvTitle.setText(s1);
        tvMessage.setText(s2);
        tvUpdate.setText("OK");
    }

}
