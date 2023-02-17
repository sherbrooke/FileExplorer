package com.mob.testsomething;

public class FileInfo {
	private boolean isDir;
	private String name;
	private String modifyDate;
	private int subDirSize; //是目录才会有subdir
	private String size;//不是目录才会显示文件大小


	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean dir) {
		isDir = dir;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public int getSubDirSize() {
		return subDirSize;
	}

	public void setSubDirSize(int subDirSize) {
		this.subDirSize = subDirSize;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
