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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmationDialog extends Dialog {


    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_positive)
    Button tvPositive;
    @BindView(R.id.tv_negative)
    Button tvNegative;
    private Context mContext;
    private DialogListener dialogListener;

    public ConfirmationDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirmation);
        ButterKnife.bind(this);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(false);
    }

    @OnClick({R.id.tv_positive, R.id.tv_negative})
    public void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.tv_positive:
                dialogListener.onPositiveButtonClick();
                break;
            case R.id.tv_negative:
                dialogListener.onNegativeButtonClick();
                break;
        }
    }


    public void setMessage(String message)
    {
        tvMessage.setText(message);
    }


}
