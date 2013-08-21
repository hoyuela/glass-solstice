package com.discover.mobile.bank.deposit.test;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.test.InstrumentationTestCase;

import com.discover.mobile.bank.deposit.MCDUtils;

/**
 * Class created to test the MCDUtils.java
 * @author jthornton
 *
 */
public class MCDUtilsTest extends InstrumentationTestCase {

	/** Camera used to create sizes*/
	private Camera camera;

	/**
	 * Default constructor
	 */
	public MCDUtilsTest() {
		super();
	}

	/**
	 * Set up for all the test cases
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		camera = Camera.open();
	}

	/**
	 * Tear down the test cases
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if(null != camera){
			camera.release();
		}
	}

	/**
	 * Test having a width of 1600
	 */
	public void testGetBestImageSizeExactWidth(){
		final Size one = camera.new Size(1600, 1200);
		final Size two = camera.new Size(1700, 1300);
		final Size three = camera.new Size(600, 200);
		final List<Size> sizes = new ArrayList<Size>();
		sizes.add(one);
		sizes.add(two);
		sizes.add(three);
		final Size result = MCDUtils.getBestImageSize(sizes, 1600);
		assertEquals(one.width, result.width);
		assertEquals(one.height, result.height);
	}

	/**
	 * Test not having a width of 1600 but a lower and higher resolution
	 */
	public void testGetBestImageSizeLowerWidth(){
		final Size one = camera.new Size(1650, 1200);
		final Size two = camera.new Size(1700, 1300);
		final Size three = camera.new Size(600, 200);
		final List<Size> sizes = new ArrayList<Size>();
		sizes.add(one);
		sizes.add(two);
		sizes.add(three);
		final Size result = MCDUtils.getBestImageSize(sizes, 1600);
		assertEquals(one.width, result.width);
		assertEquals(one.height, result.height);
	}

	/**
	 * Test having only one width
	 */
	public void testGetBestImageSizeOneWidth(){
		final Size one = camera.new Size(1650, 1200);
		final List<Size> sizes = new ArrayList<Size>();
		sizes.add(one);
		final Size result = MCDUtils.getBestImageSize(sizes, 1600);
		assertEquals(one.width, result.width);
		assertEquals(one.height, result.height);
	}

	/**
	 * Test having only lower resolutions
	 */
	public void testGetBestImageSizeOnlyLower(){
		final Size one = camera.new Size(800, 600);
		final Size two = camera.new Size(600, 400);
		final List<Size> sizes = new ArrayList<Size>();
		sizes.add(one);
		sizes.add(two);
		final Size result = MCDUtils.getBestImageSize(sizes, 1600);
		assertEquals(one.width, result.width);
		assertEquals(one.height, result.height);
	}

	/**
	 * Test having only higher resolutions
	 */
	public void testGetBestImageSizeOnlyHigher(){
		final Size one = camera.new Size(1800, 1600);
		final Size two = camera.new Size(1600, 1400);
		final List<Size> sizes = new ArrayList<Size>();
		sizes.add(one);
		sizes.add(two);
		final Size result = MCDUtils.getBestImageSize(sizes, 1600);
		assertEquals(two.width, result.width);
		assertEquals(two.height, result.height);
	}

	/**
	 * Test making sure the adjusted height calculation is correct
	 */
	public void testAdjustedImageHeight1(){
		assertEquals(1200, MCDUtils.getAdjustedImageHeight(1200, 1600, 1600));
	}

	/**
	 * Test making sure the adjusted height calculation is correct
	 */
	public void testAdjustedImageHeight2(){
		assertEquals(1200, MCDUtils.getAdjustedImageHeight(1500, 2000, 1600));
	}


}
