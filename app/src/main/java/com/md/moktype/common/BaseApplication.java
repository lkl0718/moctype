package com.md.moktype.common;

import android.app.Application;
import android.content.ContextWrapper;

import com.md.moktype.utils.Prefs;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Preference 인스턴스를 초기화합니다.
        Prefs.init(getApplicationContext(), getPackageName(), ContextWrapper.MODE_PRIVATE);
    }
}
