// Generated code from Butter Knife. Do not modify!
package com.example.hoangdang.diemdanh.currentSessionImage;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.hoangdang.diemdanh.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UploadPhotoActivity_ViewBinding implements Unbinder {
  private UploadPhotoActivity target;

  @UiThread
  public UploadPhotoActivity_ViewBinding(UploadPhotoActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public UploadPhotoActivity_ViewBinding(UploadPhotoActivity target, View source) {
    this.target = target;

    target.UploadListView = Utils.findRequiredViewAsType(source, R.id.myuploadlistview, "field 'UploadListView'", ListView.class);
    target.UploadButton = Utils.findRequiredViewAsType(source, R.id.UploadButton, "field 'UploadButton'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    UploadPhotoActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.UploadListView = null;
    target.UploadButton = null;
  }
}
