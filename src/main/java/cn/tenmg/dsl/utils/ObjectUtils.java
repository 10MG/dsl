package cn.tenmg.dsl.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象工具类
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.2.2
 */
public abstract class ObjectUtils {

	private static volatile Map<Class<?>, Map<String, Field>> fieldMap = new HashMap<Class<?>, Map<String, Field>>(128);

	private static volatile Map<Class<?>, Map<String, Method>> getMethodMap = new HashMap<Class<?>, Map<String, Method>>(
			128);

	private static volatile Map<Class<?>, Map<String, List<Method>>> setMethodMap = new HashMap<Class<?>, Map<String, List<Method>>>(
			128);

	private static volatile Map<Class<?>, Map<String, Method>> bestSetMethodMap = new HashMap<Class<?>, Map<String, Method>>(
			128);

	private static final String GET = "get", SET = "set", METHOD_RETURNTYPE_SPLITOR = ":";

	private ObjectUtils() {
	}

	/**
	 * 获取指定对象中的指定成员变量
	 * 
	 * @param object
	 *            指定对象
	 * @param fieldName
	 *            指定成员变量
	 * @param <T>
	 *            返回类型
	 * @return 返回指定成员变量的值
	 * @throws Exception
	 *             发生异常
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object object, String fieldName) throws Exception {
		if (object instanceof Map) {
			return (T) ((Map<?, ?>) object).get(fieldName);
		} else {
			Class<?> cls = object.getClass();
			Method method = getGetMethods(cls).get(toMethodName(GET, fieldName));
			if (method == null) {
				return getValue(object, fieldName, getFields(cls));
			} else {
				return (T) method.invoke(object);
			}
		}
	}

	/**
	 * 忽略异常获取指定对象中的指定成员变量。当异常发生时，返回 <code>null</code> 。
	 * 
	 * @param object
	 *            指定对象
	 * @param fieldName
	 *            指定成员变量
	 * @param <T>
	 *            返回类型
	 * @return 返回指定成员变量的值
	 */
	public static <T> T getValueIgnoreException(Object object, String fieldName) {
		T value = null;
		try {
			value = getValue(object, fieldName);
		} catch (Exception e) {
		}
		return value;
	}

	/**
	 * 设置指定对象中的指定成员变量的值。
	 * 
	 * @param object
	 *            指定对象
	 * @param fieldName
	 *            指定成员变量
	 * @param value
	 *            指定的值
	 * @throws Exception
	 *             发生异常
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> void setValue(Object object, String fieldName, T value) throws Exception {
		if (object instanceof Map) {
			((Map) object).put(fieldName, value);
		} else {
			Class<?> cls = object.getClass();
			String methodName = toMethodName(SET, fieldName);
			List<Method> methods = getSetMethods(cls).get(methodName);
			if (methods == null) {
				Field field = getFields(cls).get(fieldName);
				if (field != null) {
					field.set(object, value);
				}
			} else {
				int size = methods.size();
				if (size == 1 || value == null) {
					methods.get(0).invoke(object, value);
				} else {
					Method method = getSetMethod(object, methods, methodName, value);
					if (method == null) {
						setField(cls, object, fieldName, value);
					} else {
						method.invoke(object, value);
					}
				}
			}
		}
	}

	/**
	 * 忽略异常设置指定对象中的指定成员变量的值。当异常发生时，放弃设置该值。
	 * 
	 * @param object
	 *            指定对象
	 * @param fieldName
	 *            指定成员变量
	 * @param value
	 *            指定的值
	 */
	public static <T> void setValueIgnoreException(Object object, String fieldName, T value) {
		try {
			setValue(object, fieldName, value);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T getValue(Object object, String fieldName, Map<String, Field> fields) throws Exception {
		T value = null;
		Field field = fields.get(fieldName);
		if (field != null) {
			if (field.isAccessible()) {
				value = (T) field.get(object);
			} else {
				field.setAccessible(true);
				value = (T) field.get(object);
			}
		}
		return value;
	}

	private static String toMethodName(String prefix, String fieldName) {
		return prefix + (fieldName.length() > 1 ? fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
				: fieldName.toUpperCase());
	}

	/**
	 * 获取指定类的获取方法（getter）集
	 * 
	 * @param cls
	 *            指定类
	 * @return 获取方法（getter）集
	 */
	private static Map<String, Method> getGetMethods(Class<?> cls) {
		Map<String, Method> methods = getMethodMap.get(cls);
		if (methods == null) {
			synchronized (getMethodMap) {
				methods = getMethodMap.get(cls);
				if (methods == null) {
					methods = new HashMap<String, Method>(128);
					Method[] mthds = cls.getMethods();
					String name;
					for (int i = 0, count; i < mthds.length; i++) {
						Method method = mthds[i];
						name = method.getName();
						count = method.getParameterCount();
						if (count == 0 && name.startsWith("get")) {
							methods.put(name, method);
						}
					}
					getMethodMap.put(cls, methods);
				}
			}
		}
		return methods;
	}

	/**
	 * 获取指定类的设置方法（setter）集
	 * 
	 * @param cls
	 *            指定类
	 * @return 设置方法（setter）集
	 */
	private static Map<String, List<Method>> getSetMethods(Class<?> cls) {
		Map<String, List<Method>> methods = setMethodMap.get(cls);
		if (methods == null) {
			synchronized (setMethodMap) {
				methods = setMethodMap.get(cls);
				if (methods == null) {
					methods = new HashMap<String, List<Method>>(128);
					Method[] mthds = cls.getMethods();
					String name;
					List<Method> setMethods;
					for (int i = 0, count; i < mthds.length; i++) {
						Method method = mthds[i];
						name = method.getName();
						count = method.getParameterCount();
						if (count == 1 && name.startsWith("set")) {
							setMethods = methods.get(name);
							if (setMethods == null) {
								setMethods = new ArrayList<Method>();
								methods.put(name, setMethods);
							}
							setMethods.add(method);
						}
					}
					setMethodMap.put(cls, methods);
				}
			}
		}
		return methods;
	}

	/**
	 * 获取指定对象指定属性的最佳匹配设置方法（setter）
	 * 
	 * @param object
	 *            指定对象
	 * @param methods
	 *            对象设置方法（setter）集
	 * @param methodName
	 *            设置方法名
	 * @param value
	 *            设置的值
	 * @return 最佳匹配设置方法（setter）
	 */
	private static <T> Method getSetMethod(Object object, List<Method> methods, String methodName, T value) {
		Method method = null;
		Class<?> cls = object.getClass(), valueType = value.getClass();
		Map<String, Method> bestSetMethods = bestSetMethodMap.get(cls);
		if (bestSetMethods == null) {
			synchronized (bestSetMethodMap) {
				bestSetMethods = bestSetMethodMap.get(cls);
				if (bestSetMethods == null) {
					bestSetMethods = new HashMap<String, Method>(128);
					bestSetMethodMap.put(cls, bestSetMethods);
				}
				String bestKey = String.join(METHOD_RETURNTYPE_SPLITOR, methodName, valueType.getName());
				if (bestSetMethods.containsKey(bestKey)) {
					method = bestSetMethods.get(bestKey);
				} else {
					method = null;
					Method curMethod;
					Class<?> parameterType;
					for (int i = 0, minGeneration = Integer.MAX_VALUE, generation, size = methods
							.size(); i < size; i++) {
						curMethod = methods.get(i);
						parameterType = curMethod.getParameterTypes()[0];
						if (parameterType.equals(valueType)) {
							method = curMethod;
							break;
						} else if (parameterType.isAssignableFrom(valueType)) {
							generation = 1;
							while (!parameterType.equals(valueType)) {
								valueType = valueType.getSuperclass();
								generation++;
							}
							if (generation < minGeneration) {
								method = curMethod;
							}
						}
					}
					bestSetMethods.put(bestKey, method);
				}
			}
		}
		return method;
	}

	private static Map<String, Field> getFields(Class<?> cls) {
		Map<String, Field> fields = fieldMap.get(cls);
		if (fields == null) {
			synchronized (fieldMap) {
				fields = fieldMap.get(cls);
				if (fields == null) {
					fields = new HashMap<String, Field>();
					while (!cls.equals(Object.class)) {
						Field[] declaredFields = cls.getDeclaredFields();
						for (int i = 0; i < declaredFields.length; i++) {
							Field field = declaredFields[i];
							fields.put(field.getName(), field);
						}
						cls = cls.getSuperclass();
					}
					fieldMap.put(cls, fields);
				}
			}
		}
		return fields;
	}

	private static <T> void setField(Class<?> cls, Object object, String fieldName, T value)
			throws IllegalArgumentException, IllegalAccessException {
		Field field = getFields(cls).get(fieldName);
		if (field != null) {
			field.set(object, value);
		}
	}
}
