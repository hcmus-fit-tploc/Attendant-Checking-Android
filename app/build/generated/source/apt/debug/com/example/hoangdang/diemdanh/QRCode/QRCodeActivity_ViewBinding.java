// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.QRCode;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class QRCodeActivity_ViewBinding implements Unbinder {
  private QRCodeActivity target;

  @UiThread
  public QRCodeActivity_ViewBinding(QRCodeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public QRCodeActivity_ViewBinding(QRCodeActivity target, View source) {
    this.target = target;

    target._toolBar = Utils.findRequiredViewAsType(source, R.id.qrcode_toolbar, "field '_toolBar'", Toolbar.class);
    target._return_button = Utils.findRequiredViewAsType(source, R.id.return_button, "field '_return_button'", Button.class);
    target._qrCode_imageView = Utils.findRequiredViewAsType(source, R.id.qrCode_imageView, "field '_qrCode_imageView'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    QRCodeActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._toolBar = null;
    target._return_button = null;
    target._qrCode_imageView = null;
  }
}
