package com.simpleentity.serialize2.meta;

import static org.junit.Assert.assertTrue;

public class MetaDataTestUtil {

	public static void assertDeepEquals(MetaData expected, MetaData actual) {
		assertTrue("Expected "+expected+" but was: "+actual, MetaDataUtil.deepEquals(expected, actual));
	}

	private MetaDataTestUtil() {}

}
