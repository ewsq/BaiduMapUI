package com.ctc.gis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

public class GridFragment extends Fragment {
	
//	private String number;
	private Grid mGrid;
	
    private TextView mNumber;
    private TextView mPoiName;
    private TextView mAddress;
    private TextView mDistance;
    private Button mDetail;
    private Button mBtnLeft;
    private Button mBtnCenter;
    private Button mBtnRight;
    
    PoiMapActivity activity;
    MapView mMapView;
    BaiduMap mBaiduMap;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        Log.v(this.toString(), "onCreate()");
        activity = (PoiMapActivity)getActivity();
//        mMapView = (MapView) activity.findViewById(R.id.bmapView);
//		mBaiduMap = mMapView.getMap();
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
        View view =inflater.inflate(R.layout.fragment_item, container, false);
        mNumber = (TextView) view.findViewById(R.id.number);
        mPoiName = (TextView) view.findViewById(R.id.poiname);
        mAddress = (TextView) view.findViewById(R.id.address);
        mDistance = (TextView) view.findViewById(R.id.distance);        
        mDetail = (Button) view.findViewById(R.id.btn_detail);
        mBtnLeft = (Button) view.findViewById(R.id.btn_acceleration);
        mBtnCenter = (Button) view.findViewById(R.id.btn_renewal);
        mBtnRight = (Button) view.findViewById(R.id.btn_resources);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			mGrid = bundle.getParcelable("grid");
			mNumber.setText(bundle.getString("number"));
			mPoiName.setText(mGrid.getPoiName());
			mAddress.setText(mGrid.getAddress());
			mDistance.setText(mGrid.getDistance());
			mBtnLeft.setSelected(false);
			mBtnCenter.setSelected(false);
			mBtnRight.setSelected(false);
		}
        Log.v(this.toString(), "onCreateView() ");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(this.toString(), "onActivityCreated()");

        mDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
        mBtnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(activity, "请自行开发", Toast.LENGTH_SHORT).show();
			}
		});
        mBtnCenter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(activity, "请自行开发", Toast.LENGTH_SHORT).show();
			}
		});
        mBtnRight.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				Toast.makeText(activity, "请自行开发", Toast.LENGTH_SHORT).show();
			}
		});
    }
	
}
