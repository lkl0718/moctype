package com.md.moktype.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.md.moktype.R;

public class PermissionDialog extends Dialog {

    public PermissionDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public static class Builder {

        Context context;
        View contentView;
        OnClickListener pListener;
        boolean cancelable = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setPositiveButton(OnClickListener pListener) {
            this.pListener = pListener;
            return this;
        }

        public PermissionDialog build() {
            final PermissionDialog dialog = new PermissionDialog(context);

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.layout_permission_dialog, null);

            Button btnPositive = contentView.findViewById(R.id.btn_stack);
            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if(pListener != null)
                        pListener.onClick(dialog, BUTTON_POSITIVE);
                }
            });

            dialog.setContentView(contentView);
            dialog.setCancelable(cancelable);
            return dialog;
        }
    }
}
