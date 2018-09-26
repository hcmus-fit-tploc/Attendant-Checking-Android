// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SetHostActivity_ViewBinding implements Unbinder {
  private SetHostActivity target;

  @UiThread
  public SetHostActivity_ViewBinding(SetHostActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SetHostActivity_ViewBinding(SetHostActivity target, View source) {
    this.target = target;

    target.host_textview = Utils.findRequiredViewAsType(source, R.id.textview_host, "field 'host_textview'", TextView.class);
    target.editText_inputhost = Utils.findRequiredViewAsType(source, R.id.editText_inputhost, "field 'editText_inputhost'", EditText.class);
    target.button_changehostbyhand = Utils.findRequiredViewAsType(source, R.id.button_changehostbyhand, "field 'button_changehostbyhand'", Button.class);
    target.button_changehostonline = Utils.findRequiredViewAsType(source, R.id.button_changehostonline, "field 'button_changehostonline'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SetHostActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.host_textview = null;
    target.editText_inputhost = null;
    target.button_changehostbyhand = null;
    target.button_changehostonline = null;
  }
}
