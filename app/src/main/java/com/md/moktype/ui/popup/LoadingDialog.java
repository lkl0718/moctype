package com.md.moktype.ui.popup;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import com.md.moktype.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Activity activity){
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_loading_dialog);
    }
}