// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.feedback;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FeedbackHistoryActivity_ViewBinding implements Unbinder {
  private FeedbackHistoryActivity target;

  @UiThread
  public FeedbackHistoryActivity_ViewBinding(FeedbackHistoryActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public FeedbackHistoryActivity_ViewBinding(FeedbackHistoryActivity target, View source) {
    this.target = target;

    target._listview = Utils.findRequiredViewAsType(source, R.id.feedback_history_listView, "field '_listview'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FeedbackHistoryActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target._listview = null;
  }
}
