// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.Fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SendFeedbackFragment_ViewBinding implements Unbinder {
  private SendFeedbackFragment target;

  @UiThread
  public SendFeedbackFragment_ViewBinding(SendFeedbackFragment target, View source) {
    this.target = target;

    target._send_Feedback = Utils.findRequiredViewAsType(source, R.id.send_button, "field '_send_Feedback'", Button.class);
    target._title = Utils.findRequiredViewAsType(source, R.id.title_editText, "field '_title'", EditText.class);
    target._content = Utils.findRequiredViewAsType(source, R.id.content_editText, "field '_content'", EditText.class);
    target._isAnonymous = Utils.findRequiredViewAsType(source, R.id.isAnonymous_checkbox, "field '_isAnonymous'", CheckBox.class);
    target._desFeedback = Utils.findRequiredViewAsType(source, R.id.des_feedback, "field '_desFeedback'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SendFeedbackFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._send_Feedback = null;
    target._title = null;
    target._content = null;
    target._isAnonymous = null;
    target._desFeedback = null;
  }
}
