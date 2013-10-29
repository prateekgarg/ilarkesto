/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.base;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Beans {

	// -------------------
	// --- annotations ---
	// -------------------

	public static void processAnnotations(Object object, FieldAnnotationHandler handler) {
		processAnnotations(object, object.getClass(), handler);
	}

	public static void processAnnotations(Object object, Class<?> clazz, FieldAnnotationHandler handler) {
		Field[] fields = clazz.getDeclaredFields();
		for (int i = fields.length - 1; i >= 0; i--) {
			Annotation[] annotations = fields[i].getAnnotations();
			for (int j = 0; j < annotations.length; j++) {
				handler.handle(annotations[j], fields[i], object);
			}
		}

		Class<?> supa = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			processAnnotations(object, interfaces[i], handler);
		}
		if (supa != null && !supa.equals(Object.class)) processAnnotations(object, supa, handler);
	}

	public static void processAnnotations(Object object, MethodAnnotationHandler handler) {
		processAnnotations(object, object.getClass(), handler);
	}

	public static void processAnnotations(Object object, Class<?> clazz, MethodAnnotationHandler handler) {
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = methods.length - 1; i >= 0; i--) {
			Annotation[] annotations = methods[i].getAnnotations();
			for (int j = 0; j < annotations.length; j++) {
				handler.handle(annotations[j], methods[i], object);
			}
		}

		Class<?> supa = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			processAnnotations(object, interfaces[i], handler);
		}
		if (supa != null && !supa.equals(Object.class)) processAnnotations(object, supa, handler);
	}

	public static void processAnnotations(Object object, PropertyMethodAnnotationHandler handler, boolean getter,
			boolean setter) {
		processAnnotations(object, object.getClass(), handler, getter, setter);
	}

	public static void processAnnotations(Object object, Class<?> clazz, PropertyMethodAnnotationHandler handler,
			boolean getter, boolean setter) {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException ex) {
			throw new RuntimeException(ex);
		}
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
			if (getter) {
				Method method = propertyDescriptor.getReadMethod();
				if (method != null) {
					Annotation[] annotations = method.getAnnotations();
					for (int j = 0; j < annotations.length; j++) {
						handler.handle(annotations[j], propertyDescriptor, object);
					}
				}
			}
			if (setter) {
				Method method = propertyDescriptor.getWriteMethod();
				if (method != null) {
					Annotation[] annotations = method.getAnnotations();
					for (int j = 0; j < annotations.length; j++) {
						handler.handle(annotations[j], propertyDescriptor, object);
					}
				}
			}
		}
		Class<?> supa = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			processAnnotations(object, interfaces[i], handler, getter, setter);
		}
		if (supa != null && !supa.equals(Object.class)) processAnnotations(object, supa, handler, getter, setter);
	}

	public static interface MethodAnnotationHandler {

		public void handle(Annotation annotation, Method method, Object object);

	}

	public static interface PropertyMethodAnnotationHandler {

		public void handle(Annotation annotation, PropertyDescriptor property, Object object);

	}

	public static interface FieldAnnotationHandler {

		public void handle(Annotation annotation, Field field, Object object);

	}

}
