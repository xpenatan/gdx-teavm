package com.github.xpenatan.gdx.teavm.android;

import android.app.Activity;
import android.os.Bundle;

public class TeaAndroidActivity extends Activity {
    private TeaAndroidView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = createTeaAndroidView();
        setContentView(view);
    }

    protected TeaAndroidView createTeaAndroidView() {
        return new TeaAndroidView(this);
    }

    @Override
    protected void onPause() {
        if(view != null) {
            view.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(view != null) {
            view.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if(view != null) {
            view.dispose();
            view = null;
        }
        super.onDestroy();
    }
}
