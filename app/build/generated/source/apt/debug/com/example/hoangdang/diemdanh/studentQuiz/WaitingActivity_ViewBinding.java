// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.studentQuiz;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import com.trncic.library.DottedProgressBar;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WaitingActivity_ViewBinding implements Unbinder {
  private WaitingActivity target;

  @UiThread
  public WaitingActivity_ViewBinding(WaitingActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public WaitingActivity_ViewBinding(WaitingActivity target, View source) {
    this.target = target;

    target.tv1 = Utils.findRequiredViewAsType(source, R.id.tv1, "field 'tv1'", TextView.class);
    target.dottedProgressBar = Utils.findRequiredViewAsType(source, R.id.progress_dot, "field 'dottedProgressBar'", DottedProgressBar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WaitingActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tv1 = null;
    target.dottedProgressBar = null;
  }
}
