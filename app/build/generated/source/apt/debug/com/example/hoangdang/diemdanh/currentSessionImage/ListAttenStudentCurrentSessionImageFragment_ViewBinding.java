// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.currentSessionImage;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.GridView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ListAttenStudentCurrentSessionImageFragment_ViewBinding implements Unbinder {
  private ListAttenStudentCurrentSessionImageFragment target;

  @UiThread
  public ListAttenStudentCurrentSessionImageFragment_ViewBinding(ListAttenStudentCurrentSessionImageFragment target,
      View source) {
    this.target = target;

    target.listView = Utils.findRequiredViewAsType(source, R.id.current_session_image_listView, "field 'listView'", GridView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ListAttenStudentCurrentSessionImageFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.listView = null;
  }
}
