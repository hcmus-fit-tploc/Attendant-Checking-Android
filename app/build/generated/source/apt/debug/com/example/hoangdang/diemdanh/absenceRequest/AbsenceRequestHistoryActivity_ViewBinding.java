// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.absenceRequest;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AbsenceRequestHistoryActivity_ViewBinding implements Unbinder {
  private AbsenceRequestHistoryActivity target;

  @UiThread
  public AbsenceRequestHistoryActivity_ViewBinding(AbsenceRequestHistoryActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AbsenceRequestHistoryActivity_ViewBinding(AbsenceRequestHistoryActivity target,
      View source) {
    this.target = target;

    target._listview = Utils.findRequiredViewAsType(source, R.id.absence_request_history_listView, "field '_listview'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AbsenceRequestHistoryActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._listview = null;
  }
}
