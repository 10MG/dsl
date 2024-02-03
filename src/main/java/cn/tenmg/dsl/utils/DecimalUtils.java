package cn.tenmg.dsl.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ConcurrentHashMap;

import cn.tenmg.dsl.exception.NumberParseException;

/**
 * 实数工具类
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.0
 */
public abstract class DecimalUtils {

	private static final ConcurrentHashMap<String, NumberFormat> FORMATTERS = new ConcurrentHashMap<String, NumberFormat>();

	/**
	 * 根据模板将指定对象格式化数字字符串
	 * 
	 * @param obj     指定对象
	 * @param pattern 模板
	 * @return 数字字符串
	 */
	public static String format(Object obj, String pattern) {
		return formatter(pattern).format(obj);
	}

	/**
	 * 根据模板将指定对象转换为数字对象
	 * 
	 * @param obj     指定对象
	 * @param pattern 模板
	 * @return 数字对象
	 */

	public static Number parse(Object obj, String pattern) {
		NumberFormat formatter = formatter(pattern);
		if (obj instanceof String) {
			try {
				return formatter.parse((String) obj);
			} catch (Exception e) {
				throw new NumberParseException(StringUtils.concat("An exception occurred when using the pattern: ",
						pattern, " to parse the String: ", obj, " to Number"), e);
			}
		}
		String str;
		try {
			str = formatter.format(obj);
		} catch (Exception e) {
			throw new NumberParseException(StringUtils.concat("An exception occurred when using the pattern: ", pattern,
					" to format the Object: ", obj, " to String"), e);
		}
		try {
			return formatter.parse(str);
		} catch (Exception e) {
			throw new NumberParseException(StringUtils.concat("An exception occurred when using the pattern: ", pattern,
					" to parse the String: ", str, " to Number"), e);
		}
	}

	private static NumberFormat formatter(String pattern) {
		NumberFormat formatter = FORMATTERS.get(pattern);
		if (formatter == null) {
			formatter = new DecimalFormat(pattern);
			FORMATTERS.put(pattern, formatter);
		}
		return formatter;
	}
}