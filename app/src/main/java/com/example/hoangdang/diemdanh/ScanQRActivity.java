package com.example.hoangdang.diemdanh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanQRActivity extends AppCompatActivity {
    @BindView(R.id.scan_qrcode_toolbar)
    Toolbar _toolBar;

    @BindView(R.id.zxing_barcode_scanner)
    DecoratedBarcodeView barcodeScannerView;

    @BindView(R.id.footer)
    TextView _footer;

    private CaptureManager capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        ButterKnife.bind(this);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        Log.wtf("HiepQR","onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
        Log.wtf("HiepQR","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
        Log.wtf("HiepQR","onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
        Log.wtf("HiepQR","onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
        Log.wtf("HiepQR","onInstance");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.wtf("HiepQR","onSupport");
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.wtf("HiepQR","onKey");
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
