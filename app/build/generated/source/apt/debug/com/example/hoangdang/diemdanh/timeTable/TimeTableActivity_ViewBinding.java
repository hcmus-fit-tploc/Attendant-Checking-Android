// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.timeTable;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TimeTableActivity_ViewBinding implements Unbinder {
  private TimeTableActivity target;

  @UiThread
  public TimeTableActivity_ViewBinding(TimeTableActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public TimeTableActivity_ViewBinding(TimeTableActivity target, View source) {
    this.target = target;

    target._toolBar = Utils.findRequiredViewAsType(source, R.id.timetable_toolbar, "field '_toolBar'", Toolbar.class);
    target._gripView = Utils.findRequiredViewAsType(source, R.id.timetable_gripView, "field '_gripView'", GridView.class);
    target.free = Utils.findRequiredViewAsType(source, R.id.free_tv, "field 'free'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TimeTableActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._toolBar = null;
    target._gripView = null;
    target.free = null;
  }
}
