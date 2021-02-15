package com.md.moktype.ui.activity.qr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.md.moktype.R;
import com.md.moktype.common.Constants;
import com.md.moktype.ui.activity.BaseActivity;
import com.md.moktype.ui.popup.NFCSignPopup;
import com.md.moktype.utils.Prefs;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.lang.reflect.Field;

public class QRScanActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener{
    protected final String TAG = "CustomScannerActivity";

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Boolean switchFlashlightButtonCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        switchFlashlightButtonCheck = true;

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        ViewfinderView viewFinder = barcodeScannerView.getViewFinder();
        viewFinder.setVisibility(View.INVISIBLE);
        Field scannerAlphaField = null;

        try {
            scannerAlphaField = viewFinder.getClass().getDeclaredField("SCANNER_ALPHA");
            scannerAlphaField.setAccessible(true);
            scannerAlphaField.set(viewFinder, new int[1]);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        barcodeScannerView.setTorchListener(this);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        if(Prefs.getBoolean(Constants.PREFS_FIRST_QR, true)) {
            Prefs.putBoolean(Constants.PREFS_FIRST_QR, false);
            startActivity(new Intent(QRScanActivity.this, NFCSignPopup.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * TorchListener
     */
    @Override
    public void onTorchOn() {
//        btnSwitchFlash.setImageResource(R.drawable.ic_menu_flash);
        switchFlashlightButtonCheck = false;
    }

    @Override
    public void onTorchOff() {
//        btnSwitchFlash.setImageResource(R.drawable.ic_menu_flash);
        switchFlashlightButtonCheck = true;
    }

    public void clickCloseBtn(View view) {
        finish();
    }
}
