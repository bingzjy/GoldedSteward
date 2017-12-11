package com.ldnet.activity.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.ldnet.activity.find.FreaMarket_Create;
import com.ldnet.activity.find.Weekend_Create;
import com.ldnet.activity.home.HouseRentUpdate;
import com.ldnet.activity.home.HouseRent_Create;
import com.ldnet.activity.home.Property_Complain_Create;
import com.ldnet.activity.home.Property_Repair_Create;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CustomConstants;
import com.ldnet.utility.ImageFetcher;
import com.ldnet.utility.IntentConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择相册
 * 
 */

public class ImageBucketChooseActivity extends Activity
{
	private ImageFetcher mHelper;
	private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
	private ListView mListView;
	private ImageBucketAdapter mAdapter;
	private int availableSize;
	private String classname = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_image_bucket_choose);
		classname = getIntent().getStringExtra("classname");
		mHelper = ImageFetcher.getInstance(getApplicationContext());
		initData();
		initView();
	}

	private void initData()
	{
		mDataList = mHelper.getImagesBucketList(false);
		availableSize = getIntent().getIntExtra(
				IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
				CustomConstants.MAX_IMAGE_SIZE);
	}

	private void initView()
	{
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ImageBucketAdapter(this, mDataList);
		mListView.setAdapter(mAdapter);
		TextView titleTv  = (TextView) findViewById(R.id.title);
		titleTv.setText("相册");
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
			{

				selectOne(position);

				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ImageChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
						(Serializable) mDataList.get(position).imageList);
				intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
						mDataList.get(position).bucketName);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						availableSize);
				intent.putExtra("classname",classname);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
				finish();
			}
		});

		TextView cancelTv = (TextView) findViewById(R.id.action);
		cancelTv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if(classname.equals("FreaMarket_Create")){
					Intent intent = new Intent(ImageBucketChooseActivity.this,
							FreaMarket_Create.class);
					intent.putExtra("flag","true");
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					finish();
				}else if(classname.equals("Weekend_Create")){
					Intent intent = new Intent(ImageBucketChooseActivity.this,
							Weekend_Create.class);
					intent.putExtra("flag","true");
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					finish();
				}else if(classname.equals("HouseRent_Create")){
					Intent intent = new Intent(ImageBucketChooseActivity.this,
							HouseRent_Create.class);
					intent.putExtra("flag","true");
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					finish();
				}else if(classname.equals("HouseRentUpdate")){
					Intent intent = new Intent(ImageBucketChooseActivity.this,
							HouseRentUpdate.class);
					intent.putExtra("flag","true");
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					finish();
				}else if(classname.equals("Property_Complain_Create")){
					Intent intent = new Intent(ImageBucketChooseActivity.this,
							Property_Complain_Create.class);
					intent.putExtra("flag","true");
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					finish();
				}else if(classname.equals("Property_Repair_Create")){
					Intent intent = new Intent(ImageBucketChooseActivity.this,
							Property_Repair_Create.class);
					intent.putExtra("flag","true");
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					finish();
				}
			}
		});
	}

	private void selectOne(int position)
	{
		int size = mDataList.size();
		for (int i = 0; i != size; i++)
		{
			if (i == position) mDataList.get(i).selected = true;
			else
			{
				mDataList.get(i).selected = false;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			if(classname.equals("FreaMarket_Create")){
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						FreaMarket_Create.class);
				intent.putExtra("flag","true");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
				finish();
			}else if(classname.equals("Weekend_Create")){
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						Weekend_Create.class);
				intent.putExtra("flag","true");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
				finish();
			}else if(classname.equals("HouseRent_Create")){
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						HouseRent_Create.class);
				intent.putExtra("flag","true");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
				finish();
			}else if(classname.equals("HouseRentUpdate")){
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						HouseRentUpdate.class);
				intent.putExtra("flag","true");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
				finish();
			}else if(classname.equals("Property_Complain_Create")){
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						Property_Complain_Create.class);
				intent.putExtra("flag","true");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
				finish();
			}else if(classname.equals("Property_Repair_Create")){
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						Property_Repair_Create.class);
				intent.putExtra("flag","true");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
