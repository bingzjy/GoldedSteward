package com.ldnet.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImagePublishAdapter extends BaseAdapter
{
	private List<ImageItem> mDataList = new ArrayList<ImageItem>();
	private Context mContext;
	protected DisplayImageOptions imageOptions;
	String aa = Services.timeFormat();
	String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

	public ImagePublishAdapter(Context context, List<ImageItem> dataList)
	{
		this.mContext = context;
		this.mDataList = dataList;
		imageOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
				.showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.resetViewBeforeLoading(true)
				.extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
				.build();
	}

	public int getCount()
	{
		// 多返回一个用于展示添加图标
		if (mDataList == null)
		{
			return 1;
		}
		else if (mDataList.size() == CustomConstants.MAX_IMAGE_SIZE)
		{
			return CustomConstants.MAX_IMAGE_SIZE;
		}
		else
		{
			return mDataList.size() + 1;
		}
	}

	public Object getItem(int position)
	{
		if (mDataList != null
				&& mDataList.size() == CustomConstants.MAX_IMAGE_SIZE)
		{
			return mDataList.get(position);
		}

		else if (mDataList == null || position - 1 < 0
				|| position > mDataList.size())
		{
			return null;
		}
		else
		{
			return mDataList.get(position - 1);
		}
	}

	public long getItemId(int position)
	{
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
		convertView = View.inflate(mContext, R.layout.item_publish1, null);
		ImageView imageIv = (ImageView) convertView
				.findViewById(R.id.item_grid_image);

		if (isShowAddItem(position))
		{
			imageIv.setImageResource(R.drawable.picture_plus);
			imageIv.setBackgroundResource(R.color.bg_gray);
		}
		else
		{
			final ImageItem item = mDataList.get(position);
			if(item.thumbnailPath != null){
				if( item.sourcePath.contains("/")){
					ImageDisplayer.getInstance(mContext).displayBmp(imageIv,
							item.thumbnailPath, item.sourcePath);
				}else {
					ImageLoader.getInstance().displayImage(Services.getImageUrl(item.sourcePath), imageIv, imageOptions);
				}
			}else{
				if( item.sourcePath.contains("/")){
					ImageDisplayer.getInstance(mContext).displayBmp(imageIv,
							item.thumbnailPath, item.sourcePath);
				}else {
					ImageLoader.getInstance().displayImage(Services.getImageUrl(item.sourcePath), imageIv, imageOptions);
				}
			}

		}

		return convertView;
	}

	private boolean isShowAddItem(int position)
	{
		int size = mDataList == null ? 0 : mDataList.size();
		return position == size;
	}

}
