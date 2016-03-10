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

public class ItemFragment extends Fragment {
	
	private String number;
	private Grid grid;
	
    private TextView mNumber;
    private TextView mLocation;
    private TextView mAddress;
    private TextView mDistance;
    Button mDetail;
    Button mBtnLeft;
    Button mBtnCenter;
    Button mBtnRight;
    
    MainActivity mainActivity;
    MapView mMapView;
    BaiduMap mBaiduMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        Log.v(this.toString(), "onCreate()");
		mainActivity = (MainActivity)getActivity();
//		mMapView = (MapView) mainActivity.findViewById(R.id.bmapView);
//		mBaiduMap = mMapView.getMap();
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_item, container, false);
        mNumber = (TextView) view.findViewById(R.id.number);
        mLocation = (TextView) view.findViewById(R.id.poiname);
        mAddress = (TextView) view.findViewById(R.id.address);
        mDistance = (TextView) view.findViewById(R.id.distance);
        mDetail = (Button) view.findViewById(R.id.btn_detail);
        mBtnLeft = (Button) view.findViewById(R.id.btn_acceleration);
        mBtnCenter = (Button) view.findViewById(R.id.btn_renewal);
        mBtnRight = (Button) view.findViewById(R.id.btn_resources);
        
        if (grid != null) {
        	mNumber.setText(number);
			mLocation.setText(grid.getPoiName());
			mAddress.setText(grid.getAddress());
			mDistance.setText(grid.getDistance());
        }
		mBtnLeft.setSelected(false);
		mBtnCenter.setSelected(false);
		mBtnRight.setSelected(false);

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
//				Intent intent = new Intent(mainActivity, GridMsgMainActivity.class);
//				intent.putExtra("gridId", grid.getGridID());
//				intent.putExtra("judge", "QGridMsg");
//				intent.putExtra("paramValue", grid.getGridName());
//				startActivity(intent);
			}
		});
        mBtnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mainActivity, "请自行开发", Toast.LENGTH_SHORT).show();
			}
		});
        mBtnCenter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mainActivity, "请自行开发", Toast.LENGTH_SHORT).show();
			}
		});
        mBtnRight.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				Toast.makeText(mainActivity, "请自行开发", Toast.LENGTH_SHORT).show();
			}
		});
    }

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
    
}
