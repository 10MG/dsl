package cn.tenmg.dsl.converter;

import java.util.Calendar;
import java.util.Date;

import cn.tenmg.dsl.utils.DateUtils;
import cn.tenmg.dsl.utils.DecimalUtils;

/**
 * 将参数转换为 {@code java.lang.String} 类型的转换器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public class ToStringParamsConverter extends FormatableParamsConverter<String> {

	@Override
	public String convert(Object value) {
		if (value == null) {
			return null;
		} else {
			String formatter = getFormatter();
			if (formatter != null) {
				if (value instanceof Number) {
					return DecimalUtils.format(value, formatter);
				} else if (value instanceof Date) {
					return DateUtils.format(value, formatter);
				} else if (value instanceof Calendar) {
					return DateUtils.format(((Calendar) value).getTime(), formatter);
				}
			}
		}
		return value.toString();
	}

}
