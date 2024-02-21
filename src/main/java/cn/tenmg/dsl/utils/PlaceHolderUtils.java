package cn.tenmg.dsl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 占位符工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.10
 */
public abstract class PlaceHolderUtils {

	private static final char PLACEHOLDER_PREFIX[] = { '$', '{' }, PLACEHOLDER_SUFFIX = '}';

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[\\s\\S]*\\$\\{[^}]+\\}[\\s\\S]*");

	private static final String EMPTY_STRING = "", NULL_STRING = "null";

	private PlaceHolderUtils() {
	}

	/**
	 * 将模板字符串中占位符替换为指定的参数
	 * 
	 * @param tpl    模板字符串
	 * @param params 参数集（分别列出参数名和参数值）
	 * @return 返回将模板字符串中占位符替换为指定的参数后的字符串
	 */
	public static String replace(String tpl, CharSequence... params) {
		return replace(ReplaceStrategy.DONOTHING_WHEN_NULL_OR_NOT_EXISTS, tpl, params);
	}

	/**
	 * 将模板字符串中占位符替换为指定的参数
	 * 
	 * @param tpl    模板字符串
	 * @param params 参数集
	 * @return 返回将模板字符串中占位符替换为指定的参数后的字符串
	 */
	public static String replace(String tpl, Map<?, ?> params) {
		return replace(ReplaceStrategy.DONOTHING_WHEN_NULL_OR_NOT_EXISTS, tpl, params);
	}

	/**
	 * 将模板字符串中占位符替换为指定的参数
	 * 
	 * @param replaceStrategy 替换策略
	 * @param tpl             模板字符串
	 * @param params          参数集（分别列出参数名和参数值）
	 * @return 返回将模板字符串中占位符替换为指定的参数后的字符串
	 */
	public static String replace(ReplaceStrategy replaceStrategy, String tpl, CharSequence... params) {
		Map<CharSequence, CharSequence> map;
		if (params != null) {
			map = new HashMap<CharSequence, CharSequence>(params.length / 2);
			for (int i = 0; i < params.length; i += 2) {
				map.put(params[i], params[i + 1]);
			}
		} else {
			map = new HashMap<CharSequence, CharSequence>();
		}
		return replace(replaceStrategy, tpl, map);
	}

	/**
	 * 将模板字符串中占位符替换为指定的参数
	 * 
	 * @param tpl             模板字符串
	 * @param replaceStrategy 替换策略
	 * @param params          参数集
	 * @return 返回将模板字符串中占位符替换为指定的参数后的字符串
	 */
	public static String replace(ReplaceStrategy replaceStrategy, String tpl, Map<?, ?> params) {
		if (StringUtils.isBlank(tpl)) {
			return tpl;
		} else {
			if (params == null) {
				params = new HashMap<>();
			}
			int len = tpl.length(), i = 0, deep = 0;
			char b = DSLUtils.BLANK_SPACE, c;
			boolean isPlaceHolder = false; // 是否在占位符区域
			StringBuilder sb = new StringBuilder();
			HashMap<Integer, StringBuilder> placeHolderKey = new HashMap<Integer, StringBuilder>();
			while (i < len) {
				c = tpl.charAt(i);
				if (isPlaceHolder) {// 当前字符处于占位符区域
					if (isPlaceHolderEnd(c)) {// 结束当前占位符区域
						if (deep < 2) {// 已离开占位符区域
							isPlaceHolder = false;
							replace(replaceStrategy, sb, placeHolderKey.get(deep).toString(), params);
						} else {
							replace(replaceStrategy, placeHolderKey.get(deep - 1), placeHolderKey.get(deep).toString(),
									params);
						}
						deep--;
					} else if (isPlaceHolderBegin(b, c)) {// 嵌套了新的一层占位符
						placeHolderKey.get(deep).deleteCharAt(placeHolderKey.get(deep).length() - 1);
						deep++;
						placeHolderKey.put(deep, new StringBuilder());
					} else {
						placeHolderKey.get(deep).append(c);
					}
				} else {// 当前字符占位符区域
					if (isPlaceHolderBegin(b, c)) {
						isPlaceHolder = true;
						sb.deleteCharAt(sb.length() - 1);
						deep++;
						placeHolderKey.put(deep, new StringBuilder());
					} else {
						sb.append(c);
					}
				}
				b = c;
				i++;
			}
			return sb.toString();
		}
	}

	/**
	 * 替换策略
	 * 
	 * @author June wjzhao@aliyun.com
	 * @since 1.4.4
	 */
	public enum ReplaceStrategy {
		/**
		 * 当参数不存在或者参数值为 {@code null} 时，不替换
		 */
		DONOTHING_WHEN_NULL_OR_NOT_EXISTS,

		/**
		 * 当参数不存在或者参数值为 {@code null} 时，替换为不含字符的空白字符串
		 */
		EMPTY_STRING_WHEN_NULL_OR_NOT_EXISTS,
		/**
		 * 当参数不存在或者参数值为 {@code null} 时，替换为字符串“null”
		 */
		NULL_STRING_WHEN_NULL_OR_NOT_EXISTS
	}

	/**
	 * 将占位符的键替换为指定参数的值，并将该值拼接到可变字符序列中
	 * 
	 * @param replaceStrategy 替换策略
	 * @param sb              可变字符序列中
	 * @param key             占位符键
	 * @param params          参数集
	 */
	private static void replace(ReplaceStrategy replaceStrategy, StringBuilder sb, String key, Map<?, ?> params) {
		if (PLACEHOLDER_PATTERN.matcher(key).matches()) {
			sb.append(replace(key, params)).toString();
		} else {
			String keys[] = key.split(":", 2), name = keys[0], defaultValue;
			if (keys.length > 1) {
				defaultValue = keys[1];
			} else {
				if (ReplaceStrategy.EMPTY_STRING_WHEN_NULL_OR_NOT_EXISTS.equals(replaceStrategy)) {
					defaultValue = EMPTY_STRING;
				} else if (ReplaceStrategy.NULL_STRING_WHEN_NULL_OR_NOT_EXISTS.equals(replaceStrategy)) {
					defaultValue = NULL_STRING;
				} else {
					defaultValue = StringUtils.concat(PLACEHOLDER_PREFIX[0], PLACEHOLDER_PREFIX[1], key,
							PLACEHOLDER_SUFFIX);
				}
			}
			Object value = ObjectUtils.getValueIgnoreException(params, name);
			if (value == null) {
				sb.append(defaultValue);
			} else {
				sb.append(value.toString());
			}
		}
	}

	/**
	 * 根据指定的两个前后相邻字符b和c，判断其是否为占位符区的开始位置
	 * 
	 * @param b 前一个字符b
	 * @param c 当前字符c
	 * @return
	 */
	private static boolean isPlaceHolderBegin(char b, char c) {
		return b == PLACEHOLDER_PREFIX[0] && c == PLACEHOLDER_PREFIX[1];
	}

	/**
	 * 根据指定字符c，判断其是否为占位符区的结束位置
	 * 
	 * @param c 指定字符
	 * @return
	 */
	private static boolean isPlaceHolderEnd(char c) {
		return c == PLACEHOLDER_SUFFIX;
	}

}
