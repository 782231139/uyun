package com.uyun.hummer.view.refresh;

/**
 * Created by zhu on 2018/3/22.
 */

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;


import com.tencent.smtt.sdk.WebView;


/**
 *        兼容QQ X5WebView
 */
public class X5WebViewSwipeRefreshLayout extends SwipeRefreshLayout {
    public X5WebViewSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean canChildScrollUp() {
        View child = getChildAt(0);
        if (child != null && child instanceof WebView) {
            View scrollView = ((WebView) child).getChildAt(0);
            if (scrollView != null)
                return scrollView.getScrollY() != 0;
        }
        return super.canChildScrollUp();
    }
}