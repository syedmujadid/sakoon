package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.interfaces.DialogListenerBackPressed;

public class ConfirmationDialogBackPressed extends Dialog {

    private Context mContext;
    private DialogListenerBackPressed dialogListener;
    private TextView tvMessage;

    public ConfirmationDialogBackPressed(Context context, DialogListenerBackPressed dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirmation_backpress);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(true);
        
        // Initialize views
        tvMessage = findViewById(R.id.tv_message);
        findViewById(R.id.tv_positive).setOnClickListener(this::onViewClicked);
        findViewById(R.id.tv_neutral).setOnClickListener(this::onViewClicked);
        findViewById(R.id.tv_negative).setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.tv_positive:
                dialogListener.onPositiveButtonClick();
                break;
            case R.id.tv_negative:
                dialogListener.onNegativeButtonClick();
                break;
            case R.id.tv_neutral:
                dialogListener.onNeutralButtonClick();
                break;
        }
    }

    public void setMessage(String message)
    {
        tvMessage.setText(message);
    }

}
