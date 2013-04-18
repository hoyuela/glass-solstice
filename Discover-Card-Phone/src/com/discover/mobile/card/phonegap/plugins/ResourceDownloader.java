/**
 * 
 */
package com.discover.mobile.card.phonegap.plugins;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import android.util.Log;

/**
 * @author jcoyne
 * @version 1.0
 *  
 *  This class serves as a broker to download a File via HTTP/S 
 *  and save it to a given location on an Android device.
 *  
 */
public class ResourceDownloader {

	private static final String LOG_TAG = "ResourceDownloader";
	private static final int connectionTimeout  = 10000;
	private static final int DEFAULT_STATUS_CODE  = 500;
	private static final String DEFAULT_FILE_NAME = "default";
	private static final String HDR_CNT_TYP = "content-type";
	private static final String HDR_CNT_DSP = "content-disposition";
	private static final String FILE_NAME = "filename=";
	private static final String HTTPS = "https";
	private static final String TLS = "TLS";
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String FS = "/";
    
	/**
	 * Singleton pattern
	 */
	private static ResourceDownloader instance = new ResourceDownloader();
	
	public static ResourceDownloader getInstance(){
		return instance;
	}
	
	/**
	 * private Constructor for Singleton
	 */
	private ResourceDownloader(){}
		
	/**
	 * This method will download a file from the Web and Save it in the directory specified, 
	 *	as well as return a handle to the file.
	 * 
	 * @param urlStr - URL where resource can be downloaded from
	 * @param method - HTTP Method: GET or POST
	 * @param inputContent - Only applicable for POST
	 * @param headers - HTTP Headers
	 * @param directory - Full Directory Name to where the File should be saved
	 * @param fileExt - File extension, eg: .pdf
	 * @param contentType - The Content Type - if not in the response, exception is thrown
	 * 
	 * @return
	 * @throws Exception
	 */
	public File executeDownload(String urlStr, String method, String inputContent, HashMap<String,String> headers, String directory, String fileExt, String contentType) throws Exception{
		HttpURLConnection connection = null; 
		int statusCode = DEFAULT_STATUS_CODE;
		File theFile = null;
		try {
			boolean isSecure = urlStr.indexOf(HTTPS) >= 0;
			URL url = new URL(urlStr);
			if(isSecure){
				connection = (HttpsURLConnection)(url.openConnection());
				SSLContext context = SSLContext.getInstance(TLS);
				context.init(null, null ,null);
				SSLSocketFactory sockFactory = context.getSocketFactory();
				((HttpsURLConnection) connection).setSSLSocketFactory(sockFactory);
			}else{
				connection = (HttpURLConnection)(url.openConnection());
			}
			connection.setConnectTimeout(connectionTimeout);
			connection.setRequestMethod(method);
			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(inputContent!=null);
			
			// Set the HTTP Headers, if any were passed in
			if(headers!=null){
				for(String headerName : headers.keySet()){
					connection.setRequestProperty(headerName, headers.get(headerName));
				}
			}

			Log.d(LOG_TAG, "Sending "+ method+ " to "+urlStr );

			if(GET.equals(method)){
				connection.connect();
			}else{
				OutputStream outStream = null;
				try {
					if(inputContent!=null){
						outStream = connection.getOutputStream();
						Log.d(LOG_TAG, "Setting inputContent: "+inputContent );
						outStream.write(inputContent.getBytes());
					}
				} finally {
					if (outStream != null) { outStream.close(); }
				}
			}
			
			statusCode = connection.getResponseCode();
			String respMsg = connection.getResponseMessage();
			Log.d(LOG_TAG, "HTTP Response: "+statusCode+ " " +respMsg );
			
			if(statusCode != 200){
				throw new Exception("Bad Return. HTTP Response: "+statusCode+ " " +respMsg);
			}
						
			// Check to see if a filename was given in the response 
			//  eg Content-Disposition: attachment; filename=Discover-Statement-20120520.pdf
			boolean contentTypeFound = false;
			String respFileName = null;
			if(connection.getHeaderFields()!=null){
				for(String header: connection.getHeaderFields().keySet()){
					if(HDR_CNT_DSP.equalsIgnoreCase(header)){
						for(String headerVal: connection.getHeaderFields().get(header)){
							int fnIdx = headerVal.indexOf(FILE_NAME);
							if(fnIdx >= 0){
								respFileName = headerVal.substring( fnIdx+ FILE_NAME.length() );
							}
						}
					}else if(HDR_CNT_TYP.equalsIgnoreCase(header)){
						for(String headerVal: connection.getHeaderFields().get(header)){
							Log.d(LOG_TAG, HDR_CNT_TYP+"="+headerVal );
							if(headerVal.indexOf(contentType) >=0){
								contentTypeFound = true;
							}
						}
						if(!contentTypeFound){
							throw new Exception("Content Type:"+contentType+" was not found in the response" );
						}
					}
				}
			}
			
			StringBuilder buf = new StringBuilder(directory);
			buf.append(FS);
			if(respFileName != null){
				buf.append(respFileName);
			}else{
				// file name was not in the response header
        		if( urlStr.indexOf(fileExt) > 0 ){
        			buf.append(urlStr.substring( (urlStr.lastIndexOf(FS)+1) , urlStr.indexOf(fileExt) ));
        		}else{
        			// File extension does not appear in the URL so lets just make the name up.
        			buf.append(DEFAULT_FILE_NAME);
        		}
        		buf.append(fileExt);
			}
			Log.d(LOG_TAG, "About to write file: "+buf.toString() );
			theFile = new File( buf.toString() );
			try{
				InputStream stream = connection.getInputStream();
				BufferedInputStream in = new BufferedInputStream(stream,8192);
				FileOutputStream file = new FileOutputStream( theFile );
				BufferedOutputStream out = new BufferedOutputStream(file,8192);
				int i;
				while ((i = in.read()) != -1) {
				    out.write(i);
				}
				out.flush();
			}catch(Exception e){
				throw new Exception("Exception caught trying to fetch the response content: " + e.getMessage());
			}
		} catch(Exception e){
			throw new Exception("Exception caught sending request: " + e.getMessage());
		}finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		return theFile;
	}
}
