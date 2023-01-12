package cn.tenmg.dsl.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.tenmg.dsl.exception.DateFormatException;
import cn.tenmg.dsl.exception.DateParseException;

/**
 * 日期工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public abstract class DateUtils {
	/**
	 * 根据模板将制定对象格式化为日期字符串
	 * 
	 * @param obj
	 *            指定对象
	 * @param pattern
	 *            模板
	 * @return 日期字符串
	 */
	public static String format(Object obj, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(obj);
	}

	/**
	 * 根据模板将指定对象转换为日期对象
	 * 
	 * @param obj
	 *            指定对象
	 * @param pattern
	 *            模板
	 * @return 日期对象
	 */
	public static Date parse(Object obj, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (obj instanceof String) {
			try {
				return sdf.parse((String) obj);
			} catch (Exception e) {
				throw new DateParseException(StringUtils.concat("An exception occurred when using the pattern: ",
						pattern, " to parse the String: ", obj, " to Date"), e);
			}
		}
		String str;
		try {
			str = sdf.format(obj);
		} catch (Exception e) {
			throw new DateFormatException(StringUtils.concat("An exception occurred when using the pattern: ", pattern,
					" to format the Object: ", obj, " to String"), e);
		}
		try {
			return sdf.parse(str);
		} catch (Exception e) {
			throw new DateParseException(StringUtils.concat("An exception occurred when using the pattern: ", pattern,
					" to parse the String: ", str, " to Date"), e);
		}
	}
	
	/**
	 * 向指定日期加上或减去指定天数，正数为加，负数为减
	 * 
	 * @param date
	 *            指定日期
	 * @param days
	 *            指定天数
	 * @return 返回新的日期
	 */
	public static Date addDays(Date date, int days) {
		return add(date, Calendar.DATE, days);
	}

	/**
	 * 向指定日期加上或减去指定月数，正数为加，负数为减
	 * 
	 * @param date
	 *            指定日期
	 * @param months
	 *            指定月数
	 * @return 返回新的日期
	 */
	public static Date addMonths(Date date, int months) {
		return add(date, Calendar.MONTH, months);
	}

	/**
	 * 向指定日期加上或减去指定年数，正数为加，负数为减
	 * 
	 * @param date
	 *            指定日期
	 * @param years
	 *            指定年数
	 * @return 返回新的日期
	 */
	public static Date addYears(Date date, int years) {
		return add(date, Calendar.YEAR, years);
	}

	/**
	 * 截断日期
	 * 
	 * @param date
	 *            日期
	 * @param fmt
	 *            截断格式 SECOND/MINUTE/HOUR/DAY/MONTH/YEAR，不区分大小写
	 * @return 返回新的日期
	 */
	public static Date trunc(Date date, String fmt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		if ("SECOND".equalsIgnoreCase(fmt)) {
			return cal.getTime();
		}
		cal.set(Calendar.SECOND, 0);
		if ("MINUTE".equalsIgnoreCase(fmt)) {
			return cal.getTime();
		}
		cal.set(Calendar.MINUTE, 0);
		if ("HOUR".equalsIgnoreCase(fmt)) {
			return cal.getTime();
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		if ("DAY".equalsIgnoreCase(fmt)) {
			return cal.getTime();
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if ("MONTH".equalsIgnoreCase(fmt)) {
			return cal.getTime();
		}
		cal.set(Calendar.MONTH, 0);
		if ("YEAR".equalsIgnoreCase(fmt)) {
			return cal.getTime();
		}
		return cal.getTime();
	}

	/**
	 * 向指定日期加上指定单位的指定量
	 * 
	 * @param date
	 *            指定日期
	 * @param field
	 *            指定单位
	 * @param amount
	 *            指定量
	 * @return 返回新的日期
	 */
	public static Date add(Date date, int field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);
		return cal.getTime();
	}
}