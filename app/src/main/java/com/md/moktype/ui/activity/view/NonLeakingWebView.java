package com.md.moktype.ui.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.lang.reflect.Field;

public class NonLeakingWebView extends WebView {

    private static Field sConfigCallback;

    static {
        try {
            sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);
        } catch (Exception e) {
            // ignored
        }
    }

    public NonLeakingWebView(Context context) {
        super(context);
    }

    public NonLeakingWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonLeakingWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void destroy() {
        doClearCache();
        super.destroy();
    }

    /**
     * clearCache
     */
    public void doClearCache() {
        try {
            if(sConfigCallback!=null)
                sConfigCallback.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
