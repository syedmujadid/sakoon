package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.utils.TouchImageView;

import butterknife.BindView;


public class View_Image_Dialog extends Dialog implements View.OnClickListener {

    private final Context context;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.bottomLayout)
    RelativeLayout bottomLayout;
    @BindView(R.id.image)
    TouchImageView ivIcon;
    @BindView(R.id.title)
    TextView header;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    public View_Image_Dialog(Context context) {
        super(context, R.style.BottomSheetDialogStyle_Article);
        this.context = context;
        setContentView(R.layout.dialog_view_image);

        initViews();
        setListeners();

    }

    private void initViews() {
        close = findViewById(R.id.close);
        bottomLayout = findViewById(R.id.bottomLayout);
        ivIcon = findViewById(R.id.image);
        header = findViewById(R.id.title);
        progress_bar = findViewById(R.id.progress_bar);

    }

    private void setListeners() {
        bottomLayout.setOnClickListener(this);
    }



    public void setData(String url,String title) {
        Glide.with(context).load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progress_bar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivIcon);
        header.setText(title);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottomLayout:
                dismiss();
                break;
        }
    }

}
