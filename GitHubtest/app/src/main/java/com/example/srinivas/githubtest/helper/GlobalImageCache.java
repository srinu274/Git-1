package com.example.srinivas.githubtest.helper;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Srinivas on 11-04-2015.
 */
public class GlobalImageCache extends LruCache<String,Bitmap> implements ComponentCallbacks2{

    public GlobalImageCache(int maxSize){
        super(maxSize);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int level){
        if(level>=TRIM_MEMORY_MODERATE){
            evictAll();
        } else if(level >= TRIM_MEMORY_UI_HIDDEN){
            trimToSize(size()/2);
        }
    }
}
