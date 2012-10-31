package com.discover.rdc.camera.plugin.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.discoverfinancial.mobile.R;

import android.content.Context;

public class DeviceParser{
	
	private XmlPullParser xpp;
	
	public DeviceParser(Context context){
		xpp = context.getResources().getXml(R.xml.devices);
	}
	
	public List<Device> getDevicesToReleaseAllCameraResources() throws XmlPullParserException, IOException {
		List<Device> listDevices = new ArrayList<Device>(0);
		
		int eventType = xpp.getEventType();
		Device device = null;
		
		while(eventType != XmlPullParser.END_DOCUMENT) {
			switch(eventType) 
			{
				case XmlPullParser.START_DOCUMENT:
					break;
					
				case XmlPullParser.START_TAG:
					//get tag name
					String tagName = xpp.getName();
					
					//if<device>
					if(Device.DEVICE_TAG_NAME.equals(tagName)){
						device = new Device();
					}
					//if<brand>
					else if (Device.BRAND_TAG_NAME.equals(tagName)){
						device.setBrand(xpp.nextText());
					}
					//if<sdk_level>
					else if(Device.SDK_LEVEL_TAG_NAME.equals(tagName)){
						device.setSdkLevel(xpp.nextText());
					}
					//if<model>
					else if(Device.MODEL_TAG_NAME.equals(tagName)){
						device.setModel(xpp.nextText());
					}
					break;
					
				case XmlPullParser.END_TAG:
					//get tag name
					tagName = xpp.getName();
					//if</device>
					if(Device.DEVICE_TAG_NAME.equals(tagName) && device != null){
						listDevices.add(device);
					}
					break;
			}
			
			//jump to next event
			eventType = xpp.next();
		}
		return listDevices;
	}
}
