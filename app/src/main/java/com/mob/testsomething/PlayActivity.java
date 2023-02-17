package com.mob.testsomething;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


// TODO: 2023/2/16 mkv，flv等格式是否都支持
//todo 记录当前位置
//todo stop的时候，记录MD5，当前位置。
public class PlayActivity extends Activity {
	private static final String TAG = "MainActivity2";
	private String url = "http://vfx.mtime.cn/Video/2019/03/09/mp4/190309153658147087.mp4";
	private String parentPath = "";
	private StyledPlayerView playerView;
	private ExoPlayer mPlayer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bilibili);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initView();
		initData(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initData(intent);
	}

	private void initData(Intent intent) {
		if (intent != null) {
			url = intent.getStringExtra("path");
			parentPath = intent.getStringExtra("parentPath");
			if (TextUtils.isEmpty(parentPath)) {
				//todo 获取url的上级目录
			}



		}
		mPlayer = new ExoPlayer.Builder(this).build();
		mPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
		mPlayer.addListener(new Player.Listener() {
			@Override
			public void onPlaybackStateChanged(int playbackState) {
				Player.Listener.super.onPlaybackStateChanged(playbackState);
				if (playbackState == Player.STATE_BUFFERING) {
//					Toast.makeText(this@MainActivity,"加载中",Toast.LENGTH_LONG).show()
				} else if (playbackState == Player.STATE_READY) {
					playerView.hideController();
//					Toast.makeText(this@MainActivity,"播放中",Toast.LENGTH_LONG).show()
				} else if (playbackState == Player.STATE_ENDED) {
//					Toast.makeText(this@MainActivity,"播放完成",Toast.LENGTH_LONG).show()
				}
			}

			@Override
			public void onPlayerError(PlaybackException error) {
				Player.Listener.super.onPlayerError(error);
				Log.e("ssss", "播放失败" + error);
			}
		});
		playerView.setPlayer(mPlayer);
		playerView.setControllerShowTimeoutMs(3000);
		playerView.setFullscreenButtonClickListener(new StyledPlayerView.FullscreenButtonClickListener() {
			@Override
			public void onFullscreenButtonClick(boolean isFullScreen) {
				//如果是横屏，那么变为竖屏
				//如果当前是竖屏，变为横屏
				if (isFullScreen) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
				}
			}
		});
		playerView.requestFocus();

		List<String> fileInfoFromPath = getFileInfoFromPath(parentPath);
		boolean needAdd = false;
		for (int i=0;i<fileInfoFromPath.size();i++) {
			if (fileInfoFromPath.get(i).equals(url) || needAdd) {
				needAdd =true;
				MediaItem mediaItem = MediaItem.fromUri(Uri.parse(fileInfoFromPath.get(i)));
				mPlayer.addMediaItem(mediaItem);//准备媒体资源
			}
		}
		needAdd = false;
//		MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
//		mPlayer.setMediaItem(mediaItem);//准备媒体资源
		mPlayer.prepare();
		mPlayer.play();//开始播放
	}

	private List<String> getFileInfoFromPath(String rootPath) {
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

	private void initView() {
		playerView = findViewById(R.id.exo_player);
	}

//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		updateTrackSelectorParameters();
//		updateStartPosition();
////		outState.putBundle(KEY_TRACK_SELECTION_PARAMETERS, trackSelectionParameters.toBundle());
////		outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
////		outState.putInt(KEY_ITEM_INDEX, startItemIndex);
////		outState.putLong(KEY_POSITION, startPosition);
////		saveServerSideAdsLoaderState(outState);
//	}

	@Override
	public void onStart() {
		super.onStart();
		if (Build.VERSION.SDK_INT > 23) {
			if (playerView != null) {
				playerView.onResume();
				playerView.requestFocus();
			}
			if (mPlayer != null) {
				mPlayer.prepare();
				mPlayer.play();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT <= 23 || mPlayer == null) {
//			initializePlayer();
			if (playerView != null) {
				playerView.onResume();
				playerView.requestFocus();
			}
			// TODO: 2023/2/17 根据md5,获取mmkv中的position值，将进度定位
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (Build.VERSION.SDK_INT <= 23) {
			if (playerView != null) {
				playerView.onPause();
			}
			releasePlayer();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (Build.VERSION.SDK_INT > 23) {
			if (playerView != null) {
				playerView.onPause();
			}
			if (mPlayer != null) {
				mPlayer.stop();
			}
			// TODO: 2023/2/17 currentUrl? 播放到下一个目录时
			File file = new File(url);
			String md5 = Data.getFileMD5(file);
			// TODO: 2023/2/17 根据md5,以及当前的position，保存到mmkv中
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPlayer.stop();
		mPlayer.release();
	}

//	protected boolean initializePlayer() {
//		if (mPlayer == null) {
//			Intent intent = getIntent();
//
////			mediaItems = createMediaItems(intent);
////			if (mediaItems.isEmpty()) {
////				return false;
////			}
//
//			ExoPlayer.Builder playerBuilder =
//					new ExoPlayer.Builder(/* context= */ this);
//			mPlayer = playerBuilder.build();
//			mPlayer.addAnalyticsListener(new EventLogger());
//			mPlayer.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
//			mPlayer.setPlayWhenReady(true);
//			playerView.setPlayer(mPlayer);
//		}
//		boolean haveStartPosition = startItemIndex != C.INDEX_UNSET;
//		if (haveStartPosition) {
//			mPlayer.seekTo(startItemIndex, startPosition);
//		}
//		mPlayer.setMediaItems(mediaItems, /* resetPosition= */ !haveStartPosition);
//		mPlayer.prepare();
//		return true;
//	}


	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	protected void releasePlayer() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
			playerView.setPlayer(/* player= */ null);
		}
	}

}