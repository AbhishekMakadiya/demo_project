package com.demo.utils.alertmessages

import android.content.Context
import android.content.DialogInterface
import com.demo.R
import com.demo.utils.LogHelper

object AlertMessage {

    fun showMessage(mContext: Context?, message: String) {
        return showMessage(mContext, message, null, null, null,false, null)
    }

    fun showMessage(mContext: Context?, messageResId: Int) {
        return showMessage(mContext, messageResId, null, null, null,false, null)
    }

    fun showMessage(mContext: Context?, message: String, listener: AlertMessageListener) {
        return showMessage(mContext, message, null, null, null,false, listener)
    }

    fun showMessage(mContext: Context?, message: String, title: String) {
        return showMessage(mContext, message, title, null, null, false,null)
    }

    fun showMessage(mContext: Context?, message: String, positiveText: String, listener: AlertMessageListener) {
        return showMessage(mContext, message, null, positiveText, null,false, listener)
    }

    fun showMessage(
            mContext: Context?,
            message: String?,
            positiveText: String?,
            negativeTxt: String?,
            listener: AlertMessageListener?
    ) {
        return showMessage(mContext, message, null, positiveText, negativeTxt,false, listener)
    }

    fun showMessage(
        mContext: Context?,
        message: Any?,
        title: String?,
        positiveText: String?,
        negativeTxt: String?,
        isCancelableDialog:Boolean,
        listener: AlertMessageListener?
    ) {
        if (mContext == null) {
            LogHelper.e(this.javaClass.simpleName, "Not able to show dialog because of null context")
            return
        }

        val myDialog = MyAlertDialog(mContext)
//        title?.let { myDialog.setTitle(it) }
        myDialog.setMTitle(mContext.getString(R.string.app_name).takeIf { title.isNullOrBlank() }
                ?: title!!)

        message?.let { myDialog.setMessage(it) }

        myDialog.setPositiveButton(mContext.getString(R.string.ok).takeIf { positiveText.isNullOrBlank() }
                ?: positiveText!!, DialogInterface.OnClickListener { _, _ ->
            listener?.onPositiveButtonClick()
        })

        negativeTxt?.let {
            myDialog.setNegativeButton(it, DialogInterface.OnClickListener { _, _ ->
                listener?.onNegativeButtonClick()
            })
        }

        myDialog.isCancelable(isCancelableDialog)
        myDialog.show()
    }
}

