// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.classManagement;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class classManagementActivity_ViewBinding implements Unbinder {
  private classManagementActivity target;

  @UiThread
  public classManagementActivity_ViewBinding(classManagementActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public classManagementActivity_ViewBinding(classManagementActivity target, View source) {
    this.target = target;

    target.listView = Utils.findRequiredViewAsType(source, R.id.class_management_listView, "field 'listView'", GridView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.class_management_toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    classManagementActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.listView = null;
    target.toolbar = null;
  }
}
