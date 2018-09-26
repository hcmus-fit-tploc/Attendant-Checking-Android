// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.studentQuiz;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TwoStepFragment_ViewBinding implements Unbinder {
  private TwoStepFragment target;

  @UiThread
  public TwoStepFragment_ViewBinding(TwoStepFragment target, View source) {
    this.target = target;

    target._send_Feedback = Utils.findRequiredViewAsType(source, R.id.send_button, "field '_send_Feedback'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TwoStepFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._send_Feedback = null;
  }
}
