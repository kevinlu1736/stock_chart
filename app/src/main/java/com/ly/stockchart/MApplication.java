package com.ly.stockchart;

import android.app.Application;
import android.content.Context;

/**
 * Created by ly on 04/06/2017.
 */

public class MApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
