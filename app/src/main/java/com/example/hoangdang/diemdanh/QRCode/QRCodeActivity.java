package com.example.hoangdang.diemdanh.QRCode;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.encoder.ErrorCorrection;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QRCodeActivity extends AppCompatActivity {
    @BindView(R.id.qrcode_toolbar)
    Toolbar _toolBar;
    @BindView(R.id.return_button)
    Button _return_button;
    @BindView(R.id.qrCode_imageView)
    ImageView _qrCode_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        ButterKnife.bind(this);
        SharedPreferences pref = new SecurePreferences(this);
        _toolBar.setTitle(pref.getString(AppVariable.CURRENT_COURSE_NAME, "QR CODE"));
        setSupportActionBar(_toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int attendanceID = pref.getInt(AppVariable.CURRENT_ATTENDANCE, 0);

        String message = Network.API_ATTENDANCE_QR_CODE + String.valueOf(attendanceID);

        try {
            _qrCode_imageView.setImageBitmap(generateCode(message));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        _return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected Bitmap generateCode(String str) throws WriterException {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        int size = 600;
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, size, size, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        }
        catch (WriterException e){
            e.printStackTrace();
        }
        return null;
    }
}
