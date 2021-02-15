package com.md.moktype.ui.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.md.moktype.R;

public class PermissionPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_permission);
    }

    public void clickOKBtn(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
