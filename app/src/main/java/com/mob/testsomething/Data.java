package com.mob.testsomething;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Data {

	public static String md5Str(String data) throws Throwable {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data.getBytes("utf-8"));
		byte[] md5 = md.digest();
		return toHex(md5);
	}

	public static String md5(byte[] data) throws Throwable {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data);
		byte[] md5 = md.digest();
		return toHex(md5);
	}

	private static String toHex(byte[] data) {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			buffer.append(String.format("%02x", data[i]));
		}
		return buffer.toString();
	}

	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bytesToHexString(digest.digest());
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}
