package com.md.moktype.ui.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.md.moktype.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.EnumMap;
import java.util.Map;

public class MyQRPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_my_qr);

        String strParam = getIntent().getStringExtra("PARAM");
        ImageView myQRView = findViewById(R.id.myQRView);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{

            //바코드 생성시 여백 설정
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */

            BitMatrix bitMatrix = multiFormatWriter.encode(
                    strParam,                   //바코드값
                    BarcodeFormat.QR_CODE,      //바코드타입
                    convertDpToPixel(130),      //가로
                    convertDpToPixel(130),      //세로
                    hints);                     //설정

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myQRView.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * DP 값을 Pixel 값으로 변환
     * @param dp
     * @return
     */
    public int convertDpToPixel(float dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void clickPopCloseBtn(View view) {
        finish();
    }
}
