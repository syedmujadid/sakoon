package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;

public class UploadImageDialog extends Dialog {

    private Context mContext;
    private DialogListener dialogListener;

    public UploadImageDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_upload_pic);

        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(true);
    }

    R.id.fromCamera, R.id.fromGallery
    private void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.fromCamera:
                dialogListener.onPositiveButtonClick();
                break;
            case R.id.fromGallery:
                dialogListener.onNegativeButtonClick();
                break;
        }
    }

    public void setDrawableLeft(){
        Drawable leftDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_camera_2);
        Drawable leftDrawable2 = AppCompatResources.getDrawable(mContext, R.drawable.ic_gallery);
        fromCamera.setCompoundDrawablesWithIntrinsicBounds(null, leftDrawable, null, null);
        fromGallery.setCompoundDrawablesWithIntrinsicBounds(null, leftDrawable2, null, null);

    }

}
