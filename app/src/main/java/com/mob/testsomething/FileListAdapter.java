package com.mob.testsomething;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListViewHolder> {

	private Context context;
	private List<FileInfo> fileInfos;
	private OnItemClickLitener mOnItemClickLitener;
	private OnItemLongClickLitener mOnItemLongClickLitener;

	public FileListAdapter(Context context, List<FileInfo> fileInfos) {
		this.context = context;
		this.fileInfos = fileInfos;
	}

	public void setFileInfos(List<FileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

	@NonNull
	@Override
	public FileListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dir_or_file, parent, false);
		return new FileListViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull FileListViewHolder holder, int position) {
		FileInfo bean = fileInfos.get(position);
		if (bean.isDir()) {
			holder.icon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.folder, null));
			holder.size.setText(bean.getSubDirSize() + "项");
		} else {
			holder.icon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.file, null));
			holder.size.setText(bean.getSize());
		}
		holder.name.setText(bean.getName());
		holder.date.setText(bean.getModifyDate());
		if (mOnItemClickLitener != null) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mOnItemClickLitener.onItemClick(view, position);
				}
			});
		}

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				mOnItemLongClickLitener.setOnItemLongClickLitener(v, position);
				return false;
			}
		});
	}

	@Override
	public int getItemCount() {
		return fileInfos == null ? 0 : fileInfos.size();
	}

	//设置回调接口
	public interface OnItemClickLitener {
		void onItemClick(View view, int position);
	}

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	//设置回调接口
	public interface OnItemLongClickLitener {
		void setOnItemLongClickLitener(View view, int position);
	}

	public void setOnItemLongClickLitener(OnItemLongClickLitener mOnItemLongClickLitener) {
		this.mOnItemLongClickLitener = mOnItemLongClickLitener;
	}

	class FileListViewHolder extends RecyclerView.ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView size;
		private TextView date;

		public FileListViewHolder(@NonNull View itemView) {
			super(itemView);
			icon = itemView.findViewById(R.id.file_or_fouder);
			name = itemView.findViewById(R.id.file_name);
			size = itemView.findViewById(R.id.file_size);
			date = itemView.findViewById(R.id.file_date);
		}
	}
}
