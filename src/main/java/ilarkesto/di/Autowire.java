/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.di;

import ilarkesto.base.Beans;
import ilarkesto.base.Reflect;
import ilarkesto.core.logging.Log;
import ilarkesto.core.scope.In;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Utility class for autowiering
 * 
 * @author wko
 */
public class Autowire {

	private static Log log = Log.get(Autowire.class);

	/**
	 * Autowire the class <code>clazz</code> with beans provided by <code>beanProvider</code>. Use the given
	 * <code>objectStringMapper</code> to convert from/to strings if required. For each bean provided by
	 * <code>beanProvider</code> a <em>static</em> setter is called on the given class <code>clazz</code>.
	 * 
	 * @param objectStringMapper optional
	 * @return the given <code>clazz</code>
	 */
	public static <T> Class<T> autowireClass(Class<T> clazz, BeanProvider beanProvider,
			ObjectStringMapper objectStringMapper) {
		Set<String> availableBeansNames = beanProvider.beanNames();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (!Modifier.isStatic(methods[i].getModifiers())) continue;
			String methodName = methods[i].getName();
			if (!methodName.startsWith("set")) continue;
			String name = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
			if (availableBeansNames.contains(name)) {
				invokeSetter(null, methods[i], beanProvider.getBean(name), objectStringMapper);
			} else if ("beanProvider".equals(name)) {
				invokeSetter(null, methods[i], beanProvider, objectStringMapper);
			} else {
				// TODO find bean by type
			}
		}
		Class<? extends Object> superclass = clazz.getSuperclass();
		if (superclass != null && !Object.class.equals(superclass))
			autowireClass(superclass, beanProvider, objectStringMapper);
		return clazz;
	}

	/**
	 * Autowire the object <code>bean</code> with beans provided by <code>beanProvider</code>. Use the given
	 * <code>objectStringMapper</code> to convert from/to strings if required. For each bean provided by
	 * <code>beanProvider</code> a setter is called on the given object <code>bean</code>.
	 * 
	 * @param objectStringMapper optional
	 * @return the given <code>bean</code>
	 */
	public static <T> T autowire(T bean, final BeanProvider beanProvider, final ObjectStringMapper objectStringMapper) {
		boolean xxx = bean.getClass().getSimpleName().endsWith("Action");
		if (xxx) log.info("Autowiring:", bean, "->", beanProvider);

		final Set<String> availableBeanNames = beanProvider.beanNames();
		Class beanClass = bean.getClass();

		List<Method> setters = Reflect.getSetters(beanClass);
		if (xxx) log.info("    properties:", setters);
		for (Method setter : setters) {
			String propertyName = Reflect.getPropertyNameFromSetter(setter);
			if (xxx)
				log.info("         propertyName:", propertyName, " setter:", setter, " availableBeanNames.contains():",
					availableBeanNames.contains(propertyName));
			if (setter != null) {
				if (setter.getAnnotation(AutowireHostile.class) != null) continue;
				if (availableBeanNames.contains(propertyName)) {
					Object value = beanProvider.getBean(propertyName);
					invokeSetter(bean, setter, value, objectStringMapper);
				} else if ("beanProvider".equals(propertyName)) {
					invokeSetter(bean, setter, beanProvider, objectStringMapper);
				}
			}
		}

		// TODO create beanless annotation processor in Reflect.java
		Beans.processAnnotations(bean, new Beans.FieldAnnotationHandler() {

			@Override
			public void handle(Annotation annotation, Field field, Object object) {
				if (!(annotation instanceof In)) return;
				String name = field.getName();
				if (!availableBeanNames.contains(name)) return;
				field.setAccessible(true);
				Object value = beanProvider.getBean(name);
				Class paramType = field.getType();
				try {
					if (objectStringMapper != null && value instanceof String
							&& objectStringMapper.isTypeSupported(paramType)) {
						value = objectStringMapper.stringToObject((String) value, paramType);
					} else {
						value = convertType(paramType, value);
					}
					field.set(object, value);
				} catch (Exception ex) {
					String valueStr = value == null ? "<" + value + ">" : value.getClass().getSimpleName() + ": <"
							+ value + ">";
					throw new RuntimeException("Setting field " + object.getClass().getSimpleName() + "." + name
							+ " to " + valueStr + " failed.", ex);
				}
			}
		});

		return bean;
	}

	// --- helper ---

	private static void invokeSetter(Object bean, Method method, Object value, ObjectStringMapper objectStringMapper) {
		try {
			method.invoke(bean, createWriteMethodArguments(method, value, objectStringMapper));
		} catch (Throwable ex) {
			throw new RuntimeException("Invoking setter '" + method.getDeclaringClass().getSimpleName() + "."
					+ method.getName() + "' on '" + bean + "' with '" + value + "' failed.", ex);
		}
	}

	private static Object[] createWriteMethodArguments(Method method, Object value,
			ObjectStringMapper objectStringMapper) throws IllegalAccessException, ClassCastException {
		try {
			if (value != null) {
				Class[] types = method.getParameterTypes();
				if (types != null && types.length > 0) {
					Class paramType = types[0];
					if (!paramType.isAssignableFrom(value.getClass())) {
						if (objectStringMapper != null && value instanceof String
								&& objectStringMapper.isTypeSupported(paramType)) {
							value = objectStringMapper.stringToObject((String) value, paramType);
						} else {
							value = convertType(paramType, value);
						}
					}
				}
			}
			Object[] answer = { value };
			return answer;
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Object convertType(Class newType, Object value) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// try call constructor
		try {
			if (value == null) {
				Constructor constructor = newType.getConstructor();
				return constructor.newInstance();
			} else {
				Class[] types = { value.getClass() };
				Constructor constructor = newType.getConstructor(types);
				Object[] arguments = { value };
				return constructor.newInstance(arguments);
			}
		} catch (NoSuchMethodException e) {
			// try using the transformers
			ITransformer transformer = getTypeTransformer(newType);
			if (transformer != null) { return transformer.transform(value); }
			return value;
		}
	}

	private static ITransformer getTypeTransformer(Class aType) {
		return (ITransformer) defaultTransformers.get(aType);
	}

	// --- dependencies ---

	public static HashMap defaultTransformers = new HashMap();

	static {
		defaultTransformers.put(Boolean.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return Boolean.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Character.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return new Character(input.toString().charAt(0));
			}
		});
		defaultTransformers.put(Byte.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return Byte.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Short.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return Short.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Integer.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return Integer.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Long.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return Long.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Float.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return Float.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Double.TYPE, new ITransformer() {

			@Override
			public Object transform(Object input) {
				return Double.valueOf(input.toString());
			}
		});
	}

	public interface ITransformer {

		public Object transform(Object input);

	}

}
