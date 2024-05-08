package com.demo.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;


public class DynamicAlertDialog extends AlertDialog {
//    PreferenceManager preferenceManager;
    Builder builder;

    public DynamicAlertDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DynamicAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public DynamicAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context mContext) {
//        preferenceManager = new PreferenceManager(mContext);
        builder = new Builder(mContext);
    }

    public DynamicAlertDialog setPositiveButton(String button, OnClickListener listener) {
        builder.setPositiveButton(button, listener);
        return this;
    }

    public DynamicAlertDialog setPositiveButton(String button) {
        builder.setPositiveButton(button, null);
        return this;
    }

    public DynamicAlertDialog setNegativeButton(String button, OnClickListener listener) {
        builder = builder.setNegativeButton(button, listener);
        return this;
    }

    public DynamicAlertDialog setNegativeButton(String button) {
        builder.setNegativeButton(button, null);
        return this;
    }

    public DynamicAlertDialog setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public DynamicAlertDialog setMessage(String message) {
        builder.setMessage(message);
        return this;
    }

    public DynamicAlertDialog setItems(CharSequence[] items, OnClickListener listener) {
        builder.setItems(items, listener);
        return this;
    }

    public DynamicAlertDialog isCancelable(boolean isCancelable) {
        builder.setCancelable(isCancelable);
        return this;
    }

    public void show() {
        final AlertDialog dialog = builder.create();
//        dialog.setOnShowListener(new OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//                try {
//                    dialog.getButton(BUTTON_POSITIVE).setTextColor(preferenceManager.getAccentColor());
//                } catch (Exception e) {
//                    LogHelper.printStack(e);
//                }
//                try {
//                    dialog.getButton(BUTTON_NEUTRAL).setTextColor(preferenceManager.getAccentColor());
//                } catch (Exception e) {
//                    LogHelper.printStack(e);
//                }
//                try {
//                    dialog.getButton(BUTTON_NEGATIVE).setTextColor(preferenceManager.getAccentColor());
//                } catch (Exception e) {
//                    LogHelper.printStack(e);
//                }
//
//            }
//        });
        dialog.show();
    }
}
