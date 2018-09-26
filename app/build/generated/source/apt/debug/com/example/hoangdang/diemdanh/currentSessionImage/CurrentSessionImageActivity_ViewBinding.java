// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.currentSessionImage;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CurrentSessionImageActivity_ViewBinding implements Unbinder {
  private CurrentSessionImageActivity target;

  @UiThread
  public CurrentSessionImageActivity_ViewBinding(CurrentSessionImageActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CurrentSessionImageActivity_ViewBinding(CurrentSessionImageActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.current_session_image_list_toolbar, "field 'toolbar'", Toolbar.class);
    target.tabLayout = Utils.findRequiredViewAsType(source, R.id.current_session_image_list_tabs, "field 'tabLayout'", TabLayout.class);
    target.viewPager = Utils.findRequiredViewAsType(source, R.id.current_session_image_list_viewpager, "field 'viewPager'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CurrentSessionImageActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.tabLayout = null;
    target.viewPager = null;
  }
}
