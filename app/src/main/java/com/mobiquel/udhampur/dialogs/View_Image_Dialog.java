package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.utils.TouchImageView;

public class View_Image_Dialog extends Dialog implements View.OnClickListener {

    private final Context context;
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
        webView = findViewById(R.id.webView);
    }

    private void setListeners() {
        bottomLayout.setOnClickListener(this);
    }

    public void setData(String url, String title) {
        //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        header.setText(title);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progress_bar.setVisibility(View.GONE);
            }
        });
       /* Glide.with(context).load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        Toast.makeText(context,"Error loading image",Toast.LENGTH_SHORT).show();
                        progress_bar.setVisibility(View.GONE);
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
*/
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
