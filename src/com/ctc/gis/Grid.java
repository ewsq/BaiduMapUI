package com.ctc.gis;

import android.os.Parcel;
import android.os.Parcelable;

public class Grid implements Parcelable {
	
	private String poiName;
	private String address;
	private String distance;
	private double latitude;
	private double longitude;

	public Grid() {
		this.poiName = "δ֪";
		this.address = "δ֪��ַ";
		this.distance = "";
		this.latitude = 0;
		this.longitude = 0;
	}
	
	public Grid(Parcel source) {
		poiName = source.readString();
		address = source.readString();
		distance = source.readString();
		latitude = source.readDouble();
		longitude = source.readDouble();
	}
	
	public static final Parcelable.Creator<Grid> CREATOR = new Creator<Grid>() {
		@Override
		public Grid createFromParcel(Parcel source) {
			 return new Grid(source);
		}

		@Override
		public Grid[] newArray(int size) {
			return new Grid[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(poiName);
		dest.writeString(address);
		dest.writeString(distance);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}

	public String getPoiName() {
		return poiName;
	}

	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Grid [poiName=" + poiName + ", address=" + address
				+ ", distance=" + distance + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}

}
