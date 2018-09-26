// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.Fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SendAbsenceRequestFragment_ViewBinding implements Unbinder {
  private SendAbsenceRequestFragment target;

  @UiThread
  public SendAbsenceRequestFragment_ViewBinding(SendAbsenceRequestFragment target, View source) {
    this.target = target;

    target._send_request = Utils.findRequiredViewAsType(source, R.id.send_ar_button, "field '_send_request'", Button.class);
    target._reason = Utils.findRequiredViewAsType(source, R.id.reason_editText, "field '_reason'", EditText.class);
    target._fromdate = Utils.findRequiredViewAsType(source, R.id.etxt_fromdate, "field '_fromdate'", EditText.class);
    target._todate = Utils.findRequiredViewAsType(source, R.id.etxt_todate, "field '_todate'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SendAbsenceRequestFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._send_request = null;
    target._reason = null;
    target._fromdate = null;
    target._todate = null;
  }
}
