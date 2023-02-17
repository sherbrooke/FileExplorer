package com.mob.testsomething;

import static com.mob.testsomething.FileUtils.getFileInfoFromPath;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;


//todo appbar修改 左边返回图标， 然后是目录名 右边待定
public class MainActivity extends AppCompatActivity {

	private RecyclerView fileList;
	private FileListAdapter adapter;
	private String rootPath = "/sdcard/电影";
	private List<FileInfo> fileInfos;
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
			//从目录获取到子目录或文件的信息
			fileInfos = getFileInfoFromPath(rootPath);
			adapter = new FileListAdapter(this.getApplicationContext(), fileInfos);
			//点击某个目录时
			adapter.setOnItemClickLitener((view, position) -> {
				FileInfo fileInfo = fileInfos.get(position);
				if (fileInfo.isDir()) {
					rootPath = rootPath  + "/" + fileInfo.getName();
					fileInfos = getFileInfoFromPath(rootPath);
					adapter.setFileInfos(fileInfos);
					adapter.notifyDataSetChanged();
				} else {
					Intent intent = new Intent(MainActivity.this, PlayActivity.class);
					intent.putExtra("path", rootPath + "/" + fileInfo.getName());
					intent.putExtra("index", position);
					intent.putExtra("parentPath", rootPath );
					intent.putExtra("name", fileInfo.getName());
					startActivity(intent);
				}

			});
			//todo 类似QQ或者微信，左滑删除,长按变成弹出小框，删除或者重命名
			adapter.setOnItemLongClickLitener((view, position) -> {
				FileInfo fileInfo = fileInfos.get(position);
				AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setTitle("删除文件" + fileInfo.getName())
						.setPositiveButton("删除", (dialog1, which) -> {
							FileUtils.deleteFileThings(rootPath + "/" + fileInfo.getName() );
							fileInfos.remove(position);
							adapter.setFileInfos(fileInfos);
							adapter.notifyDataSetChanged();
						})
						.setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss())
						.setCancelable(true)
						.create();
				dialog.show();
			});
			fileList.setLayoutManager(new LinearLayoutManager(this));
			fileList.setAdapter(adapter);
		} catch (Throwable t) {
//			t.printStackTrace();
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
		fileInfos = getFileInfoFromPath(rootPath);
		adapter.setFileInfos(fileInfos);
		adapter.notifyDataSetChanged();
	}

	private void initView() {
		fileList = findViewById(R.id.file_manager_list);
	}

	//申请所有文件管理权限
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
