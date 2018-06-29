package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;

import okhttp3.Cache;

/**
 * Created by Jv on 6/27/2018.
 */
@GlideModule
public class MemeItGlideModule extends AppGlideModule {
    public static final long CACHE_SIZE=10*1024*1024;
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
            builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context,"MemeIt",CACHE_SIZE));
    }
}
