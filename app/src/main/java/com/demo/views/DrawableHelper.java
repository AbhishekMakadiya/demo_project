package com.demo.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.demo.R;


public class DrawableHelper {
    public Drawable getRoundedRectangleDrawableWithStroke(Context mContext, int strokeColor, int solidColor) {
        return getRoundedRectangleDrawableWithStroke(mContext, strokeColor, solidColor, (int) mContext.getResources().getDimension(R.dimen._30dp), (int) mContext.getResources().getDimension(R.dimen._2dp));
    }

    public Drawable getRoundedRectangleDrawableWithStroke(Context mContext, int strokeColor, int solidColor, int cornerRadius) {
        return getRoundedRectangleDrawableWithStroke(mContext, strokeColor, solidColor, cornerRadius, (int) mContext.getResources().getDimension(R.dimen._2dp));
    }

    public Drawable getRoundedRectangleDrawableWithStroke(Context mContext, int strokeColor, int solidColor, int cornerRadius, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setColor(solidColor);
        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }

    public Drawable getRoundedRectangleDrawableWithStroke(Context mContext, int strokeColor, int solidColor, int cornerRadiusTopLeft, int cornerRadiusTopRight, int cornerRadiusBottomRight, int cornerRadiusBottomLeft, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setColor(solidColor);
        drawable.setCornerRadii(new float[]{cornerRadiusTopLeft, cornerRadiusTopLeft, cornerRadiusTopRight, cornerRadiusTopRight, cornerRadiusBottomRight, cornerRadiusBottomRight, cornerRadiusBottomLeft, cornerRadiusBottomLeft});
        return drawable;
    }

    public Drawable getRectangleDrawableWithStroke(Context mContext, int strokeColor, int solidColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setColor(solidColor);
        return drawable;
    }

    public Drawable getOvalDrawableWithStroke(Context mContext, int strokeColor, int solidColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke((int) mContext.getResources().getDimension(R.dimen._3dp), strokeColor);
        drawable.setColor(solidColor);
        return drawable;
    }

    public Drawable getOvalDrawableWithStroke(Context mContext, int strokeColor, int solidColor, int strokeSize) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(strokeSize, strokeColor);
        drawable.setColor(solidColor);
        return drawable;
    }
}
