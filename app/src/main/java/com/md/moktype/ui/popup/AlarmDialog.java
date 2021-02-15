package com.md.moktype.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.md.moktype.R;

public class AlarmDialog extends Dialog {

    public AlarmDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public static class Builder {

        Context context;
        View contentView;

        String nText;
        String pText;
        DialogInterface.OnClickListener nListener;
        DialogInterface.OnClickListener pListener;
        OnDismissListener dismissListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setNegativeButton(String nText, DialogInterface.OnClickListener nListener) {
            this.nText = nText;
            this.nListener = nListener;
            return this;
        }

        public Builder setPositiveButton(String pText, DialogInterface.OnClickListener pListener) {
            this.pText = pText;
            this.pListener = pListener;
            return this;
        }

        public Builder setDismissListener(OnDismissListener dismissListener) {
            this.dismissListener = dismissListener;
            return this;
        }

        public AlarmDialog build() {
            final AlarmDialog dialog = new AlarmDialog(context);

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.popup_alarm, null);

            Button btnNegative = contentView.findViewById(R.id.cancelBtn);
            Button btnPositive = contentView.findViewById(R.id.okBtn);

            if(!TextUtils.isEmpty(nText)) {
                btnNegative.setVisibility(View.VISIBLE);
                btnNegative.setText(nText);
                btnNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if(nListener != null)
                            nListener.onClick(dialog, BUTTON_NEGATIVE);
                    }
                });
            } else {
                btnNegative.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(pText)) {
                btnPositive.setVisibility(View.VISIBLE);
                btnPositive.setText(pText);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if(pListener != null)
                            pListener.onClick(dialog, BUTTON_POSITIVE);
                    }
                });
            } else {
                btnPositive.setVisibility(View.GONE);
            }

            if(dismissListener != null)
                dialog.setOnDismissListener(dismissListener);

            dialog.setContentView(contentView);
            dialog.setCancelable(false);
            return dialog;
        }
    }
}
