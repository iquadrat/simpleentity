package com.simpleentity.serialize2;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

import com.simpleentity.util.ClassUtil;

public class SerializationUtil {

	public static Field findField(Class<?> class_, String id) throws NoSuchElementException {
		for(Class<?> superClass: ClassUtil.getAllSuperTypes(class_)) {
			try {
				return superClass.getDeclaredField(id);
			} catch (NoSuchFieldException e) {
				continue;
			}
		}
		throw new NoSuchElementException(id);
	}

	private SerializationUtil() {}
}
