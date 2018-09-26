// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.Fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ProfileFragment_ViewBinding implements Unbinder {
  private ProfileFragment target;

  @UiThread
  public ProfileFragment_ViewBinding(ProfileFragment target, View source) {
    this.target = target;

    target._profile_img = Utils.findRequiredViewAsType(source, R.id.profile_img, "field '_profile_img'", ImageView.class);
    target._first_name = Utils.findRequiredViewAsType(source, R.id.first_name_tv, "field '_first_name'", TextView.class);
    target._last_name = Utils.findRequiredViewAsType(source, R.id.last_name_tv, "field '_last_name'", TextView.class);
    target._stud_id = Utils.findRequiredViewAsType(source, R.id.stud_id_tv, "field '_stud_id'", TextView.class);
    target._email = Utils.findRequiredViewAsType(source, R.id.email_tv, "field '_email'", TextView.class);
    target._phone = Utils.findRequiredViewAsType(source, R.id.phone_tv, "field '_phone'", TextView.class);
    target._changePassword = Utils.findRequiredViewAsType(source, R.id.btn_changePassword, "field '_changePassword'", Button.class);
    target.stud_id_ll = Utils.findRequiredViewAsType(source, R.id.stud_id_ll, "field 'stud_id_ll'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ProfileFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._profile_img = null;
    target._first_name = null;
    target._last_name = null;
    target._stud_id = null;
    target._email = null;
    target._phone = null;
    target._changePassword = null;
    target.stud_id_ll = null;
  }
}
