package com.discover.rdc.camera.plugin.utils;

public final class CameraUtils {
	
	
	public static final int ERROR_ENCODE_FRONT = 0;
	public static final int ERROR_ENCODE_BACK = 1;

	
	private CameraUtils()
	{
		
	}
	
	public static String getJSImageInjectCode(final String sourceImage, final String elementId)
	{
		final StringBuilder str = new StringBuilder("javascript:");
		str.append("var front = document.getElementById('");
		str.append(elementId);
		str.append("');");
		str.append("front.setAttribute('src','data:image/png;base64,");
		str.append(sourceImage);
		str.append("');");
		
		return str.toString();
	}
	
	public static String getJSEncodeError(final int sectionOfError, final String errorMessage)
	{
		final StringBuilder str = new StringBuilder("javascript:");
		
		switch (sectionOfError)
		{
			case ERROR_ENCODE_FRONT:
				str.append("$('.check-capture-front').append('<p class=\"capture-error error\">" + errorMessage +"</p>');");
				str.append("$('.check-capture-front-image').addClass('error-on-image');");
  				break;

			case ERROR_ENCODE_BACK:
				str.append("$('.check-capture-back').append('<p class=\"capture-error error\">" + errorMessage +"</p>');");
				str.append("$('.check-capture-back-image').addClass('error-on-image');");
  				break;
		}
		
		return str.toString();
	}
	
	public static String getJSToRevemoEncodeError(final int sectionOfError)
	{
		final StringBuilder str = new StringBuilder("javascript:");
		
		switch (sectionOfError)
		{
			case ERROR_ENCODE_FRONT:
				str.append("$('.check-capture-front').children('p.capture-error').remove();");
				str.append("$('.check-capture-front-image').removeClass('error-on-image');");
				break;
			case ERROR_ENCODE_BACK:
				str.append("$('.check-capture-back').children('p.capture-error').remove();");
				str.append("$('.check-capture-back-image').removeClass('error-on-image');");
				break;
		}
		
		return str.toString();
	}
}



