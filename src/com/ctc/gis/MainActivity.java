package com.ctc.gis;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;

public class MainActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";
	
    MapView mMapView = null;
    BaiduMap mBaiduMap;
	// ��λ
    private LocationClient locationClient;
    private MyLocationListenner myListener;
	boolean isFirstLoc = true; // �Ƿ��״ζ�λ
	private ImageButton mLocationButton;
    // ViewPager
    private GalleryViewPager mViewPager;
    private GalleryAdapter mAdapter;    
    // POI����
    private AutoCompleteTextView mKeywords = null;
    private ImageButton mSearchButton = null;
    private SuggestionSearch mSuggestionSearch = null;
    private ArrayAdapter<String> sugAdapter = null;
    private String mCity = "����";
    // ����
	private double mCurrentLatitude; // ��ǰγ��
	private double mCurrentLongitude; // ��ǰ����
	private Grid mGrid = new Grid(); 
	
	// ����
	private ImageButton mZoomIn;
	private ImageButton mZoomOut;
	
	private BitmapDescriptor bitmapDescriptor;
	private List<BitmapDescriptor> bitmapDescriptors;
	
	/**
	 * ��λSDK������
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
				mBaiduMap.setMapStatus(mapStatusUpdate);
				if(location.getCity() != null){
					mCity = location.getCity();
				}
				locationClient.stop();
				Log.v(TAG, "location stop");
			}
			if(location.hasAddr())	
				mGrid.setAddress(location.getAddrStr());
			mGrid.setPoiName("�ҵ�λ��");
			mCurrentLatitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			mGrid.setLatitude(mCurrentLatitude);
			mGrid.setLongitude(mCurrentLongitude);
			mGrid.setDistance("0��");
			mAdapter.notifyDataSetChanged();
			mBaiduMap.clear();
			ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
			latLngs.add(new LatLng(mCurrentLatitude, mCurrentLongitude));
			drawMarker(latLngs);
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	/**
	 * viewpager������
	 */
    class GalleryAdapter extends FragmentStatePagerAdapter {

        private int mCount = 1; // Ĭ��1
        private FragmentManager fm;

        public GalleryAdapter(FragmentManager fm) {        	
            super(fm);
            this.fm = fm;
        }
        
        public GalleryAdapter(FragmentManager fm, int count) {
            super(fm);
            this.mCount = count;
            this.fm = fm;
        }              

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ItemFragment itemFragment = (ItemFragment) super.instantiateItem(container, (position % mCount));
			itemFragment.setNumber("");
			itemFragment.setGrid(mGrid);
			return super.instantiateItem(container, (position % mCount));
		}

		@Override
        public Fragment getItem(int position) {
        	return new ItemFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
        
        @Override
        public int getCount() {
            return mCount;
        }
    }	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
        // ע��÷���Ҫ��setContentView����֮ǰʵ��
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);            
        // ��ȡ��ͼ�ؼ�����
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();        
        // ��λ��ʼ��
        initLocation();       
        // ViewPager
        mAdapter = new GalleryAdapter(getSupportFragmentManager());
        mViewPager = (GalleryViewPager) findViewById(R.id.view_pager);
        mViewPager.setPageMargin(10);//����ҳ����   
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        
        initMapListener(); // ������ͼ���    
        initZoom(); // ��ʼ�����Ű�ť
        initSearchBox(); // ��ʼ��������
    }
    
	// ��λ��ʼ��
    public void initLocation() {
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		locationClient = new LocationClient(this);
		myListener = new MyLocationListenner();
		locationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
//		option.setScanSpan(900);
		option.setIsNeedAddress(true);
		locationClient.setLocOption(option);
		locationClient.start();
		// ������λ��ť���
		mLocationButton = (ImageButton) findViewById(R.id.locateBtn);
		mLocationButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (locationClient.isStarted()) {
					locationClient.stop();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.v(TAG, "location still start");
				}
				isFirstLoc = true;
				locationClient.start();
				Log.v(TAG, "location start again");
			}
		});
	}
    
	// ������ͼ���
    public void initMapListener() {
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() { 
			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				LatLng latLng = mapPoi.getPosition();
				LatLng currentLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
				String distance = String.valueOf((int)DistanceUtil.getDistance(currentLatLng, latLng))+"��";// ����POI����ǰλ�õľ���							
				mGrid.setPoiName(mapPoi.getName());
				mGrid.setAddress("�ٶȵ�ͼ�鲻���õ�ַ");
				Log.v(TAG, "mappoi: "+mapPoi.toString());
				mGrid.setDistance(distance);
				mGrid.setLatitude(latLng.latitude);
				mGrid.setLongitude(latLng.longitude);
				mAdapter.notifyDataSetChanged();
				mBaiduMap.clear();
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
				mBaiduMap.setMapStatus(mapStatusUpdate);	
				ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
				latLngs.add(latLng);
				drawMarker(latLngs);
				return true;
			}
			
			@Override
			public void onMapClick(LatLng latLng) {

			}
		});        
        
        mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {			
			@Override
			public void onMapLongClick(LatLng latLng) {
				LatLng currentLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
				String distance = String.valueOf((int)DistanceUtil.getDistance(currentLatLng, latLng))+"��";// ����˫���㵽��ǰλ�õľ���
				mGrid.setDistance(distance);
				mGrid.setPoiName("ѡ�еص�");
				mGrid.setAddress("�ٶȵ�ͼ�鲻���õ�ַ");
				mGrid.setLatitude(latLng.latitude);
				mGrid.setLongitude(latLng.longitude);
				mAdapter.notifyDataSetChanged();
				mBaiduMap.clear();
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
				mBaiduMap.setMapStatus(mapStatusUpdate);
				ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
				latLngs.add(latLng);
				drawMarker(latLngs);
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
    
	// ��ʼ��������
    public void initSearchBox() {
        mKeywords = (AutoCompleteTextView) findViewById(R.id.keywords);
		sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		mKeywords.setAdapter(sugAdapter);
		mKeywords.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				if (charSequence.length() <= 0) {
					return;
				}				
				// ʹ�ý������������ȡ�����б������onSuggestionResult()�и���				
				mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(charSequence.toString()).city(mCity));				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {				
			}
			
			@Override
			public void afterTextChanged(Editable s) {				
			}
		});
		
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {			
			@Override
			public void onGetSuggestionResult(SuggestionResult suggestionResult) {
				if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
					return;
				}
				sugAdapter.clear();
				for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
					if (info.key != null)
						sugAdapter.add(info.key);
				}
				sugAdapter.notifyDataSetChanged();				
			}
		});
		// ���������ť����¼�
        mSearchButton = (ImageButton) findViewById(R.id.imgbtn_search);
        mSearchButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String keywords = mKeywords.getText().toString().trim();
				if(!TextUtils.isEmpty(keywords)) {
					Intent intent = new Intent(MainActivity.this, ListInfoActivity.class);
					intent.putExtra("keywords", keywords); // ������
					intent.putExtra("city", mCity); // ��ǰ����
					intent.putExtra("latitude", mCurrentLatitude);
					intent.putExtra("longitude", mCurrentLongitude);
					startActivity(intent); // POI����								
				}
			}
		});
	}
    
    public void drawMarker(ArrayList<LatLng> latLngs) {
    	int count = latLngs.size();
    	if (count == 1) {
    		bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
    		OverlayOptions markerOptions = new MarkerOptions().position(latLngs.get(0)).icon(bitmapDescriptor);
    		mBaiduMap.addOverlay(markerOptions);
    	}
//    	if (count > 1) {
//        	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
//        	bitmapDescriptors = new ArrayList<BitmapDescriptor>();
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark1));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark2));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark3));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark4));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark5));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark6));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark7));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark8));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark9));
//        	bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark10));
//			for (int i = 0; i < count; i++) {
//				OverlayOptions markerOptions = new MarkerOptions()
//						.position(latLngs.get(i))
//						.icon(bitmapDescriptors.get(i)).zIndex(i)
//						.title(mResourceList.get(i).getResourceName())
//						.perspective(true);
//				mBaiduMap.addOverlay(markerOptions);
//			}
//			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//				@Override
//				public boolean onMarkerClick(Marker marker) {
//					marker.setToTop();
//					Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
//					return true;
//				}
//			});
//    	} 
	}
    
    public void drawPolygon(ArrayList<LatLng> latLngs) {
		OverlayOptions ooPolygon = new PolygonOptions().points(latLngs).stroke(new Stroke(2, 0xAA00FF00)).fillColor(0x99FFFF00);
		mBaiduMap.addOverlay(ooPolygon);
	}
    
    public void drawCircle(LatLng latLng, int radius) {
		OverlayOptions ooCircle = new CircleOptions().center(latLng)
				.fillColor(0x33149AFD)
				.radius(radius);
		Circle circle = (Circle) mBaiduMap.addOverlay(ooCircle);
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
    protected void onDestroy() {
        super.onDestroy();
		// �˳�ʱ���ٶ�λ
		locationClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
        mMapView.onDestroy();
        mSuggestionSearch.destroy();
        if (bitmapDescriptor != null)
        	bitmapDescriptor.recycle();
        if (bitmapDescriptors != null) {
            for (int i=0; i<bitmapDescriptors.size(); i++) {
            	bitmapDescriptors.get(i).recycle();
            }
        }
    }
}
