/*
 * Created on 05.07.2007
 *
 */
package com.simpleentity.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class TypeUtil {

	public static boolean hasAnnotation(Class<?> class_, Class<? extends Annotation> annotation) {
		for(Class<?> supertype: getAllSuperTypes(class_)) {
			if (supertype.getAnnotation(annotation) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return all super classes and interfaces of the given class including the
	 *         class itself
	 */
	public static Set<Class<?>> getAllSuperTypes(Class<?> class_) {
		Set<Class<?>> result = new HashSet<Class<?>>();
		addTypes(result, class_);
		return result;
	}

	private static void addTypes(Set<Class<?>> result, @CheckForNull Class<?> class_) {
		if (class_ == null || !result.add(class_))
			return;
		addTypes(result, class_.getSuperclass());
		for (Class<?> interf : class_.getInterfaces()) {
			addTypes(result, interf);
		}
	}

	/**
	 * Collects all non-static public, protected, package and private fields of
	 * the given class including the inherited ones. Sorted first by class
	 * hierarchy, then by field names.
	 *
	 * TODO test if this works with synthetic fields
	 *
	 * @return list of all fields
	 */
	public static List<Field> getAllFields(Class<?> class_) {
		List<Field> result = new ArrayList<Field>();

		for (Class<?> c : getAllSuperTypes(class_)) {

			Field[] fields = c.getDeclaredFields();
			Arrays.sort(fields, new Comparator<Field>() {
				@Override
				public int compare(Field o1, Field o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			for (Field field : fields) {
				// skip static fields
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				result.add(field);
			}

		}

		return result;
	}

	public static Class<?> getArrayElementType(Class<?> array) {
		Class<?> cl = array.getComponentType();
		while (cl.isArray()) {
			cl = cl.getComponentType();
		}
		return cl;
	}

	public static int getArrayDimension(Class<?> array) {
		int dimension = 1;
		Class<?> cl = array.getComponentType();
		while (cl.isArray()) {
			cl = cl.getComponentType();
			dimension++;
		}
		return dimension;
	}

	private TypeUtil() {
		// utility class
	}

}
