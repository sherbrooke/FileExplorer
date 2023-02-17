package com.mob.testsomething;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class FileExApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		String rootDir = MMKV.initialize(this);
		System.out.println("mmkv root: " + rootDir);
	}
}
