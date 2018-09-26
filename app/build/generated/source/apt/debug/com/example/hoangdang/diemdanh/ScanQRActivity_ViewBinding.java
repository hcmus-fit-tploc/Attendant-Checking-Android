// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ScanQRActivity_ViewBinding implements Unbinder {
  private ScanQRActivity target;

  @UiThread
  public ScanQRActivity_ViewBinding(ScanQRActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ScanQRActivity_ViewBinding(ScanQRActivity target, View source) {
    this.target = target;

    target._toolBar = Utils.findRequiredViewAsType(source, R.id.scan_qrcode_toolbar, "field '_toolBar'", Toolbar.class);
    target.barcodeScannerView = Utils.findRequiredViewAsType(source, R.id.zxing_barcode_scanner, "field 'barcodeScannerView'", DecoratedBarcodeView.class);
    target._footer = Utils.findRequiredViewAsType(source, R.id.footer, "field '_footer'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ScanQRActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._toolBar = null;
    target.barcodeScannerView = null;
    target._footer = null;
  }
}
