package com.ctc.gis;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

public class PoiMapActivity extends FragmentActivity {

	private static final String TAG = "PoiMapActivity";
	
	MapView mMapView = null;
    BaiduMap mBaiduMap;    
    // ViewPager
    private GalleryViewPager mViewPager;
    private GalleryAdapter mAdapter;
    // ����
    private LatLng mLatLng;
    private ArrayList<Grid> mGrids;

	// ����
	private ImageButton mZoomIn;
	private ImageButton mZoomOut;
	
	private BitmapDescriptor bitmapDescriptor;
	private List<BitmapDescriptor> bitmapDescriptors;
	    
	/**
	 * viewpager������
	 */
    public class GalleryAdapter extends FragmentPagerAdapter {

        private int count = 10;//Ĭ��1
        private FragmentManager fm;
        private ArrayList<Grid> grids;
        
        public GalleryAdapter(FragmentManager fm, ArrayList<Grid> grids) {
            super(fm);
            this.fm = fm;
            this.grids = grids;
            this.count = grids.size();
        }              

		@Override
        public Fragment getItem(int position) {
        	GridFragment gridFragment = new GridFragment();
        	Bundle bundle = new Bundle();
        	bundle.putParcelable("grid", grids.get(position));
        	bundle.putString("number", String.valueOf(position+1));
        	gridFragment.setArguments(bundle);
        	return gridFragment;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }        
    }	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
        //ע��÷���Ҫ��setContentView����֮ǰʵ��
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_poi_map);   
        // ��ȡ��ͼ�ؼ�����
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        
		Intent intent = getIntent();
		mGrids = intent.getParcelableArrayListExtra("grids");
		if (mGrids == null)	
			mGrids = new ArrayList<Grid>();
        initTitleBar(intent.getStringExtra("keywords"));
        ImageButton imageButton = (ImageButton) findViewById(R.id.locateBtn);
        imageButton.setVisibility(View.GONE);
        initZoom();
        
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {			
			@Override
			public void onMapLoaded() { // LatLngBounds��Ҫ�ȴ���ͼ�����������Ч
		        mLatLng = new LatLng(mGrids.get(0).getLatitude(), mGrids.get(0).getLongitude());
		        ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
				latLngList.add(mLatLng);
				drawMarker(latLngList); // ��ǰPOIλ��
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mLatLng, 16);
				mBaiduMap.setMapStatus(mapStatusUpdate);		
			}
		});        

        // ViewPager
        mAdapter = new GalleryAdapter(getSupportFragmentManager(), mGrids);
        mViewPager = (GalleryViewPager) findViewById(R.id.view_pager);
        mViewPager.setPageMargin(10); // ����ҳ����
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int index) {
				mBaiduMap.clear();
				mLatLng = new LatLng(mGrids.get(index).getLatitude(), mGrids.get(index).getLongitude());
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mLatLng, 16);
				mBaiduMap.setMapStatus(mapStatusUpdate);
				ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
				latLngList.add(mLatLng);
				drawMarker(latLngList);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});
    }
 
    public void initTitleBar(String keywords) {
		ImageView backImageView = (ImageView) findViewById(R.id.title_bar_left_icon);
		backImageView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView title = (TextView) findViewById(R.id.title_bar_center_text);
		title.setText(keywords);
		Button listButton = (Button) findViewById(R.id.title_bar_right_button);
		listButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    
	// ��ʼ�����Ű�ť
    public void initZoom() {
    	mMapView.showZoomControls(false);
		mZoomIn = (ImageButton) findViewById(R.id.zoominBtn);
		mZoomOut = (ImageButton) findViewById(R.id.zoomoutBtn);
		
		if (mBaiduMap.getMapStatus().zoom == mBaiduMap.getMaxZoomLevel())
			mZoomIn.setEnabled(false);
		if (mBaiduMap.getMapStatus().zoom == mBaiduMap.getMinZoomLevel())
			mZoomOut.setEnabled(false);
		
        mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {			
			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
			}
			
			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				Log.v(TAG, "zoom"+mBaiduMap.getMapStatus().zoom);
				if (mBaiduMap.getMapStatus().zoom == mBaiduMap.getMaxZoomLevel())
					mZoomIn.setEnabled(false);
				if (mBaiduMap.getMapStatus().zoom == mBaiduMap.getMinZoomLevel())
					mZoomOut.setEnabled(false);
				if (mBaiduMap.getMapStatus().zoom > mBaiduMap.getMinZoomLevel()
						&& mBaiduMap.getMapStatus().zoom < mBaiduMap.getMaxZoomLevel()) {
					mZoomIn.setEnabled(true);
					mZoomOut.setEnabled(true);
				}					
			}
			
			@Override
			public void onMapStatusChange(MapStatus arg0) {				
			}
		});    	

		mZoomIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float currentLevel = mBaiduMap.getMapStatus().zoom;
				if (currentLevel < mBaiduMap.getMaxZoomLevel()) {
					MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomIn(); // �Ŵ�
					mBaiduMap.setMapStatus(mapStatusUpdate); // mBaiduMap.getMapStatus().zoomֵ��1
					if (mBaiduMap.getMapStatus().zoom == mBaiduMap.getMaxZoomLevel())
						mZoomIn.setEnabled(false);
				}
				if (!mZoomOut.isEnabled())
					mZoomOut.setEnabled(true);
			}
		});
		
		mZoomOut.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				float currentLevel = mBaiduMap.getMapStatus().zoom;
				if (currentLevel > mBaiduMap.getMinZoomLevel()) {
					MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomOut(); // ��С
					mBaiduMap.setMapStatus(mapStatusUpdate); // mBaiduMap.getMapStatus().zoomֵ��1
					if (mBaiduMap.getMapStatus().zoom == mBaiduMap.getMinZoomLevel())
						mZoomOut.setEnabled(false);
				}
				if (!mZoomIn.isEnabled())
					mZoomIn.setEnabled(true);
			}
		});		
	} 
    
/*    public class DataTask extends AsyncTask<Void, Void, Void> {
    	
		private int index;

		public DataTask(int index) {
			this.index = index;
		}
    	
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(PoiMapActivity.this, "", "������...");
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				drawGridShape(index);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
				mProgressDialog.dismiss();			
		}
	}*/
    
    public void drawMarker(ArrayList<LatLng> latLngs) {
    	int count = latLngs.size();
    	if (count == 1) {
    		bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
    		OverlayOptions markerOptions = new MarkerOptions().position(latLngs.get(0)).icon(bitmapDescriptor);
    		mBaiduMap.addOverlay(markerOptions);
    	}
/*    	if (count > 1) {
        	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱrecycle
        	bitmapDescriptors = new ArrayList<BitmapDescriptor>();
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark1));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark2));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark3));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark4));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark5));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark6));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark7));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark8));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark9));
        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark10));
			for (int i = 0; i < count; i++) {
				OverlayOptions markerOptions = new MarkerOptions()
						.position(latLngs.get(i))
						.icon(bitmapDescriptors.get(i)).zIndex(i)
						.title(mResourceList.get(i).getResourceName())
						.perspective(true);
				mBaiduMap.addOverlay(markerOptions);
			}
			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					marker.setToTop();
					Toast.makeText(PoiMapActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
					return false;
				}
			});
    	} */
	}
    
    public void drawPolygon(ArrayList<LatLng> latLngs) {
		OverlayOptions ooPolygon = new PolygonOptions().points(latLngs).stroke(new Stroke(2, 0xAA00FF00)).fillColor(0x99FFFF00);
		mBaiduMap.addOverlay(ooPolygon);
	}
    
    @Override
    protected void onResume() {
        super.onResume();
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
        mMapView.onPause();
    }

	@Override
	public void finish(){
	    super.finish();
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
        mMapView.onDestroy();
        if (bitmapDescriptor != null)
        	bitmapDescriptor.recycle();
        if (bitmapDescriptors != null) {
            for (int i=0; i<bitmapDescriptors.size(); i++) {
            	bitmapDescriptors.get(i).recycle();
            }
        }
    }
}
