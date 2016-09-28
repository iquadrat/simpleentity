/*
 * Created on Oct 6, 2007
 *
 */
package com.simpleentity.util.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.simpleentity.util.ClassUtil;

public class ClassUtilTest {

	@Test
	public void getSuperTypesOfObject() {
		assertEquals(asSet(Object.class), ClassUtil.getAllSuperTypes(Object.class));
	}

	@Test
	public void getSuperTypesOfExtendedClass() {
		class CA {
		}
		class CB extends CA {
		}
		class CC extends CB {
		}

		assertEquals(asSet(CA.class, Object.class), ClassUtil.getAllSuperTypes(CA.class));
		assertEquals(asSet(CB.class, CA.class, Object.class), ClassUtil.getAllSuperTypes(CB.class));
		assertEquals(asSet(CC.class, CB.class, CA.class, Object.class), ClassUtil.getAllSuperTypes(CC.class));
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
		assertEquals(asSet(A.class), ClassUtil.getAllSuperTypes(A.class));
		assertEquals(asSet(B.class, A.class), ClassUtil.getAllSuperTypes(B.class));
		assertEquals(asSet(C.class, A.class), ClassUtil.getAllSuperTypes(C.class));
		assertEquals(asSet(D.class, C.class, B.class, A.class), ClassUtil.getAllSuperTypes(D.class));
	}

	private Set<Class<?>> asSet(Class<?>... class_) {
		return new HashSet<Class<?>>(Arrays.asList(class_));
	}

}
