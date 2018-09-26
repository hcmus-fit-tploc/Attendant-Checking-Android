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

public class LoginActivity_ViewBinding implements Unbinder {
  private LoginActivity target;

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target, View source) {
    this.target = target;

    target._email_editText = Utils.findRequiredViewAsType(source, R.id.email_editText, "field '_email_editText'", EditText.class);
    target._password_editText = Utils.findRequiredViewAsType(source, R.id.password_editText, "field '_password_editText'", EditText.class);
    target._login_button = Utils.findRequiredViewAsType(source, R.id.login_button, "field '_login_button'", Button.class);
    target._forgot_pw_textView = Utils.findRequiredViewAsType(source, R.id.forgot_pw_textView, "field '_forgot_pw_textView'", TextView.class);
    target.sethost_textView = Utils.findRequiredViewAsType(source, R.id.setting_host, "field 'sethost_textView'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._email_editText = null;
    target._password_editText = null;
    target._login_button = null;
    target._forgot_pw_textView = null;
    target.sethost_textView = null;
  }
}
