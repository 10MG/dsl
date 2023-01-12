package cn.tenmg.dsl.converter;

import java.util.Date;

import cn.tenmg.dsl.utils.DateUtils;

/**
 * 将参数转为 {@code java.util.Date} 类型的转换器
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.0
 */
public class ToDateParamsConverter extends FormatableParamsConverter<Date> {

	@Override
	public Date convert(Object value) {
		if (value == null) {
			return null;
		}
		return DateUtils.parse(value, getFormatter());
	}

}
