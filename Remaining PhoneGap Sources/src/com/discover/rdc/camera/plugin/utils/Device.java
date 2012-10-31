package com.discover.rdc.camera.plugin.utils;

public class Device{
	
	private String brand;
	private String sdkLevel;
	private String model;
	
	public static final String BRAND_TAG_NAME = "brand";
	public static final String SDK_LEVEL_TAG_NAME = "sdk_level";
	public static final String MODEL_TAG_NAME = "model";
	public static final String DEVICE_TAG_NAME = "device";

	public String getBrand(){
		return brand;
	}
	
	public void setBrand(String brand){
		this.brand = brand;
	}
	
	public String getSdkLevel(){
		return sdkLevel;
	}
	
	public void setSdkLevel(String sdkLevel){
		this.sdkLevel = sdkLevel;
	}
	
	public String getModel(){
		return model;
	}
	
	public void setModel(String model){
		this.model = model;
	}
}
