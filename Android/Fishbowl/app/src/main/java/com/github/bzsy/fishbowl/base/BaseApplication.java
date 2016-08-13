package com.github.bzsy.fishbowl.base;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.avos.avoscloud.AVOSCloud;
import com.github.bzsy.fishbowl.LocalConfigure;
import com.github.bzsy.fishbowl.R;
import com.github.bzsy.fishbowl.util.DebugLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by bzsy on 16/1/7.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, LocalConfigure.applicationId, LocalConfigure.clientKey);
        initImageLoader();
    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.grey_light)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);
    }
}
