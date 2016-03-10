package com.ctc.gis;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;

public class ListInfoActivity extends Activity {	
	
	private static final String TAG = "ListInfoActivity";
	
	// 入参
	private String mCity; // 当前城市
	private String mKeywords; // 搜索词
	private LatLng mCurrentLatLng; // 当前位置
	// POI检索结果
	private PoiSearch mPoiSearch = null;
	private ArrayList<PoiInfo> mPoiInfos = new ArrayList<PoiInfo>();
	// Gird
	private ArrayList<Grid> mGrids = new ArrayList<Grid>();
	// ListView
	private int mCount;
	private ListView mPoiInfoList;
	private PoiInfoListAdapter mPoiInfoListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_info);
		Intent intent = getIntent();
		mCity = intent.getStringExtra("city");
		mKeywords = intent.getStringExtra("keywords");
		mCurrentLatLng = new LatLng(intent.getDoubleExtra("latitude", 32), intent.getDoubleExtra("longitude", 118));
		Log.v(TAG, "city: "+mCity+" keywords: "+mKeywords+" latlng: "+mCurrentLatLng.toString());
		
		// POI搜索
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {			
			@Override
			public void onGetPoiResult(PoiResult result) {
				if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
					Toast.makeText(ListInfoActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
					return;
				}
				if (result.error == SearchResult.ERRORNO.NO_ERROR) {					
					ArrayList<PoiInfo> poiInfos = (ArrayList<PoiInfo>) result.getAllPoi();					
					mCount = poiInfos.size() < 10 ? poiInfos.size() : 10;
					PoiInfo poiInfo;
					for(int i=0; i<mCount; i++){
						poiInfo = poiInfos.get(i);
						// POI类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路
						if(poiInfo.type.getInt() == 0 || poiInfo.type.getInt() == 3) {
							mPoiInfos.add(poiInfo); // 剔除公交、地铁线路
							Log.v(TAG, "poi infos:"+" "+poiInfo.type.getInt()+" "+poiInfo.name+" "+poiInfo.address+" "+poiInfo.location.toString());
							Grid grid = new Grid();
							grid.setPoiName(poiInfo.name.trim());
							grid.setAddress(poiInfo.address.trim());
							grid.setLatitude(poiInfo.location.latitude);
							grid.setLongitude(poiInfo.location.longitude);								
							LatLng latLng = new LatLng(poiInfo.location.latitude, poiInfo.location.longitude);
							grid.setDistance(String.valueOf((int)DistanceUtil.getDistance(mCurrentLatLng, latLng))+"米");// 计算POI到当前位置的距离
							mGrids.add(grid);
						}
					}
					if (mPoiInfos != null) 
						mCount = mPoiInfos.size();
					mPoiInfoList = (ListView) findViewById(R.id.poi_list);
					mPoiInfoListAdapter = new PoiInfoListAdapter(ListInfoActivity.this);
					mPoiInfoList.setAdapter(mPoiInfoListAdapter);
					return;
				}
				if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
					// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
					String strInfo = "在";
					for (CityInfo cityInfo : result.getSuggestCityList()) {
						strInfo += cityInfo.city;
						strInfo += ",";
					}
					strInfo += "找到结果";
					Toast.makeText(ListInfoActivity.this, strInfo, Toast.LENGTH_LONG).show();
				}				
			}
			
			@Override
			public void onGetPoiDetailResult(PoiDetailResult arg0) {
				// TODO Auto-generated method stub
			}
		});
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(mCity)
				.keyword(mKeywords)
				.pageNum(0));
		
		initTitleBar(mKeywords);
	}
	
	// 初始化标题
	public void initTitleBar (String str) {
		ImageView backImageView = (ImageView) findViewById(R.id.title_bar_left_icon);
		backImageView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView title = (TextView) findViewById(R.id.title_bar_center_text);
		title.setText(str);
		Button button = (Button) findViewById(R.id.title_bar_right_button);
		Drawable drawable = getResources().getDrawable(R.drawable.icon_map);
		button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		button.setText(" 地图");
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListInfoActivity.this, PoiMapActivity.class);
				intent.putExtra("keywords", mKeywords);
				intent.putParcelableArrayListExtra("grids", mGrids);
				startActivity(intent);
			}
		});
	}
	
//	public class DataTask extends AsyncTask<Void, Void, Void> {
//		
//		private Grid grid;
//		
//		public DataTask(Grid grid) {
//			this.grid = grid;
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {			  
//		    if(isCancelled())   
//		    	return null;// Task被取消, 退出
//			try {
//				Log.v(TAG, "thread: "+Thread.currentThread().getName());
//
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			if(isCancelled())
//				return;
//			mPoiInfoListAdapter.notifyDataSetChanged();			
//		}		
//	}
//	


	private class ViewHolder {
		TextView number;
		TextView poiName;
		TextView address;
		TextView distance;
		Button detail;
	}
	
	public class PoiInfoListAdapter extends BaseAdapter {
		
		ViewHolder viewHolder;
		private Context context;
		
		public PoiInfoListAdapter(Context context) {
			super();
			this.context = context;
		}

		@Override
		public int getCount() {
			if (mPoiInfos != null) {
				return mPoiInfos.size();
			}
			else return 0;			
		}

		@Override
		public Object getItem(int position) {
			if (mPoiInfos != null) {
				return mPoiInfos.get(position);
			}				
			else return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {			
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.poi_item, null);
				viewHolder.number = (TextView) convertView.findViewById(R.id.number);
				viewHolder.poiName = (TextView) convertView.findViewById(R.id.poiname);
				viewHolder.address = (TextView) convertView.findViewById(R.id.address);
				viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
				viewHolder.detail = (Button) convertView.findViewById(R.id.btn_detail);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}			
			viewHolder.number.setText(String.valueOf(position+1));
			viewHolder.poiName.setText(mGrids.get(position).getPoiName());
			viewHolder.address.setText(mGrids.get(position).getAddress());
			viewHolder.distance.setText(mGrids.get(position).getDistance());
//			final int mPosition = position;
			viewHolder.detail.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
				
				}
			});
			return convertView;
		}		
	}
	
	@Override
	public void finish(){
	    super.finish();
	}
	
	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}
}
