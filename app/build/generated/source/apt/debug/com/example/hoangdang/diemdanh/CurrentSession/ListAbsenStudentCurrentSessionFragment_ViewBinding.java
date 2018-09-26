// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.CurrentSession;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ListAbsenStudentCurrentSessionFragment_ViewBinding implements Unbinder {
  private ListAbsenStudentCurrentSessionFragment target;

  @UiThread
  public ListAbsenStudentCurrentSessionFragment_ViewBinding(ListAbsenStudentCurrentSessionFragment target,
      View source) {
    this.target = target;

    target.listView = Utils.findRequiredViewAsType(source, R.id.current_session_listView, "field 'listView'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ListAbsenStudentCurrentSessionFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.listView = null;
  }
}
