/*
 * Created on Oct 6, 2007
 *
 */
package com.simpleentity.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.simpleentity.util.ObjectUtil;

public class ObjectUtilTest {

	@Test
	public void objectEquals() {
		String a = "x";
		String b = "y";
		String c = "x";

		assertTrue(ObjectUtil.equals(null, null));
		assertFalse(ObjectUtil.equals(null, a));
		assertFalse(ObjectUtil.equals(b, null));
		assertTrue(ObjectUtil.equals(a, a));
		assertFalse(ObjectUtil.equals(a, b));
		assertTrue(ObjectUtil.equals(a, c));
		assertFalse(ObjectUtil.equals(b, a));
		assertTrue(ObjectUtil.equals(b, b));
		assertFalse(ObjectUtil.equals(b, c));
		assertTrue(ObjectUtil.equals(c, a));
		assertFalse(ObjectUtil.equals(c, b));
		assertTrue(ObjectUtil.equals(c, c));
	}

	@Test
	public void getSuperTypesOfObject() {
		assertEquals(asSet(Object.class), ObjectUtil.getAllSuperTypes(Object.class));
	}

	@Test
	public void getSuperTypesOfExtendedClass() {
		class CA {
		}
		class CB extends CA {
		}
		class CC extends CB {
		}

		assertEquals(asSet(CA.class, Object.class), ObjectUtil.getAllSuperTypes(CA.class));
		assertEquals(asSet(CB.class, CA.class, Object.class), ObjectUtil.getAllSuperTypes(CB.class));
		assertEquals(asSet(CC.class, CB.class, CA.class, Object.class), ObjectUtil.getAllSuperTypes(CC.class));
	}

	interface A {
	}

	interface B extends A {
	}

	interface C extends A {
	}

	interface D extends C, B {
	}

	@Test
	public void getSuperTypesOfInterfaces() {
		assertEquals(asSet(A.class), ObjectUtil.getAllSuperTypes(A.class));
		assertEquals(asSet(B.class, A.class), ObjectUtil.getAllSuperTypes(B.class));
		assertEquals(asSet(C.class, A.class), ObjectUtil.getAllSuperTypes(C.class));
		assertEquals(asSet(D.class, C.class, B.class, A.class), ObjectUtil.getAllSuperTypes(D.class));
	}

	private Set<Class<?>> asSet(Class<?>... class_) {
		return new HashSet<Class<?>>(Arrays.asList(class_));
	}

}
