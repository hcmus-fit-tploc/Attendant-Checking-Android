// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.studentQuiz;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ResultActivity_ViewBinding implements Unbinder {
  private ResultActivity target;

  @UiThread
  public ResultActivity_ViewBinding(ResultActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ResultActivity_ViewBinding(ResultActivity target, View source) {
    this.target = target;

    target._correct_question = Utils.findRequiredViewAsType(source, R.id.correct_question, "field '_correct_question'", TextView.class);
    target._message = Utils.findRequiredViewAsType(source, R.id.message, "field '_message'", TextView.class);
    target._viewDetail = Utils.findRequiredViewAsType(source, R.id.view_detail, "field '_viewDetail'", Button.class);
    target._close = Utils.findRequiredViewAsType(source, R.id.close, "field '_close'", Button.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar_quiz_result, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ResultActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._correct_question = null;
    target._message = null;
    target._viewDetail = null;
    target._close = null;
    target.toolbar = null;
  }
}
