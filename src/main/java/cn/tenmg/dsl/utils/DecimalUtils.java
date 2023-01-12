package cn.tenmg.dsl.utils;

import java.text.DecimalFormat;

import cn.tenmg.dsl.exception.DateFormatException;
import cn.tenmg.dsl.exception.DateParseException;
import cn.tenmg.dsl.exception.NumberParseException;

/**
 * 实数工具类
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.0
 */
public abstract class DecimalUtils {
	/**
	 * 根据模板将指定对象格式化数字字符串
	 * 
	 * @param obj
	 *            指定对象
	 * @param pattern
	 *            模板
	 * @return 数字字符串
	 */
	public static String format(Object obj, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(obj);
	}

	/**
	 * 根据模板将指定对象转换为数字对象
	 * 
	 * @param obj
	 *            指定对象
	 * @param pattern
	 *            模板
	 * @return 数字对象
	 */

	public static Number parse(Object obj, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		if (obj instanceof String) {
			try {
				return df.parse((String) obj);
			} catch (Exception e) {
				throw new NumberParseException(StringUtils.concat("An exception occurred when using the pattern: ",
						pattern, " to parse the String: ", obj, " to Number"), e);
			}
		}
		String str;
		try {
			str = df.format(obj);
		} catch (Exception e) {
			throw new DateFormatException(StringUtils.concat("An exception occurred when using the pattern: ", pattern,
					" to format the Object: ", obj, " to String"), e);
		}
		try {
			return df.parse(str);
		} catch (Exception e) {
			throw new DateParseException(StringUtils.concat("An exception occurred when using the pattern: ", pattern,
					" to parse the String: ", str, " to Number"), e);
		}
	}
}