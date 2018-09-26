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

public class ListStudentFragment_ViewBinding implements Unbinder {
  private ListStudentFragment target;

  @UiThread
  public ListStudentFragment_ViewBinding(ListStudentFragment target, View source) {
    this.target = target;

    target.listView = Utils.findRequiredViewAsType(source, R.id.list_student_listView, "field 'listView'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ListStudentFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.listView = null;
  }
}
