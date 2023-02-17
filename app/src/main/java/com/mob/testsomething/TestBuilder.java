package com.mob.testsomething;

import android.os.SystemClock;
import android.util.Log;

// TODO: 2020/5/8 执行完代码块之后才会执行构造方法，两个都执行完之后才算是生成了对象
// TODO: 2020/5/8 执行顺序 静态块 构造块 构造方法
public class TestBuilder {
	public static class SingleTon {
		public final static TestBuilder instance = new TestBuilder();
	}

	public static TestBuilder getInstance() {
		Log.e("ssss","getinstance");
		return SingleTon.instance;
	}

	public TestBuilder() {
//		sleep();
		Log.e("ssss", "counst2");
		sleep();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				sleep();
//			}
//		}).start();
	}

	public void exe1() {
		Log.e("ssss", "exe1");
	}
	public void exe2() {
		Log.e("ssss", "exe2");
	}

	private static void sleep() {
		Log.e("ssss","sleep");
		SystemClock.sleep(5000);
	}
}
