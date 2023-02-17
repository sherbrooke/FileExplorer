package com.mob.testsomething;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


//todo appbar修改 左边返回图标， 然后是目录名 右边待定
public class MainActivity extends AppCompatActivity {

	private RecyclerView fileList;
	private FileListAdapter adapter;
	private String rootPath = "/sdcard/电影";
	private List<FileInfo> fileInfoFromPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		requestPermisssion();
		initView();
		initData();
	}

	private void initData() {
		try {
			fileInfoFromPath = getFileInfoFromPath(rootPath);
			adapter = new FileListAdapter(this.getApplicationContext(), fileInfoFromPath);
			adapter.setOnItemClickLitener(new FileListAdapter.OnItemClickLitener() {
				@Override
				public void onItemClick(View view, int position) {
					FileInfo fileInfo = fileInfoFromPath.get(position);
					if (fileInfo.isDir()) {
						rootPath = rootPath  + "/" + fileInfo.getName();
						fileInfoFromPath = getFileInfoFromPath(rootPath);
						adapter.setFileInfos(fileInfoFromPath);
						adapter.notifyDataSetChanged();
					} else {
						Intent intent = new Intent(MainActivity.this, PlayActivity.class);
						intent.putExtra("path", rootPath + "/" + fileInfo.getName());
						intent.putExtra("index", position);
						intent.putExtra("parentPath", rootPath );
						intent.putExtra("name", fileInfo.getName());
						startActivity(intent);
					}

				}
			});
			//todo 类似QQ或者微信，左滑删除,长按变成弹出小框，删除或者重命名
			adapter.setOnItemLongClickLitener(new FileListAdapter.OnItemLongClickLitener() {
				@Override
				public void setOnItemLongClickLitener(View view, int position) {
					FileInfo fileInfo = fileInfoFromPath.get(position);
					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
							.setTitle("删除文件" + fileInfo.getName())
							.setPositiveButton("删除", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									deleteFileThings(rootPath + "/" + fileInfo.getName() );
									fileInfoFromPath.remove(position);
									adapter.setFileInfos(fileInfoFromPath);
									adapter.notifyDataSetChanged();
								}

							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}

							})
							.setCancelable(true)
							.create();
					dialog.show();
				}
			});
			LinearLayoutManager manager = new LinearLayoutManager(this);
			fileList.setLayoutManager(manager);
			fileList.setAdapter(adapter);
		} catch (Throwable t) {
//			t.printStackTrace();
		}
	}

	public void deleteFileThings(String path) {
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
	}

	@Override
	public void onBackPressed() {
		if (rootPath.equals("/sdcard") || rootPath.equals("/sdcard/")) {
			super.onBackPressed();
			return;
		}
		File newFile = new File(rootPath);
		if (newFile.exists()) {
			rootPath = newFile.getParentFile().getAbsolutePath();
		}
		fileInfoFromPath = getFileInfoFromPath(rootPath);
		adapter.setFileInfos(fileInfoFromPath);
		adapter.notifyDataSetChanged();
	}

	private List<FileInfo> getFileInfoFromPath(String rootPath) {
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

	private void initView() {
		fileList = findViewById(R.id.file_manager_list);
	}

	private void requestPermisssion() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
				Environment.isExternalStorageManager()) {
//			Toast.makeText(MainActivity.this, "已获得访问所有文件权限", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
			startActivity(intent);
		}
	}

}
