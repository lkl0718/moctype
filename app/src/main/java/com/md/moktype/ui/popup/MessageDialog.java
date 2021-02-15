package com.md.moktype.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.moktype.R;

public class MessageDialog extends Dialog {

    public MessageDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public static class Builder {

        Context context;
        View contentView;

        String title;
        String message;
        String nText;
        String pText;
        OnClickListener nListener;
        OnClickListener pListener;
        boolean cancelable = true;
        boolean closeBtn = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setVisibleCloseBtn(boolean closeBtn) {
            this.closeBtn = closeBtn;
            return this;
        }

        public Builder setNegativeButton(String nText, OnClickListener nListener) {
            this.nText = nText;
            this.nListener = nListener;
            return this;
        }

        public Builder setPositiveButton(String pText, OnClickListener pListener) {
            this.pText = pText;
            this.pListener = pListener;
            return this;
        }

        public MessageDialog build() {
            final MessageDialog dialog = new MessageDialog(context);

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.layout_message_dialog, null);

            RelativeLayout topLayout = contentView.findViewById(R.id.rl_top_layout);
            TextView tvTitle = contentView.findViewById(R.id.tv_title);
            TextView tvMessage = contentView.findViewById(R.id.tv_message);
            ImageButton btnClose = contentView.findViewById(R.id.btn_close);
            Button btnNegative = contentView.findViewById(R.id.btn_negative);
            Button btnPositive = contentView.findViewById(R.id.btn_positive);

            if(!TextUtils.isEmpty(title)) {
                topLayout.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            }else{
                topLayout.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(message)) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            }else{
                tvMessage.setVisibility(View.GONE);
            }

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

            if(closeBtn) {
                btnClose.setVisibility(View.VISIBLE);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }else{
                btnClose.setVisibility(View.GONE);
            }

            dialog.setContentView(contentView);
            dialog.setCancelable(cancelable);
            return dialog;
        }
    }
}
