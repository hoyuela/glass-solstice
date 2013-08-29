package com.discover.mobile.card.fastcheck;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;

/**
 * CardArtHelper. Main purpose of this class is to set CardArt Image in cache.
 * 
 * @author 352686
 * 
 */
public class CardArtHelper {

	private Context context;
	private final String PNG_EXTN = ".png";
	private final String UNDER_SOCRE = "_";
	private final String DEFAULT_CARD_PREFIX = "CARD_IMAGE_";

	private CardArtHelperCallback cardArtHelperCallback;

	public interface CardArtHelperCallback {
		void onSuccess();
	}

	public CardArtHelper(Context context,
			CardArtHelperCallback cardArtHelperCallback) {
		this.context = context;
		this.cardArtHelperCallback = cardArtHelperCallback;
	}

	/**
	 * Get default card image name from card_art_mapping.xml file. If desire
	 * name is not present then recursively calls till we get image name.
	 * 
	 * @param name
	 * @return
	 */
	public String getImageName(final String name) {
		int id = context.getResources().getIdentifier(name, "string",
				context.getPackageName());
		return id == 0 ? getImageName(name.substring(0, name.lastIndexOf("_")))
				: context.getResources().getString(id);

	}

	/**
	 * Calls from Quick view Fragment to get latest image from server.
	 * 
	 * @param imageName
	 * @param incentiveTypeCode
	 * @param incentiveCode
	 */
	public void updateCardImage(final String imageName,
			final String incentiveTypeCode, final String incentiveCode) {

		if (imageName != null) {

			if (!imageName.equalsIgnoreCase(getImageNamefromCache())) {

				String baseURL = context.getString(R.string.url_in_use);
				StringBuffer imageUrlBuffer = new StringBuffer();
				imageUrlBuffer.append(baseURL);
				imageUrlBuffer.append(context
						.getString(R.string.cardart_base_url));
				imageUrlBuffer.append(getDensityUrl());
				imageUrlBuffer.append(imageName);
				imageUrlBuffer.append(PNG_EXTN);

				final String imageUrl = imageUrlBuffer.toString();

				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						Utils.isSpinnerAllowed = false;
					}

					@Override
					protected Void doInBackground(Void... params) {

						Bitmap cartImage = downlodImageFromServer(imageUrl);
						if (cartImage != null) {
							addImageTocache(imageName, cartImage);
						} else {
							setDefaultImage(incentiveTypeCode, incentiveCode);
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						cardArtHelperCallback.onSuccess();
					};

				}.execute();
			} else {
				cardArtHelperCallback.onSuccess();
			}
		} else {
			setDefaultImage(incentiveTypeCode, incentiveCode);
			cardArtHelperCallback.onSuccess();
		}
	}

	/**
	 * Set default image to cache
	 * 
	 * @param incentiveTypeCode
	 * @param incentiveCode
	 */
	private void setDefaultImage(final String incentiveTypeCode,
			final String incentiveCode) {
		StringBuffer imageNameBuffer = new StringBuffer();
		imageNameBuffer.append(DEFAULT_CARD_PREFIX);
		if (incentiveTypeCode != null && !incentiveTypeCode.isEmpty()) {
			imageNameBuffer.append(incentiveTypeCode);
			imageNameBuffer.append(UNDER_SOCRE);
		}
		if (incentiveCode != null && !incentiveCode.isEmpty())
			imageNameBuffer.append(incentiveCode);

		final String card_art_image_name = imageNameBuffer.toString();
		if (!card_art_image_name.equalsIgnoreCase(getImageNamefromCache())) {
			String drawableName = getImageName(card_art_image_name);
			Bitmap card_art_image = BitmapFactory.decodeResource(
					context.getResources(),
					context.getResources().getIdentifier(drawableName,
							"drawable", context.getPackageName()));

			addImageTocache(card_art_image_name, card_art_image);
		}

	}

	/**
	 * add new image in to card cache data store.
	 * 
	 * @param card_art_image_name
	 * @param card_art_image
	 */
	public void addImageTocache(final String card_art_image_name,
			final Bitmap card_art_image) {
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
				.getInstance(context);
		cardShareDataStoreObj.addToAppCache(
				context.getResources().getString(
						R.string.fast_check_cardArt_name), card_art_image_name);
		cardShareDataStoreObj.addToAppCache(
				context.getResources().getString(
						R.string.fast_check_cardArt_bitmap), card_art_image);
	}

	/**
	 * Get latest save image name from cache
	 * 
	 * @return ImageName
	 */
	public String getImageNamefromCache() {
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
				.getInstance(context);
		Object name = cardShareDataStoreObj.getValueOfAppCache(context
				.getResources().getString(R.string.fast_check_cardArt_name));
		return name == null ? "" : name.toString();

	}

	/**
	 * Get latest save image bitmap from cache
	 * 
	 * @return saved Bitmap
	 */
	public Bitmap getCardArtFromCache() {
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
				.getInstance(context);
		Object name = cardShareDataStoreObj.getValueOfAppCache(context
				.getResources().getString(R.string.fast_check_cardArt_bitmap));
		return (Bitmap) (name == null ? BitmapFactory.decodeResource(
				context.getResources(), R.drawable.card_img) : name);
	}

	/***
	 * Connects server with URLConnection
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	private InputStream OpenHttpConnection(final String urlString)
			throws IOException {
		InputStream in = null;
		URL url = new URL(urlString);

		try {
			URLConnection urlConnection = url.openConnection();
			in = urlConnection.getInputStream();
		} catch (Exception ex) {
			throw new IOException("Error connecting");
		}
		return in;
	}

	/**
	 * Get new Image from server
	 * 
	 * @param URL
	 * @return downloaded Bitmap
	 */
	private Bitmap downlodImageFromServer(String URL) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in);
		} catch (IOException e1) {
		}

		return bitmap;
	}

	/**
	 * According to screen density type image url is created server call.
	 * 
	 * @return Image url
	 */
	private String getDensityUrl() {
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			return context.getString(R.string.card_ldpi_url);

		case DisplayMetrics.DENSITY_MEDIUM:
			return context.getString(R.string.card_mdpi_url);

		case DisplayMetrics.DENSITY_HIGH:
			return context.getString(R.string.card_hdpi_url);

		case DisplayMetrics.DENSITY_XHIGH:
			return context.getString(R.string.card_xhdpi_url);
		default:
			return context.getString(R.string.card_xhdpi_url);

		}
	}
}
