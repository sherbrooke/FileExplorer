package com.mob.testsomething;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FileUtils {
	public static List<FileInfo> getFileInfoFromPath(String rootPath) {
		List<FileInfo> fileInfos = new ArrayList<>();
		FileInfo info = null;
		File rootFile = new File(rootPath);
		if (!rootFile.exists() || !rootFile.isDirectory()) {
			return fileInfos;
		}
		File[] list = rootFile.listFiles();
		if (list == null || list.length <= 0) {
			return fileInfos;
		}
		for (File f: list) {
			info = new FileInfo();
			info.setName(f.getName());
			if (f.getName().startsWith(".")) {
				continue;
			}
			if (f.isDirectory()) {
				info.setDir(true);
				String[] list1 = f.list();
				if (list1 != null) {
					info.setSubDirSize(list1.length);
				}
			} else {
				info.setDir(false);
				long size = f.length();
				if (size > 1024*1024*1024L) {
					info.setSize(size/(1024*1024*1024L) + "G");
				} else if (size > 1024*1024L){
					info.setSize(size/(1024*1024L) + "M");
				} else if (size > 1024L){
					info.setSize(size/(1024L) + "k");
				} else {
					info.setSize(size + "b");
				}
			}
			info.setModifyDate(new Date(f.lastModified()).toString());
			fileInfos.add(info);
		}
		Collections.sort(fileInfos, new Comparator<FileInfo>() {
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				//排序规则：按照汉字拼音首字母排序
				Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
				//该排序为正序排序，如果倒序排序则将compare中的01和02互换位置
				return com.compare(o1.getName(), o2.getName());

			}
		});
		return fileInfos;
	}

	public static List<String> getStringsFromPath(String rootPath) {
		List<String> fileInfos = new ArrayList<>();
		FileInfo info = null;
		File rootFile = new File(rootPath);
		if (!rootFile.exists() || !rootFile.isDirectory()) {
			return fileInfos;
		}
		File[] list = rootFile.listFiles();
		if (list == null || list.length <= 0) {
			return fileInfos;
		}
		for (File f: list) {
			if (f.getName().startsWith(".")) {
				continue;
			}
			if (!f.isDirectory()) {
				fileInfos.add(f.getAbsolutePath());
			}
		}
		Collections.sort(fileInfos, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				//排序规则：按照汉字拼音首字母排序
				Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
				//该排序为正序排序，如果倒序排序则将compare中的01和02互换位置
				return com.compare(o1, o2);

			}
		});
		return fileInfos;
	}

	public static void deleteFileThings(String path) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(path);
					if (file.exists()) {
						if (file.isDirectory()) {
							File[] temp = file.listFiles(); //获取该文件夹下的所有文件
							for (File value : temp) {
								deleteFileThings(value.getAbsolutePath());
							}
						} else {
							file.delete(); //删除子文件
						}
						file.delete(); //删除文件夹
					}
				} catch (Throwable t) {

				}
			}
		}).start();

	}
}
