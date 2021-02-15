package com.md.moktype.ui.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.md.moktype.common.Constants;
import com.md.moktype.common.nfc.NdefMessageParser;
import com.md.moktype.common.nfc.ParsedRecord;
import com.md.moktype.common.nfc.TextRecord;
import com.md.moktype.ui.activity.MainActivity;
import com.md.moktype.ui.activity.SplashActivity;
import com.md.moktype.utils.Prefs;

import java.util.List;

public class NFCReadPopup extends AppCompatActivity {

    private NfcAdapter mAdapter;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.popup_nfcread);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent passedIntent = getIntent();
        if(passedIntent != null) {
            String action = passedIntent.getAction();
            if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
                processTag(passedIntent);
            }
        }
    }

    @Override
    public void onNewIntent(Intent passedIntent) {
        super.onNewIntent(passedIntent);

        if(passedIntent != null) {
            processTag(passedIntent);
        }
    }

    /************************************************************
     * 전달받은 Intent에서 NFC 태그에 등록한 비즈니스 정보(사원코드 등)
     * 를 얻는 메소드
     * @param passedIntent - NFC태그의 등록된 정보를 가지고있는 Intent
     **************************************************************/
    private void processTag(Intent passedIntent) {
        Parcelable[] rawMsgs = passedIntent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs == null) {
            return;
        }

//        Toast.makeText(getApplicationContext(), "스캔 성공!", Toast.LENGTH_LONG).show();

//        LogUtil.i("info", "rawMsgs.length:"+rawMsgs.length);
        // 참고! rawMsgs.length : 스캔한 태그 개수
        NdefMessage[] msgs;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                showTag(msgs[i]);
            }
        }
    }

    /*******************************************
     *  바이너리 형식의 NFC 태그 ID를 문자열 형식으로
     *  바꿔서 리턴하는 메소드
     *******************************************/
    public static final String CHARS = "0123456789ABCDEF";

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(
                    CHARS.charAt(data[i] & 0x0F));
        }
        return sb.toString();
    }

    /***************************************************************
     * NdefMessage에서 NdefRecode들을 추출후 NdefRecode내용중 텍스값만 추출
     * 예-사원코드(A0001)
     * @param mMessage
     * @return
     **************************************************************/
    private void showTag(NdefMessage mMessage) {
        List<ParsedRecord> records = NdefMessageParser.parse(mMessage);
        final int size = records.size();
        String strNFCResult = null;

        for (int i = 0; i < size; i++) {
            ParsedRecord record = records.get(i);

            int recordType = record.getType();
            if (recordType == ParsedRecord.TYPE_TEXT) {
                strNFCResult = ((TextRecord) record).getText();
                break;
            }
        }

        if(strNFCResult.startsWith("nHangarae"))
            strNFCResult = strNFCResult.substring(12);
        else
            strNFCResult = strNFCResult.substring(11);

        if(!TextUtils.isEmpty(Prefs.getString(Constants.PREFS_LOGIN_TOKEN))) { // 로그인 중이라면
            Intent intent = new Intent(NFCReadPopup.this, MainActivity.class);
            intent.putExtra("NFC", strNFCResult);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(NFCReadPopup.this, SplashActivity.class);
            intent.putExtra("NFC", strNFCResult);
            startActivity(intent);
            finish();
        }
    }
}