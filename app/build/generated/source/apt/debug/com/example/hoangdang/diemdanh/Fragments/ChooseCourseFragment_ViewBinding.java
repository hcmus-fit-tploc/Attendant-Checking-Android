// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.Fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChooseCourseFragment_ViewBinding implements Unbinder {
  private ChooseCourseFragment target;

  @UiThread
  public ChooseCourseFragment_ViewBinding(ChooseCourseFragment target, View source) {
    this.target = target;

    target._choose_course_listView = Utils.findRequiredViewAsType(source, R.id.choose_course_listView, "field '_choose_course_listView'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChooseCourseFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._choose_course_listView = null;
  }
}
