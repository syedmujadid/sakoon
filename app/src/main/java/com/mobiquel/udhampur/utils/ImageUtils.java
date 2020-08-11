package com.mobiquel.udhampur.utils;

import com.facebook.drawee.view.SimpleDraweeView;

public class ImageUtils {

    public static void setImage(SimpleDraweeView imageView, String uriString) {
        if (imageView != null) {
            imageView.setImageURI(uriString != null ? uriString : "");
        }
    }



    public static void setImage(SimpleDraweeView imageView, int drawableResId) {
        if (imageView != null) {
            imageView.setImageResource(drawableResId);
        }
    }

}
