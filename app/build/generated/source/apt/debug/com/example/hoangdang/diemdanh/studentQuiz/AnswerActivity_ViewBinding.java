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

public class AnswerActivity_ViewBinding implements Unbinder {
  private AnswerActivity target;

  @UiThread
  public AnswerActivity_ViewBinding(AnswerActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AnswerActivity_ViewBinding(AnswerActivity target, View source) {
    this.target = target;

    target.buttonA = Utils.findRequiredViewAsType(source, R.id.answer_1, "field 'buttonA'", Button.class);
    target.buttonB = Utils.findRequiredViewAsType(source, R.id.answer_2, "field 'buttonB'", Button.class);
    target.buttonC = Utils.findRequiredViewAsType(source, R.id.answer_3, "field 'buttonC'", Button.class);
    target.buttonD = Utils.findRequiredViewAsType(source, R.id.answer_4, "field 'buttonD'", Button.class);
    target.quizName = Utils.findRequiredViewAsType(source, R.id.quiz_name, "field 'quizName'", TextView.class);
    target.quizNoti = Utils.findRequiredViewAsType(source, R.id.quiz_noti, "field 'quizNoti'", TextView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar_quiz, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AnswerActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.buttonA = null;
    target.buttonB = null;
    target.buttonC = null;
    target.buttonD = null;
    target.quizName = null;
    target.quizNoti = null;
    target.toolbar = null;
  }
}
