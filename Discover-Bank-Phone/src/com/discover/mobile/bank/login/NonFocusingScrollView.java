package com.discover.mobile.bank.login;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;
/**
 * http://stackoverflow.com/questions/5375838/scrollview-disable-focus-move
 *
 *A scroll view which does not focus its child views when it scrolls.
 * @author scottseward
 *
 */

public class NonFocusingScrollView extends ScrollView {

  public NonFocusingScrollView(final Context context) {
    super(context);
  }

  public NonFocusingScrollView(final Context context, final AttributeSet attrs) {
    super(context, attrs);
  }

  public NonFocusingScrollView(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected boolean onRequestFocusInDescendants(final int direction, final Rect previouslyFocusedRect) {
    return true;
  }

}