package cn.tenmg.dsl.utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数工具类。已废弃，即将在后续版本移除，{@code getParam} 方法请使用 {@code ObjectUtils.getValue} 或 {@code ObjectUtils.getValueIgnoreException} 方法替换。
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.2
 *
 */
@Deprecated
public abstract class ParamsUtils {

	private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[[^\\]]+\\]");

	/**
	 * 获取参数集中的参数值。参数名支持使用“key.name”访问参数值的属性值，层级数不限，支持使用“[*]”访问数组值，维数不限，“key.name”和“[*]”也可以配合使用
	 * 
	 * @param params
	 *            参数集
	 * @param name
	 *            参数名
	 * @return 如果参数集的参数存在则，返回它的值；否则，返回null
	 */
	public static Object getParam(Map<?, ?> params, String name) {
		Object value = params.get(name);
		if (value == null) {
			if (name.contains(".")) {// 访问Bean或者Map属性
				String[] names = name.split("\\.");
				name = names[0];
				value = params.get(name);
				if (value == null) {// 如果类似“key.name[*]……”形式的，可能是访问数组的某一项值或者是访问Map对象的属性值。如果是，则获取数组的某一项值或者Map对象的某个属性值
					return getMaybeArrayOrMapValue(params, name);
				} else {
					for (int i = 1; i < names.length; i++) {
						name = names[i];
						value = ObjectUtils.getValueIgnoreException(value, name);// 获取对象属性
						if (value == null) {// 可能是数组
							Matcher m = ARRAY_PATTERN.matcher(name);
							if (m.find()) {// 含有数组访问符“[]”
								value = ObjectUtils.getValueIgnoreException(value,
										name.substring(0, name.indexOf("[")));// 获取数组对象
								if (value == null) {// 数组对象为null，返回null
									return null;
								} else {// 否则，获取数组的值
									value = getArrayOrMapValue(value, params, m);
								}
							}
							return value;
						}
					}
					return value;
				}
			} else {// 如果类似“key[*]……”形式的，可能是访问数组的某一项值或者是访问Map对象的属性值。如果是，则获取数组的某一项值或者Map对象的某个属性值
				return getMaybeArrayOrMapValue(params, name);
			}
		} else {
			return value;
		}
	}

	/**
	 * 获取可能通过“[*]”方式访问的数组某一项值或Map对象属性值
	 * 
	 * @param params
	 *            参数集
	 * @param name
	 *            参数名
	 * @return 如果含有“[*]”符号，则获取数组的值或者Map对象的属性值；否则返回null
	 */
	private static final Object getMaybeArrayOrMapValue(Map<?, ?> params, String name) {
		Object value = null;
		Matcher m = ARRAY_PATTERN.matcher(name);
		if (m.find()) {
			value = params.get(name.substring(0, name.indexOf("[")));
			if (value == null) {
				return null;
			} else {// 继续获取下一维数组的值
				value = getArrayOrMapValue(value, params, m);
			}
		}
		return value;
	}

	private static final Object getArrayOrMapValue(Object value, Map<?, ?> params, Matcher m) {
		value = getArrayOrMapValue(value, params, m.group());
		while (value != null && m.find()) {
			value = getArrayOrMapValue(value, params, m.group());
		}
		return value;
	}

	private static final Object getArrayOrMapValue(Object value, Map<?, ?> params, String group) {
		String name = group.substring(1, group.length() - 1);
		return getValue(value, params, name);
	}

	private static final Object getValue(Object value, Map<?, ?> params, String name) {
		Object v = params.get(name);
		String real = name;
		if (v != null) {
			real = v.toString();
		}
		if (value instanceof Map) {
			return ((Map<?, ?>) value).get(real);
		} else if (value instanceof List) {
			return ((List<?>) value).get(Integer.valueOf(real));
		} else if (value instanceof Object[]) {
			return ((Object[]) value)[Integer.valueOf(real)];
		} else if (value instanceof LinkedHashSet) {
			return ((LinkedHashSet<?>) value).toArray()[Integer.valueOf(real)];
		} else {
			return null;
		}
	}
}
