// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.currentSessionImage;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MyFaceDetect_ViewBinding implements Unbinder {
  private MyFaceDetect target;

  @UiThread
  public MyFaceDetect_ViewBinding(MyFaceDetect target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MyFaceDetect_ViewBinding(MyFaceDetect target, View source) {
    this.target = target;

    target.verify = Utils.findRequiredViewAsType(source, R.id.faceverify, "field 'verify'", Button.class);
    target.checkall = Utils.findRequiredViewAsType(source, R.id.facecheckall, "field 'checkall'", Button.class);
    target.back_button = Utils.findRequiredViewAsType(source, R.id.img_back_button, "field 'back_button'", ImageView.class);
    target.next_button = Utils.findRequiredViewAsType(source, R.id.img_next_button, "field 'next_button'", ImageView.class);
    target.face_verify_single = Utils.findRequiredViewAsType(source, R.id.faceverify2, "field 'face_verify_single'", Button.class);
    target.imageview = Utils.findRequiredViewAsType(source, R.id.imageView, "field 'imageview'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyFaceDetect target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.verify = null;
    target.checkall = null;
    target.back_button = null;
    target.next_button = null;
    target.face_verify_single = null;
    target.imageview = null;
  }
}
