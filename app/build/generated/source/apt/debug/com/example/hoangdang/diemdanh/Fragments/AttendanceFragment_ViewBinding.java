// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.Fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AttendanceFragment_ViewBinding implements Unbinder {
  private AttendanceFragment target;

  @UiThread
  public AttendanceFragment_ViewBinding(AttendanceFragment target, View source) {
    this.target = target;

    target._use_checklistButton = Utils.findRequiredViewAsType(source, R.id.use_checklist_button, "field '_use_checklistButton'", Button.class);
    target._use_quizButton = Utils.findRequiredViewAsType(source, R.id.use_quiz_button, "field '_use_quizButton'", Button.class);
    target._use_face_recButton = Utils.findRequiredViewAsType(source, R.id.use_face_rec_button, "field '_use_face_recButton'", Button.class);
    target._use_QRButton = Utils.findRequiredViewAsType(source, R.id.use_qr_button, "field '_use_QRButton'", Button.class);
    target._cancel_button = Utils.findRequiredViewAsType(source, R.id.cancel_attendance, "field '_cancel_button'", Button.class);
    target._finish_button = Utils.findRequiredViewAsType(source, R.id.finish_attendance, "field '_finish_button'", Button.class);
    target._current_sessionGrip = Utils.findRequiredViewAsType(source, R.id.gridView_current_session, "field '_current_sessionGrip'", GridView.class);
    target._qllh_ll = Utils.findRequiredViewAsType(source, R.id.qllh_ll, "field '_qllh_ll'", LinearLayout.class);
    target._use_qllh_button = Utils.findRequiredViewAsType(source, R.id.use_qllh_button, "field '_use_qllh_button'", Button.class);
    target._statistic_title = Utils.findRequiredViewAsType(source, R.id.statistic_ll, "field '_statistic_title'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AttendanceFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._use_checklistButton = null;
    target._use_quizButton = null;
    target._use_face_recButton = null;
    target._use_QRButton = null;
    target._cancel_button = null;
    target._finish_button = null;
    target._current_sessionGrip = null;
    target._qllh_ll = null;
    target._use_qllh_button = null;
    target._statistic_title = null;
  }
}
