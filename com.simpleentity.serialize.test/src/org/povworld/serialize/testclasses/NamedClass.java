/*
 * Created on Dec 2, 2007
 *
 */
package org.povworld.serialize.testclasses;

import junit.framework.AssertionFailedError;


public class NamedClass {

	private String code = "312";

	public Object anonymous = new Object() {
		int f = 17;
		String name = "anonymous inner thingy";

		private String getCode() {
			return code;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !o.getClass().equals(getClass()))
				return false;
			try {
				if (getClass().getDeclaredField("f").getInt(o) != f)
					return false;
				if (!getClass().getDeclaredField("name").get(o).equals(name))
					return false;
				if (!getClass().getDeclaredMethod("getCode").invoke(o).equals(getCode()))
					return false;
			} catch (Throwable t) {
				throw new AssertionFailedError(t.getMessage());
			}
			return true;
		}

		@Override
		public int hashCode() {
			return f;
		}

	};

}