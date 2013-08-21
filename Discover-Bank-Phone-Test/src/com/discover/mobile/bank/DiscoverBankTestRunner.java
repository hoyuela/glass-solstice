package com.discover.mobile.bank;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

import com.discover.mobile.bank.deposit.test.MCDUtilsTest;

public class DiscoverBankTestRunner extends InstrumentationTestRunner {

	@Override
	public TestSuite getAllTests() {
		final InstrumentationTestSuite suite = new InstrumentationTestSuite(this);

		suite.addTestSuite(MCDUtilsTest.class);
		return suite;
	}
}
