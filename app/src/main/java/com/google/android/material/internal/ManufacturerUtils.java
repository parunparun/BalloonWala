package com.google.android.material.internal;

import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class ManufacturerUtils {
    private ManufacturerUtils() {
    }

    public static boolean isSamsungDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("samsung");
    }
}
